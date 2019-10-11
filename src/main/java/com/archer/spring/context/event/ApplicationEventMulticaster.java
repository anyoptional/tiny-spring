/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/10/11.
 *  All rights reserved.
 */

package com.archer.spring.context.event;

import com.archer.spring.context.ApplicationEvent;
import com.archer.spring.context.ApplicationListener;

/**
 * 应用程序事件多播器，管理着一组ApplicationListener，并将
 * ApplicationEvent派发给它们。
 *
 * ApplicationEventPublisher就是利用ApplicationEventMulticaster
 * 来将事件派发给监听器。
 */
public interface ApplicationEventMulticaster {

    /**
     * 添加一个ApplicationListener来接收ApplicationEvent。
     */
    void addApplicationListener(ApplicationListener listener);

    /**
     * 移除一个ApplicationListener。
     */
    void removeApplicationListener(ApplicationListener listener);

    /**
     * 移除所有已注册的ApplicationListener。
     */
    void removeAllListeners();

    /**
     * 将给定的ApplicationEvent多播到适当的ApplicationListener(s)。
     */
    void multicastEvent(ApplicationEvent event);
}
