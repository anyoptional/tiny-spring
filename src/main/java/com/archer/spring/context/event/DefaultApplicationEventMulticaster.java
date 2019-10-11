/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/10/11.
 *  All rights reserved.
 */

package com.archer.spring.context.event;

import com.archer.spring.context.ApplicationEvent;
import com.archer.spring.context.ApplicationListener;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * ApplicationEventMulticaster的默认实现。
 */
public class DefaultApplicationEventMulticaster implements ApplicationEventMulticaster {

    /// MARK - Properties

    // 已注册的ApplicationListener
    private Set<ApplicationListener> registeredListeners = new CopyOnWriteArraySet<>();

    /// MARK - Getters & Setters

    public Set<ApplicationListener> getApplicationListeners() {
        return Collections.unmodifiableSet(registeredListeners);
    }

    /// MARK - ApplicationEventMulticaster

    @Override
    public void addApplicationListener(ApplicationListener listener) {
        registeredListeners.add(listener);
    }

    @Override
    public void removeApplicationListener(ApplicationListener listener) {
        registeredListeners.remove(listener);
    }

    @Override
    public void removeAllListeners() {
        registeredListeners.clear();
    }

    @Override
    public void multicastEvent(ApplicationEvent event) {
        registeredListeners.forEach((listener) -> listener.onApplicationEvent(event));
    }

}
