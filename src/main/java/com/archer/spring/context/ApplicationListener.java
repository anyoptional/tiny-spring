/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/10/11.
 *  All rights reserved.
 */

package com.archer.spring.context;

import java.util.EventListener;

/**
 * 应用程序事件监听器，观察者模式的应用。
 */
public interface ApplicationListener extends EventListener {

    /**
     * 接收到ApplicationEvent的回调
     */
    void onApplicationEvent(ApplicationEvent event);
}
