/*
 * Github: https://github.com/AnyOptional
 * Created by Archer on 2019/9/26.
 * All rights reserved.
 */

package com.archer.spring.factory.support;

import com.archer.spring.factory.config.BeanDefinition;

/**
 * 这个接口管理着BeanFactory中BeanDefinition注册
 * 的相关事宜，因此BeanFactory的实现类也会实现这个接口。
 * 单独抽取出这个接口，是为了让BeanFactory的职责更清晰，
 * 避免成为上帝接口。BeanFactory就是一个bean工厂，司职于bean的获取查询。
 */
public interface BeanDefinitionRegistry {

    /**
     * 向BeanFactory中注册bean的定义信息
     */
    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition);

}
