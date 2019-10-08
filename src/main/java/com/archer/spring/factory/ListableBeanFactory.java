/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/9/26.
 *  All rights reserved.
 */

package com.archer.spring.factory;

import com.sun.istack.internal.Nullable;

import java.util.Map;

/**
 * BeanFactory只提供了对单个BeanDefinition的查询、管理接口，
 * 实际上从配置文件解析出来的BeanDefinition可能是有很多的。
 *
 * 这个接口提供了管理所有解析出来的BeanDefinition的能力。
 */
public interface ListableBeanFactory extends BeanFactory  {

    /**
     * 返回已注册的BeanDefinitions的数量。
     */
    int getBeanDefinitionCount();

    /**
     * 返回已注册的BeanDefinitions的名称。
     */
    String[] getBeanDefinitionNames();

    /**
     * 返回工厂中匹配给定类型(包含子类型)的bean的名称，通过已注册的bean definitions来判断。
     * FactoryBean不包含在内，因为FactoryBean创建的对象的类型在FactoryBean本身创建之前是不可知的。
     */
    String[] getBeanDefinitionNames(@Nullable Class<?> type);

    /**
     * 检查是否持有匹配给定名次的BeanDefinition。
     */
    boolean containsBeanDefinition(@Nullable String name);

    /**
     * 返回匹配给定类型(包含子类型)的bean实例，BeanDefinition定义的类型或FactoryBean的
     * getObjectType()返回的类型都会被考虑。
     */
    Map<String, Object> getBeansOfType(@Nullable Class<?> type,
                                       boolean includePrototypes,
                                       boolean includeFactoryBeans) throws BeansException;

}
