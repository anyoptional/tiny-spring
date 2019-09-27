/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/9/26.
 *  All rights reserved.
 */

package com.archer.spring.factory;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

/**
 * BeanFactory持有从xml解析出来的
 * BeanDefinition，根据BeanDefinition
 * 提供的信息去做JavaBean的创建、管理工作。
 *
 * 这个接口只提供了最小的功能集，恰恰也是一般客
 * 户端程序用得最多的部分。更多高级功能都定义在
 * 其子接口中，比如ListableBeanFactory、ApplicationContext。
 */
public interface BeanFactory {

    /**
     * 通过beanName返回BeanFactory管理的一个对象。
     */
    @NotNull
    Object getBean(@NotNull String beanName) throws BeansException;

    /**
     * 通过beanName返回BeanFactory管理的一个对象，附加类型检查。
     */
    @NotNull
    <T> T getBean(@NotNull String beanName, @Nullable Class<T> requiredType) throws BeansException;

    /**
     * BeanFactory中是否存在名称为beanName的对象。
     * 该算法会查看BeanFactory持有的BeanDefinition来
     * 判断，因此不一定会导致对象的实例化。
     */
    boolean containsBean(@NotNull String beanName);

    /**
     * 名称为beanName的对象类型是singleton还是prototype。
     * 同样会查看持有的BeanDefinition，也因此不一定会导致
     * 对象的实例化。
     */
    boolean isSingleton(@NotNull String beanName) throws BeansException;

}
