/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/10/11.
 *  All rights reserved.
 */

package com.archer.spring.context;

import com.archer.spring.factory.ListableBeanFactory;
import com.archer.spring.factory.config.AutowireCapableBeanFactory;
import com.archer.spring.io.ResourceLoader;

/**
 * 在BeanFactory之上集成许多易用的功能，更加方便客户端的使用，比如资源加载、事件派发等等。
 */
public interface ApplicationContext extends ResourceLoader, ListableBeanFactory, ApplicationEventPublisher {

    /**
     * 返回ApplicationContext包装的AutowireCapableBeanFactory，一般在框架内部使用。
     */
    AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException;

    /**
     * 返回ApplicationContext的名称
     */
    String getDisplayName();

    /**
     * 返回ApplicationContext的启动时间
     */
    long getStartupDate();

}
