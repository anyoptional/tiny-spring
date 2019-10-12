/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/10/12.
 *  All rights reserved.
 */

package com.archer.spring.context.processor;

import com.archer.spring.context.ApplicationEvent;
import com.archer.spring.context.ApplicationListener;

public class ApplicationListenerImpl implements ApplicationListener {

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        System.out.println("onApplicationEvent " + event);
    }
}
