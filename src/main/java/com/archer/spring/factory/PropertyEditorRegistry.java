/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/9/26.
 *  All rights reserved.
 */

package com.archer.spring.factory;

import java.beans.PropertyEditor;

/**
 * PropertyEditor注册器。
 */
public interface PropertyEditorRegistry {

    /**
     * 注册一个PropertyEditor，此PropertyEditor
     * 处理的类型是requiredType。
     */
    void registerCustomEditor(Class<?> requiredType, PropertyEditor propertyEditor);

    /**
     * 根据requiredType返回一个PropertyEditor。
     */
    PropertyEditor findCustomEditor(Class<?> requiredType);

}
