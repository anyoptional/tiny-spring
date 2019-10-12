/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/10/11.
 *  All rights reserved.
 */

package com.archer.spring.context;

/**
 * 设置加载该对象的ApplicationEventPublisherAware。
 */
public interface ApplicationEventPublisherAware {

    /**
     * 设置加载该对象的ApplicationEventPublisherAware。
     */
    void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher);

}
