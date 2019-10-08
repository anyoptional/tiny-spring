/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/9/26.
 *  All rights reserved.
 */

package com.archer.spring.factory;

/**
 * Bean的生命周期标记接口。
 */
public interface InitializingBean {

    /**
     * 在bean的属性被赋值后调用
     */
    void afterPropertiesSet() throws Exception;
}
