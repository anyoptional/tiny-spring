/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/10/11.
 *  All rights reserved.
 */

package com.archer.spring.context.event;

import com.archer.spring.context.ApplicationEvent;
import com.archer.spring.context.ApplicationListener;

import java.util.HashSet;
import java.util.Set;

public class DefaultApplicationEventMulticaster implements ApplicationEventMulticaster {

    private Set<ApplicationListener> registeredListeners = new HashSet<>();

    @Override
    public void addApplicationListener(ApplicationListener listener) {

    }

    @Override
    public void removeApplicationListener(ApplicationListener listener) {

    }

    @Override
    public void removeAllListeners() {

    }

    @Override
    public void multicastEvent(ApplicationEvent event) {

    }

}
