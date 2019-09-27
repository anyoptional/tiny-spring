/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/9/26.
 *  All rights reserved.
 */

package com.archer.spring.factory.config;

import com.archer.spring.factory.BeansException;

/**
 * Bean后置处理器，允许自定义的转换(比如包装成代理对象)。
 */
public interface BeanPostProcessor {

    /**
     * 在其他初始化方法之前调用，此时的bean已经组装完毕。
     */
    Object postProcessBeforeInitialization(Object bean, String name) throws BeansException;

    /**
     * 在其他初始化方法之后调用，此时的bean已经组装完毕。
     */
    Object postProcessAfterInitialization(Object bean, String name) throws BeansException;

}
