/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/9/26.
 *  All rights reserved.
 */

package com.archer.spring.factory.support;

import com.archer.spring.factory.*;
import com.archer.spring.factory.config.AutowireCapableBeanFactory;
import com.archer.spring.factory.config.BeanDefinition;
import com.archer.spring.factory.config.BeanPostProcessor;
import com.archer.spring.utils.ClassUtils;
import com.sun.istack.internal.NotNull;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.reflect.Constructor;
import java.util.*;

/**
 * BeanFactory模板类，实现了AutowireCapableBeanFactory接口。
 */
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory {

    /// MARK - Initializers

    public AbstractAutowireCapableBeanFactory() { }

    /// MARK - AbstractBeanFactory

    @Override
    public abstract BeanDefinition getBeanDefinition(String beanName) throws BeansException;

    @Override
    public Object createBean(String beanName, BeanDefinition mbd) {
        // 确保依赖的bean先得到初始化
        String[] dependsOnBeanNames = mbd.getDependsOn();
        if (dependsOnBeanNames != null) {
            for (String dependsOnBeanName : dependsOnBeanNames) {
                getBean(dependsOnBeanName);
            }
        }

        BeanWrapper beanWrapper = null;
        // 是构造函数注入或持有构造函数的参数
        if (mbd.getResolvedAutowireMode() == BeanDefinition.AUTOWIRE_CONSTRUCTOR ||
                mbd.hasConstructorArgumentValues()) {
            beanWrapper = autowireConstructor(beanName, mbd);
        } else {
            // 不是的话就走普通的解析赋值路线
            beanWrapper = new BeanWrapper(mbd.getBeanClass());
            registerPropertyEditors(beanWrapper);
        }
        Object bean = beanWrapper.getWrappedInstance();

        // 提前缓存单实例bean
        // 只要保证都是先创建后赋值，就可以解决循环依赖
        if (mbd.isSingleton()) {
            addSingleton(beanName, bean);
        }

        // 给属性赋值
        populateBean(beanName, mbd, beanWrapper);

        // 生命周期回调
        try {
            if (bean instanceof BeanNameAware) {
                ((BeanNameAware) bean).setBeanName(beanName);
            }
            if (bean instanceof BeanFactoryAware) {
                ((BeanFactoryAware) bean).setBeanFactory(this);
            }
            bean = applyBeanPostProcessorsBeforeInitialization(bean, beanName);
            invokeInitMethods(bean, mbd);
            bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
        } catch (Exception e) {
            throw new BeansException("调用生命周期函数失败", e);
        }

        return bean;
    }

    @Override
    public void destroySingleton(String beanName, Object singletonObject) {
        BeanDefinition mbd = getBeanDefinition(beanName);
        try {
            invokeDestroyMethod(singletonObject, mbd);
        } catch (Exception e) {
            throw new BeansException("无法调用[" + beanName + "]定义的销毁方法");
        }
    }

    @Override
    public abstract boolean containsBeanDefinition(String beanName);

    /// MARK - AutowireCapableBeanFactory

    @Override
    public Object autowire(Class<?> beanClass, int autowireMode) throws BeansException {
        BeanDefinition mbd = new BeanDefinition(beanClass);
        mbd.setAutowireMode(autowireMode);
        if (mbd.getResolvedAutowireMode() == BeanDefinition.AUTOWIRE_CONSTRUCTOR) {
            return autowireConstructor(beanClass.getName(), mbd).getWrappedInstance();
        }
        Object bean = ClassUtils.instantiateClass(beanClass);
        BeanWrapper beanWrapper = new BeanWrapper(bean);
        registerPropertyEditors(beanWrapper);
        populateBean(beanClass.getName(), mbd, beanWrapper);
        return bean;
    }

    @Override
    public void autowireBeanProperties(Object existingBean, int autowireMode) throws BeansException {
        if (autowireMode != BeanDefinition.AUTOWIRE_BY_NAME && autowireMode != BeanDefinition.AUTOWIRE_BY_TYPE) {
            throw new IllegalArgumentException("只允许AUTOWIRE_BY_NAME或AUTOWIRE_BY_TYPE");
        }
        BeanDefinition mbd = new BeanDefinition(existingBean.getClass());
        mbd.setAutowireMode(autowireMode);
        BeanWrapper beanWrapper = new BeanWrapper(existingBean);
        registerPropertyEditors(beanWrapper);
        populateBean(existingBean.getClass().getName(), mbd, beanWrapper);
    }

    @Override
    public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String name) throws BeansException {
        // 这儿的顺序是定义的顺序
        Object bean = existingBean;
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            bean = processor.postProcessBeforeInitialization(existingBean, name);
        }
        return bean;
    }

    @Override
    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String name) throws BeansException {
        Object bean = existingBean;
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            bean = processor.postProcessAfterInitialization(existingBean, name);
        }
        return bean;
    }

    /// MARK - Template method

    /**
     * 自动装配时使用，返回工厂中所有与requiredType兼容的bean
     */
    protected abstract Map<String, Object> findMatchingBeans(Class<?> requiredType) throws BeansException;

    /// MARK - Internals

    @NotNull
    private BeanWrapper autowireConstructor(String beanName, BeanDefinition mbd) {
        // cargs中持有的是未解析的参数
        ConstructorArgumentValues cargs = mbd.getConstructorArgumentValues();
        // 保存cargs持有的参数对应的解析版本
        ConstructorArgumentValues resolvedValues = new ConstructorArgumentValues();
        int numberOfCArgs = 0;
        if (cargs != null) {
            numberOfCArgs = cargs.getNumberOfArguments();
            Set<Integer> integerSet = cargs.getIndexedArgumentValues().keySet();
            for (int index : integerSet) {
                // 支持指定下标和不指定下标的混用
                if (index > numberOfCArgs) {
                    numberOfCArgs = index + 1;
                }
                // 执行解析并构造镜像版本
                ConstructorArgumentValues.ValueHolder valueHolder = cargs.getIndexedArgumentValues().get(index);
                Object resolvedValue = resolveValueIfNecessary(beanName, mbd, "ctor arg at " + index, valueHolder.getValue());
                resolvedValues.addIndexedArgumentValue(index, resolvedValue, valueHolder.getType());
            }
            // 执行解析并构造镜像版本
            for (ConstructorArgumentValues.ValueHolder valueHolder : cargs.getGenericArgumentValues()) {
                Object resolvedValue = resolveValueIfNecessary(beanName, mbd, "ctor generic arg", valueHolder.getValue());
                resolvedValues.addGenericArgumentValue(resolvedValue, valueHolder.getType());
            }
        }
        Constructor<?>[] constructors = mbd.getBeanClass().getConstructors();
        BeanWrapper beanWrapper = new BeanWrapper();
        registerPropertyEditors(beanWrapper);
        Constructor<?> selectedCtor = null;
        Object[] selectedArgs = null;
        // 遍历构造函数，查找第一个匹配的
        for (int i = 0; i < constructors.length; i++) {
            try {
                Constructor constructor = constructors[i];
                if (constructor.getParameterTypes().length < numberOfCArgs) {
                    throw new BeansException("在给[ " + beanName + "]按构造器自动装配时找不到对应的构造函数");
                }
                Class[] argTypes = constructor.getParameterTypes();
                Object[] args = new Object[argTypes.length];
                for (int j = 0; j < argTypes.length; j++) {
                    // 首先看看resolvedValues中是否有符合的
                    ConstructorArgumentValues.ValueHolder valueHolder = resolvedValues.getArgumentValue(j, argTypes[j]);
                    if (valueHolder != null) {
                        // 有的话解析以后可以使用
                        args[j] = beanWrapper.convertIfNecessary(valueHolder.getValue(), argTypes[i]);
                    } else {
                        // 没有的话就要在整个BeanFactory中查看一下有没有类型兼容的了
                        Map<String, Object> matchingBeans = findMatchingBeans(argTypes[j]);
                        if (matchingBeans == null || matchingBeans.size() != 1) {
                            throw new IllegalStateException("使用自动装配时BeanFactory中只能有一个类型兼容的bean。");
                        }
                        args[j] = matchingBeans.values().iterator().next();
                    }
                }
                selectedArgs = args;
                selectedCtor = constructor;
            } catch (BeansException ex) {
                // 没有合适的
                if (i == constructors.length - 1 && selectedCtor == null) {
                    throw ex;
                }
            }
        }

        if (selectedCtor == null) {
            throw new BeansException("在[" + beanName + "]中找不到合适的构造函数");
        }
        // 根据推断的构造器和参数初始化新的对象
        beanWrapper.setWrappedInstance(ClassUtils.instantiateClass(selectedCtor, selectedArgs));
        return beanWrapper;
    }

    /**
     * 给bean的属性赋值
     */
    private void populateBean(String beanName, BeanDefinition mbd, BeanWrapper beanWrapper) {
        MutablePropertyValues pvs = mbd.getPropertyValues();

        if (mbd.getResolvedAutowireMode() == BeanDefinition.AUTOWIRE_BY_NAME ||
                mbd.getResolvedAutowireMode() == BeanDefinition.AUTOWIRE_BY_TYPE) {
            MutablePropertyValues mpvs = new MutablePropertyValues(pvs);

            if (mbd.getResolvedAutowireMode() == BeanDefinition.AUTOWIRE_BY_NAME) {
                autowireByName(beanName, mbd, beanWrapper, mpvs);
            }

            if (mbd.getResolvedAutowireMode() == BeanDefinition.AUTOWIRE_BY_TYPE) {
                autowireByType(beanName, mbd, beanWrapper, mpvs);
            }

            pvs = mpvs;
        }

        applyPropertyValues(beanName, mbd, beanWrapper, pvs);
    }

    /**
     * 按名称自动装配
     */
    private void autowireByName(String beanName, BeanDefinition mbd,
                                  BeanWrapper bw, MutablePropertyValues pvs) {
        String[] propertyNames = unsatisfiedObjectProperties(mbd, bw);
        for (String propertyName : propertyNames) {
            if (containsBean(propertyName)) {
                Object bean = getBean(propertyName);
                pvs.addPropertyValue(new PropertyValue(propertyName, bean));
            }
        }
    }

    /**
     * 按类型自动装配
     */
    private void autowireByType(String beanName, BeanDefinition mbd,
                                  BeanWrapper bw, MutablePropertyValues pvs) {
        String[] propertyNames = unsatisfiedObjectProperties(mbd, bw);
        for (String propertyName : propertyNames) {
            Class<?> requiredType = bw.getPropertyDescriptor(propertyName).getPropertyType();
            Map<String, Object> matchingBeans = findMatchingBeans(requiredType);
            if (matchingBeans != null && matchingBeans.size() == 1) {
                pvs.addPropertyValue(new PropertyValue(propertyName, matchingBeans.values().iterator().next()));
            } else {
                if (matchingBeans != null && matchingBeans.size() > 1) {
                    throw new BeansException("给[" + beanName + "]应用自动装配时找到多个符合条件的bean");
                }
            }
        }
    }

    /**
     * 返回bean中未赋值的属性(可写的且非简单类型)，用于自动装配
     */
    private String[] unsatisfiedObjectProperties(BeanDefinition mbd, BeanWrapper bw) {
        Set<String> result = new TreeSet<>();
        PropertyDescriptor[] pds = bw.getPropertyDescriptors();
        for (PropertyDescriptor pd : pds) {
            if (pd.getWriteMethod() != null &&
                    !ClassUtils.isSimpleProperty(pd.getPropertyType()) &&
                    mbd.getPropertyValues().getPropertyValue(pd.getName()) == null) {
                result.add(pd.getName());
            }
        }
        return result.toArray(new String[0]);
    }

    /**
     * 赋值方法，将属性值赋给对应属性
     */
    private void applyPropertyValues(String beanName, BeanDefinition mbd, BeanWrapper bw,
                                     MutablePropertyValues pvs) throws BeansException {
        if (pvs == null) return;
        MutablePropertyValues deepCopy = new MutablePropertyValues(pvs);
        PropertyValue[] pvals = deepCopy.getPropertyValues();
        for (int i = 0; i < pvals.length; i++) {
            Object value = resolveValueIfNecessary(beanName, mbd, pvals[i].getName(), pvals[i].getValue());
            PropertyValue pv = new PropertyValue(pvals[i].getName(), value);
            deepCopy.setPropertyValueAtIndex(i, pv);
        }
        bw.setPropertyValues(deepCopy);
    }

    /**
     * 将BeanFactory持有的PropertyEditor同步到BeanWrapper
     */
    private void registerPropertyEditors(BeanWrapper wrapper) {
        Map<Class<?>, PropertyEditor> customEditors = getCustomEditors();
        Set<Class<?>> keys = customEditors.keySet();
        for (Class<?> type : keys) {
            wrapper.registerCustomEditor(type, customEditors.get(type));
        }
    }

    /**
     * 调用bean的初始化方法
     */
    private void invokeInitMethods(Object bean, BeanDefinition mbd) throws Exception {
        if (bean instanceof InitializingBean) {
            ((InitializingBean) bean).afterPropertiesSet();
        }
        if (mbd.getInitMethodName() != null) {
            bean.getClass().getMethod(mbd.getInitMethodName()).invoke(bean);
        }
    }

    /**
     * 调用bean的销毁方法
     */
    private void invokeDestroyMethod(Object bean, BeanDefinition mbd) throws Exception {
        if (bean instanceof DisposableBean) {
            ((DisposableBean) bean).destroy();
        }
        if (mbd.getDestroyMethodName() != null) {
            bean.getClass().getMethod(mbd.getDestroyMethodName()).invoke(bean);
        }
    }

    /**
     * value可能是复合类型，需要进一步解析
     */
    private Object resolveValueIfNecessary(String beanName, BeanDefinition mbd,
                                           String argName, Object value) throws BeansException {
        // value是指向另一个bean的引用
        if (value instanceof RuntimeBeanReference) {
            RuntimeBeanReference ref = (RuntimeBeanReference) value;
            return resolveReference(mbd, beanName, argName, ref);
        }
        // value是<list>标签定义的列表
        else if (value instanceof ManagedList) {
            return resolveManagedList(beanName, mbd, argName, (ManagedList) value);
        } else {
            // 内部bean/map/set等这里就不做支持了
            return value;
        }
    }

    /**
     * 解析一个指向其他bean的引用。
     */
    private Object resolveReference(BeanDefinition mergedBeanDefinition, String beanName,
                                      String argName, RuntimeBeanReference ref) throws BeansException {
        try {
            System.out.println("正在解析[" + beanName + "]的[" + argName + "]指向的["+ ref + "]引用");
            return getBean(ref.getBeanName());
        } catch (BeansException ex) {
            throw new BeansException("无法解析[" + beanName + "]的[" + argName + "]指向的["+ ref + "]引用");
        }
    }

    /**
     * 解析<list>中的每一个元素
     */
    private List<Object> resolveManagedList(String beanName, BeanDefinition mbd,
                                            String argName, ManagedList<?> ml) throws BeansException {
        List<Object> resolved = new ArrayList<>();
        for (int i = 0; i < ml.size(); i++) {
            resolved.add(resolveValueIfNecessary(beanName, mbd, argName + "[" + i + "]", ml.get(i)));
        }
        return resolved;
    }
}
