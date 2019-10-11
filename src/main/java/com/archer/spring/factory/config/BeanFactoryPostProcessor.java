/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/10/11.
 *  All rights reserved.
 */

package com.archer.spring.factory.config;

import com.archer.spring.factory.BeansException;

/**
 * BeanFactory的后置处理器。
 * ApplicationContext可以通过BeanDefinition检测出所有被配置的
 * BeanFactoryPostProcessor，并且在其它bean实例化之前先准备好它们，
 * 这个特性使得BeanFactoryPostProcessor可以对配置的其它bean产生影响，
 * 比如PropertyPlaceholderConfigurer。
 */
public interface BeanFactoryPostProcessor {

    /**
     * 允许在Spring定义的标准初始化流程之后更新ApplicationContext(通过ApplicationContext暴露的BeanFactory)。
     * 此时所有的BeanDefinition已经加载，但是还没有实例化任何一个bean，因而可以对要实例化的bean做一些属性的更新或添加。
     */
    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;

}
