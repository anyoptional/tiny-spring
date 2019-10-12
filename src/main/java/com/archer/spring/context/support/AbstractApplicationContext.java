/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/10/11.
 *  All rights reserved.
 */

package com.archer.spring.context.support;

import com.archer.spring.context.*;
import com.archer.spring.context.event.ApplicationEventMulticaster;
import com.archer.spring.context.event.ContextClosedEvent;
import com.archer.spring.context.event.ContextRefreshedEvent;
import com.archer.spring.context.event.DefaultApplicationEventMulticaster;
import com.archer.spring.factory.BeansException;
import com.archer.spring.factory.config.BeanFactoryPostProcessor;
import com.archer.spring.factory.config.BeanPostProcessor;
import com.archer.spring.factory.config.ConfigurableListableBeanFactory;
import com.archer.spring.io.DefaultResourceLoader;

import java.util.*;

/**
 * 模板方法模式的应用。
 * ApplicationContext模板类，完成了基础的功能框架。
 */
public abstract class AbstractApplicationContext extends DefaultResourceLoader implements ConfigurableApplicationContext {

    /// MARK - Properties

    // 注册的BeanFactory后置处理器
    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();

    // ApplicationContext的名称
    private String displayName = Objects.toString(this);

    // 启动时间，毫秒
    private long startupDate;

    // ApplicationEvent多播器
    private ApplicationEventMulticaster eventMulticaster;

    // 手动注册的ApplicationListener
    private Set<ApplicationListener> manuallyRegisteredListeners = new LinkedHashSet<>();

    /// MARK - Initializers

    public AbstractApplicationContext() { }

    /// MARK - ConfigurableApplicationContext

    public List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
        return Collections.unmodifiableList(beanFactoryPostProcessors);
    }

    @Override
    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor beanFactoryPostProcessor) {
        beanFactoryPostProcessors.add(beanFactoryPostProcessor);
    }

    @Override
    public void addApplicationListener(ApplicationListener listener) {
        manuallyRegisteredListeners.add(listener);
        eventMulticaster.addApplicationListener(listener);
    }

    @Override
    public void refresh() throws BeansException, IllegalStateException {
        // 记录启动时间
        startupDate = System.currentTimeMillis();
        // 刷新内部BeanFactory
        ConfigurableListableBeanFactory beanFactory = refreshBeanFactory();
        // 为BeanFactory进行必要的准备工作
        prepareBeanFactory(beanFactory);
        try {
            // 进行额外的后置处理
            postProcessBeanFactory(beanFactory);
            // 执行BeanFactoryPostProcessor的回调
            invokeBeanFactoryPostProcessors(beanFactory);
            // 注册所有BeanPostProcessor
            registerBeanPostProcessors(beanFactory);
            // 初始化事件多播器
            initApplicationEventMulticaster();
            // 准备完成，正在刷新
            onRefresh();
            // 注册所有ApplicationListener
            registerApplicationListeners();
            // 完成BeanFactory的初始化流程
            finishBeanFactoryInitialization(beanFactory);
            // 完成刷新
            finishRefresh();
        } catch (BeansException e) {
            // 若失败，就清理资源
            beanFactory.destroySingletons();
            // 抛出这个异常给调用者
            throw e;
        }
    }

    @Override
    public void close() {
        System.out.println("正在关闭" + getDisplayName());
        // 销毁所有缓存的singleton bean
        getBeanFactory().destroySingletons();
        // 发出ContextClosedEvent
        publishEvent(new ContextClosedEvent(this));
    }

    @Override
    public abstract ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;

    /// MARK - ApplicationContext

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public long getStartupDate() {
        return startupDate;
    }

    /// MARK - ApplicationEventPublisher

    @Override
    public void publishEvent(ApplicationEvent event) {
        eventMulticaster.multicastEvent(event);
    }

    /// MARK - ListableBeanFactory

    @Override
    public int getBeanDefinitionCount() {
        return getBeanFactory().getBeanDefinitionCount();
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return getBeanFactory().getBeanDefinitionNames();
    }

    @Override
    public String[] getBeanDefinitionNames(Class<?> type) {
        return getBeanFactory().getBeanDefinitionNames(type);
    }

    @Override
    public boolean containsBeanDefinition(String name) {
        return getBeanFactory().containsBeanDefinition(name);
    }

    @Override
    public Map<String, Object> getBeansOfType(Class<?> type, boolean includePrototypes, boolean includeFactoryBeans) throws BeansException {
        return getBeanFactory().getBeansOfType(type, includePrototypes, includeFactoryBeans);
    }

    @Override
    public Object getBean(String beanName) throws BeansException {
        return getBeanFactory().getBean(beanName);
    }

    @Override
    public <T> T getBean(String beanName, Class<T> requiredType) throws BeansException {
        return getBeanFactory().getBean(beanName, requiredType);
    }

    @Override
    public boolean containsBean(String beanName) {
        return getBeanFactory().containsBean(beanName);
    }

    @Override
    public boolean isSingleton(String beanName) throws BeansException {
        return getBeanFactory().isSingleton(beanName);
    }

    /// MARK - Template method

    /**
     * 刷新内部组合的BeanFactory。
     */
    protected abstract ConfigurableListableBeanFactory refreshBeanFactory();

    /// MARK - Internal

    /**
     * 为BeanFactory进行一些准备工作。
     */
    protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        // 添加Bean后置处理器
        beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
    }

    /**
     * 在标准初始化完成后，对BeanFactory进行一些额外处理。
     * 钩子方法，交给子类去实现。
     */
    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) { }

    /**
     * 为所有的BeanFactoryPostProcessor执行回调。
     */
    protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        // 1. 处理直接注册的BeanFactoryPostProcessor
        for (BeanFactoryPostProcessor processor : getBeanFactoryPostProcessors()) {
            processor.postProcessBeanFactory(beanFactory);
        }
        // 2. 处理在配置文件中配置的BeanFactoryPostProcessor
        // 2.1 获取类型为BeanFactoryPostProcessor的所有beanName
        String[] beanNames = beanFactory.getBeanDefinitionNames(BeanFactoryPostProcessor.class);
        for (String beanName : beanNames) {
            // 2.2 在调用之前确保BeanFactoryPostProcessor得到初始化
            BeanFactoryPostProcessor processor = getBean(beanName, BeanFactoryPostProcessor.class);
            // 2.3 调用BeanFactoryPostProcessor的相关方法
            processor.postProcessBeanFactory(beanFactory);
        }
        // TODO: 这里可以加入Order接口进行排序
    }

    /**
     * 实例化所有定义的BeanPostProcessor，并为它们进行注册。
     */
    protected void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // ApplicationContext表面上只是一个ListableBeanFactory,
        // 并不具备ConfigurableBeanFactory.addBeanPostProcessor(...)的能力。
        // 对ApplicationContext来说，配置文件中配置的BeanPostProcessor就是所有的了
        String[] beanNames = beanFactory.getBeanDefinitionNames(BeanPostProcessor.class);
        if (beanNames.length > 0) {
            for (String beanName : beanNames) {
                // 实例化所有BeanPostProcessor
                BeanPostProcessor processor = getBean(beanName, BeanPostProcessor.class);
                // 注册进beanFactory
                beanFactory.addBeanPostProcessor(processor);
            }
        }
        // TODO: 这里可以加入Order接口进行排序
    }

    /**
     * 正在刷新。
     * 钩子方法，交给子类去实现。
     */
    protected void onRefresh() { }

    /**
     * 初始化多播器。
     */
    protected void initApplicationEventMulticaster() {
        if (eventMulticaster != null) {
            eventMulticaster.removeAllListeners();
        }
        eventMulticaster = new DefaultApplicationEventMulticaster();
    }

    /**
     * 注册所有已定义的ApplicationListener
     */
    protected void registerApplicationListeners() {
        // 1. 重新注册所有手动注册上去的ApplicationListener
        manuallyRegisteredListeners.forEach(eventMulticaster::addApplicationListener);
        // 2. 注册配置文件中定义的ApplicationListener
        Collection<Object> listeners = getBeansOfType(ApplicationListener.class,
                true, false).values();
        for (Object listener : listeners) {
            eventMulticaster.addApplicationListener((ApplicationListener) listener);
        }
    }

    /**
     * 初始化剩余的所有非懒加载的bean
     */
    protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
        beanFactory.preInstantiateSingletons();
    }

    /**
     * 完成刷新
     */
    protected void finishRefresh() {
        publishEvent(new ContextRefreshedEvent(this));
    }

}
