/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/9/26.
 *  All rights reserved.
 */

package com.archer.spring.factory.config;

import com.archer.spring.factory.ConfigurableBeanFactory;
import com.archer.spring.factory.ListableBeanFactory;

/**
 * 在ConfigurableBeanFactory和ListableBeanFactory的基础上
 * 提供了提前统一初始化单实例bean的能力。
 */
public interface ConfigurableListableBeanFactory extends ConfigurableBeanFactory, ListableBeanFactory {

    /**
     * 保证所有非懒加载的单例bean得到初始化，包括FactoryBean。
     */
    void preInstantiateSingletons();

}
