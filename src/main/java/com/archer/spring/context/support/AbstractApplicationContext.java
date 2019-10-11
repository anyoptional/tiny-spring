/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/10/11.
 *  All rights reserved.
 */

package com.archer.spring.context.support;

import com.archer.spring.context.*;
import com.archer.spring.factory.BeansException;
import com.archer.spring.factory.config.BeanFactoryPostProcessor;
import com.archer.spring.factory.config.ConfigurableListableBeanFactory;
import com.archer.spring.io.DefaultResourceLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AbstractApplicationContext extends DefaultResourceLoader implements ConfigurableApplicationContext {

    /// MARK - Properties

    // 注册的BeanFactory后置处理器
    private List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();

    // ApplicationContext的名称
    private String displayName = Objects.toString(this);


    /// MARK - LifeCycle

    @Override
    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor beanFactoryPostProcessor) {
        beanFactoryPostProcessors.add(beanFactoryPostProcessor);
    }

    @Override
    public void addApplicationListener(ApplicationListener listener) {

    }

    @Override
    public void refresh() throws BeansException, IllegalStateException {

    }

    @Override
    public void close() {

    }

    @Override
    public ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException {
        return null;
    }

    /// MARK - LifeCycle

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }

    /// MARK - ApplicationContext

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public long getStartupDate() {
        return 0;
    }

    /// MARK - ApplicationEventPublisher

    @Override
    public void publishEvent(ApplicationEvent event) {

    }

    /// MARK - ListableBeanFactory

    @Override
    public int getBeanDefinitionCount() {
        return 0;
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return new String[0];
    }

    @Override
    public String[] getBeanDefinitionNames(Class<?> type) {
        return new String[0];
    }

    @Override
    public boolean containsBeanDefinition(String name) {
        return false;
    }

    @Override
    public Map<String, Object> getBeansOfType(Class<?> type, boolean includePrototypes, boolean includeFactoryBeans) throws BeansException {
        return null;
    }

    @Override
    public Object getBean(String beanName) throws BeansException {
        return null;
    }

    @Override
    public <T> T getBean(String beanName, Class<T> requiredType) throws BeansException {
        return null;
    }

    @Override
    public boolean containsBean(String beanName) {
        return false;
    }

    @Override
    public boolean isSingleton(String beanName) throws BeansException {
        return false;
    }

}
