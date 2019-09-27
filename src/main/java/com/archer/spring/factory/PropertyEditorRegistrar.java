/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/9/26.
 *  All rights reserved.
 */

package com.archer.spring.factory;

/**
 * 策略接口，用于向PropertyEditorRegistry注册自定义编辑器。
 */
public interface PropertyEditorRegistrar {

    /**
     * 通过PropertyEditorRegistry注册自定义PropertyEditor。
     */
    void registerCustomEditors(PropertyEditorRegistry registry);

}
