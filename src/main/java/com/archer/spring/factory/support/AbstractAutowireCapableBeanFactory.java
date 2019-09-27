/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/9/26.
 *  All rights reserved.
 */

package com.archer.spring.factory.support;

import com.archer.spring.factory.BeansException;
import com.archer.spring.factory.config.AutowireCapableBeanFactory;
import com.archer.spring.factory.config.BeanDefinition;
import com.archer.spring.factory.config.BeanPostProcessor;

/**
 * BeanFactory模板类，实现了AutowireCapableBeanFactory接口。
 */
public class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory {


    /// MARK - Initializers

    public AbstractAutowireCapableBeanFactory() { }

    /// MARK - AbstractBeanFactory

    @Override
    public BeanDefinition getBeanDefinition(String beanName) throws BeansException {
        return null;
    }

    @Override
    public Object createBean(String beanName, BeanDefinition mbd) {
        return null;
    }

    @Override
    public void destroySingleton(String beanName, Object singletonObject) {

    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return false;
    }

    /// MARK - AutowireCapableBeanFactory

    @Override
    public Object autowire(Class beanClass, int autowireMode) throws BeansException {
        return null;
    }

    @Override
    public void autowireBeanProperties(Object existingBean, int autowireMode) throws BeansException {

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

}
