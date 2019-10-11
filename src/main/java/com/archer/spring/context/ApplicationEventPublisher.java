/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/10/11.
 *  All rights reserved.
 */

package com.archer.spring.context;

/**
 * 应用程序事件派发器，封装了事件的派发逻辑，一般作为
 * ApplicationContext的父接口使用。
 */
public interface ApplicationEventPublisher {

    /**
     * 派发一个ApplicationEvent，并通知所有已注册的ApplicationListener。
     */
    void pushlishEvent(ApplicationEvent event);
}
