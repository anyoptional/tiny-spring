/*
 * Github: https://github.com/AnyOptional
 * Created by Archer on 2019/9/26.
 * All rights reserved.
 */

package com.archer.spring.factory.xml;

import com.archer.spring.io.Resource;

import com.sun.istack.internal.NotNull;

/**
 * 对xml配置文件读取器的抽象。
 * 读取器最主要的目的是读取一个个<bean>标签，
 * 解析出其中的信息，生成对应的BeanDefinition。
 * @see com.archer.spring.factory.config.BeanDefinition
 */
public interface XMLBeanDefinitionReader {

    /**
     * 加载bean的定义信息。
     * @param resource 代表一个xml配置文件
     */
    void loadBeanDefinitions(@NotNull Resource resource);

}
