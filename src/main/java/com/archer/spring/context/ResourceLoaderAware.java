/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/10/11.
 *  All rights reserved.
 */

package com.archer.spring.context;

import com.archer.spring.io.ResourceLoader;

/**
 * 设置加载该对象的ResourceLoader。
 */
public interface ResourceLoaderAware {

    /**
     * 设置加载该对象的ResourceLoader。
     */
    void setResourceLoader(ResourceLoader resourceLoader);
}
