/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/9/26.
 *  All rights reserved.
 */

package com.archer.spring.factory.config;

import com.archer.spring.factory.BeansException;

/**
 * 扩展了BeanFactory，提供了自动装配的能力。
 */
public interface AutowireCapableBeanFactory {

    /**
     * 根据给定的bean类型和自动装配策略来创建和设置一个bean实例。
     */
    Object autowire(Class beanClass, int autowireMode) throws BeansException;

    /**
     * 自动装配给定bean实例的属性，使用指定的自动装配策略。
     */
    void autowireBeanProperties(Object existingBean, int autowireMode) throws BeansException;

    /**
     * 为指定的bean实例执行BeanPostProcessors的postProcessBeforeInitialization方法。
     * 这个方法返回的bean可以是一个包装对象。
     */
    Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String name)
            throws BeansException;

    /**
     * 为指定的bean实例执行BeanPostProcessors的postProcessAfterInitialization方法。
     * 这个方法返回的bean可以是一个包装对象。
     */
    Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String name)
            throws BeansException;
    
}
