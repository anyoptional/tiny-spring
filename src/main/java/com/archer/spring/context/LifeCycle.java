/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/10/11.
 *  All rights reserved.
 */

package com.archer.spring.context;

/**
 * 生命周期管理。
 */
public interface LifeCycle {

    /**
     * 开启当前组件。
     */
    void start();

    /**
     * 停止当前组件。
     */
    void stop();

    /**
     * 当前组件是否正在运行。
     */
    boolean isRunning();

}
