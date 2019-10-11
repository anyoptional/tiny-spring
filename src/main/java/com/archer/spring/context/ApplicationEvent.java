/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/10/11.
 *  All rights reserved.
 */

package com.archer.spring.context;

import java.util.EventObject;

/**
 * ApplicationEvent的基类，记录了事件发生的时间。
 */
public abstract class ApplicationEvent extends EventObject {

    // 事件发生的时间
    private long timestamp;

    public long getTimestamp() {
        return timestamp;
    }

    public ApplicationEvent(Object source) {
        super(source);
        this.timestamp = System.currentTimeMillis();
    }
}
