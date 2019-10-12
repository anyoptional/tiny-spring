/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/9/26.
 *  All rights reserved.
 */

package com.archer.spring.factory.support;

import com.archer.spring.factory.BeansException;
import com.archer.spring.factory.PropertyEditorRegistrar;
import com.archer.spring.factory.config.ConfigurableBeanFactory;
import com.archer.spring.factory.FactoryBean;
import com.archer.spring.factory.config.BeanDefinition;
import com.archer.spring.factory.config.BeanPostProcessor;
import com.archer.spring.utils.ClassUtils;

import java.beans.PropertyEditor;
import java.util.*;

/**
 * BeanFactory模板类。
 * 其中主要模板方法是 createBean 和 destroySingleton。
 * 同时因为BeanDefinition是通过BeanDefinitionRegistry来注册
 * 管理的，因此ConfigurableBeanFactory.getBeanDefinition方法
 * 的也需要子类来填充具体实现。
 */
public abstract class AbstractBeanFactory implements ConfigurableBeanFactory {

    /// MARK - Properties

    // 单实例bean的缓存，需保证其线程安全
    private final Map<String, Object> singletonMap = Collections.synchronizedMap(new HashMap<>());

    // 保存自定义的PropertyEditor
    private final Map<Class<?>, PropertyEditor> customEditors = new HashMap<>();

    // 保存所有的bean后置处理器
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    // 保存所有的PropertyEditorRegistrar
    private final Set<PropertyEditorRegistrar> propertyEditorRegistrars = new HashSet<>();

    /// MARK - Getters & Setters

    public Map<Class<?>, PropertyEditor> getCustomEditors() {
        return Collections.unmodifiableMap(customEditors);
    }

    public List<BeanPostProcessor> getBeanPostProcessors() {
        return Collections.unmodifiableList(beanPostProcessors);
    }

    public Set<PropertyEditorRegistrar> getPropertyEditorRegistrars() {
        return propertyEditorRegistrars;
    }

    /// MARK - Initializers

    public AbstractBeanFactory() { }

    /// MARK - Public methods

    /**
     * 添加一个singleton bean到缓存中。
     * 如果两个bean之间存在循环依赖，只要遵循
     * 先创建再缓存后设置(依赖)的步骤，就可以解决。
     */
    public void addSingleton(String beanName, Object singletonBean) {
        singletonMap.put(beanName, singletonBean);
    }

    /// MARK - BeanFactory

    @Override
    public Object getBean(String beanName) throws BeansException {
        // 处理一下是FactoryBean的情况
        String resolvedBeanName = getResolvedBeanName(beanName);
        // 查一下缓存，看看是否已经创建了
        Object bean = singletonMap.get(resolvedBeanName);
        // 缓存命中
        if (bean != null) {
            // 可能是FactoryBean，根据请求的是
            // FactoryBean本身还是其生产的对象，要分别处理
            return getBeanFromSharedInstance(beanName, bean);
        }
        // 缓存未命中，此时就要通过BeanDefinition中保存的相关信息去创建bean了
        // 在我们的实现中不支持父子bean工厂，因此没有额外的BeanDefinition
        // 合并操作，实现起来要简单很多。
        BeanDefinition mbd = getBeanDefinition(resolvedBeanName);
        if (mbd != null) {
            if (mbd.isSingleton()) {
                // "先检查后执行"这类操作基本上都不是线程安全的
                // Collection.synchronizedMap是以自身作为锁，这里也用同一把锁来保护
                synchronized (singletonMap) {
                    // getBean()方法并没有被整个同步住，因此这里再检查一下
                    bean = singletonMap.get(resolvedBeanName);
                    // 确实没有才创建
                    if (bean == null) {
                        System.out.println("正在创建singleton bean[" + resolvedBeanName + "]");
                        bean = createBean(resolvedBeanName, mbd);
                        // 加入缓存
                        addSingleton(resolvedBeanName, bean);
                    }
                }
                // 这个bean可能是FactoryBean
                return getBeanFromSharedInstance(beanName, bean);
            } else {
                System.out.println("正在创建prototype bean[" + beanName + "]");
                return createBean(beanName, mbd);
            }
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(String beanName, Class<T> requiredType) throws BeansException {
        Object bean = getBean(beanName);
        if (!requiredType.isAssignableFrom(bean.getClass())) {
            throw new BeansException("[" + bean + "]不是" + requiredType.getName() + "类型");
        }
        return (T) bean;
    }

    @Override
    public boolean containsBean(String beanName) {
        String resolvedBeanName = getResolvedBeanName(beanName);
        // 先看看缓存是否命中
        if (singletonMap.containsKey(resolvedBeanName)) {
            return true;
        }
        // 再查询一下是否有对应的BeanDefinition
//        return getBeanDefinition(resolvedBeanName) != null;
        return containsBeanDefinition(resolvedBeanName);
    }

    @Override
    public boolean isSingleton(String beanName) throws BeansException {
        String resolvedBeanName = getResolvedBeanName(beanName);
        // 先看看缓存是否命中
        if (singletonMap.containsKey(resolvedBeanName)) {
            return true;
        }
        // 再查询一下对应的BeanDefinition
        // 这里没有去考虑是FactoryBean的情况
        BeanDefinition mbd = getBeanDefinition(resolvedBeanName);
        if (mbd != null) {
            return mbd.isSingleton();
        }
        return false;
    }

    /// MARK - ConfigurableBeanFactory

    @Override
    public void addPropertyEditorRegistrar(PropertyEditorRegistrar registrar) {
        propertyEditorRegistrars.add(registrar);
    }

    @Override
    public void registerCustomEditor(Class<?> requiredType, Class<? extends PropertyEditor> propertyEditorClass) {
        PropertyEditor propertyEditor = ClassUtils.instantiateClass(propertyEditorClass);
        customEditors.put(requiredType, propertyEditor);
    }

    @Override
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        beanPostProcessors.add(beanPostProcessor);
    }

    @Override
    public void registerSingleton(String beanName, Object singletonObject) throws BeansException {
        if (isFactoryBean(beanName)) {
            throw new BeansException("不能直接注册以&开头的bean");
        }
        synchronized (singletonMap) {
            if (singletonMap.containsKey(beanName)) {
                throw new BeansException("已经注册有名称为[" + beanName + "]的bean了");
            }
            addSingleton(beanName, singletonObject);
        }
    }

    @Override
    public void destroySingletons() {
        synchronized (singletonMap) {
            Set<String> keySet = singletonMap.keySet();
            for (String key : keySet) {
                destroySingleton(key, singletonMap.get(key));
            }
            singletonMap.clear();
        }
    }

    @Override
    public abstract BeanDefinition getBeanDefinition(String beanName) throws BeansException;

    /// MARK - Template methods

    /**
     * 创建一个bean。
     */
    public abstract Object createBean(String beanName, BeanDefinition mbd);

    /**
     * 销毁一个单例bean。
     */
    public abstract void destroySingleton(String beanName, Object singletonObject);

    /**
     * 是否包含beanName对应的BeanDefinition
     */
    public abstract boolean containsBeanDefinition(String beanName);


    /// MARK - Public

    public String[] getSingletonNames(Class<?> type) {
        Set<String> matches = new HashSet<>();
        for (String name : singletonMap.keySet()) {
            Object sharedObj = singletonMap.get(name);
            if (type == null || type.isAssignableFrom(sharedObj.getClass())) {
                matches.add(name);
            }
        }
        return matches.toArray(new String[0]);
    }

    /// MARK - Internal

    /**
     * 是否是FactoryBean
     */
    private boolean isFactoryBean(String beanName) {
        return beanName.startsWith(FactoryBean.FACTORY_BEAN_PREFIX);
    }

    /**
     * 剔除beanName的&前缀。
     */
    private String getResolvedBeanName(String beanName) {
        Objects.requireNonNull(beanName, "bean name不能为空。");
        // FactoryBean本身是单例，是会进入singletonMap的
        // 但它生产的对象却不会，因此在实现FactoryBean的getObject()方法时
        // 需要保证返回的是单例。
        if (beanName.startsWith(FactoryBean.FACTORY_BEAN_PREFIX)) {
            beanName = beanName.substring(FactoryBean.FACTORY_BEAN_PREFIX.length());
        }
        return beanName;
    }

    /**
     * 根据sharedInstance的类型和beanName来确定最终返回的对象
     */
    private Object getBeanFromSharedInstance(String beanName, Object sharedInstance) {
        boolean isPrefixed = isFactoryBean(beanName);
        boolean isInstanceOfFactoryBean = sharedInstance instanceof FactoryBean;
        // 先校验一下，根据约定拥有&前缀的bean name，其指向的是一个FactoryBean
        if (isPrefixed && !isInstanceOfFactoryBean) {
            throw new BeansException("[" + sharedInstance + "]不是一个FactoryBean");
        }
        // 是FactoryBean的话要分一下情况
        if (isInstanceOfFactoryBean) {
            // 请求FactoryBean生产的对象
           if (!isPrefixed) {
               try {
                 sharedInstance = ((FactoryBean) sharedInstance).getObject();
               } catch (Exception e) {
                   throw new BeansException("[&" + beanName + "]对应的FactoryBean创建对象时抛出了异常");
               }
               if (sharedInstance == null) {
                   throw new BeansException("无法从[&" + beanName + "]对应的FactoryBean中生产出对象");
               }
           }
        }
        // 不是FactoryBean或者请求FactoryBean
        // 本身，这种的话就直接返回了
        return sharedInstance;
    }
}
