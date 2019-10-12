/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/10/11.
 *  All rights reserved.
 */

package com.archer.spring.context;

/**
 * 设置加载该对象的ApplicationContext。
 */
public interface ApplicationContextAware {

    /**
     * 设置加载该对象的ApplicationContext。
     */
    void setApplicationContext(ApplicationContext applicationContext);

}
