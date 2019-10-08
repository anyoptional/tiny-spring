/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/9/26.
 *  All rights reserved.
 */

package com.archer.spring.factory;

import com.archer.spring.factory.propertyeditor.ClassEditor;
import com.archer.spring.factory.propertyeditor.CustomBooleanEditor;
import com.archer.spring.factory.propertyeditor.CustomNumberEditor;

import java.beans.PropertyEditor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * PropertyEditorRegistry的简单实现。
 */
public abstract class PropertyEditorRegistrySupport implements PropertyEditorRegistry {

    // 默认属性编辑器
    private static final Map<Class<?>, PropertyEditor> defaultEditors = new LinkedHashMap<>();

    static {
        // 注册默认的属性编辑器
        defaultEditors.put(Class.class, new ClassEditor());

        defaultEditors.put(boolean.class, new CustomBooleanEditor(false));
        defaultEditors.put(Boolean.class, new CustomBooleanEditor(true));

        defaultEditors.put(byte.class, new CustomNumberEditor(Byte.class, false));
        defaultEditors.put(Byte.class, new CustomNumberEditor(Byte.class, true));
        defaultEditors.put(short.class, new CustomNumberEditor(Short.class, false));
        defaultEditors.put(Short.class, new CustomNumberEditor(Short.class, true));
        defaultEditors.put(int.class, new CustomNumberEditor(Integer.class, false));
        defaultEditors.put(Integer.class, new CustomNumberEditor(Integer.class, true));
        defaultEditors.put(long.class, new CustomNumberEditor(Long.class, false));
        defaultEditors.put(Long.class, new CustomNumberEditor(Long.class, true));
        defaultEditors.put(float.class, new CustomNumberEditor(Float.class, false));
        defaultEditors.put(Float.class, new CustomNumberEditor(Float.class, true));
        defaultEditors.put(double.class, new CustomNumberEditor(Double.class, false));
        defaultEditors.put(Double.class, new CustomNumberEditor(Double.class, true));
        defaultEditors.put(BigDecimal.class, new CustomNumberEditor(BigDecimal.class, true));
        defaultEditors.put(BigInteger.class, new CustomNumberEditor(BigInteger.class, true));
    }

    // 自定义属性编辑器
    private final Map<Class<?>, PropertyEditor> customEditors = new LinkedHashMap<>();

    @Override
    public void registerCustomEditor(Class<?> requiredType, PropertyEditor propertyEditor) {
        if (requiredType == null) {
            throw new IllegalArgumentException("requiredType不能为空");
        }
        customEditors.put(requiredType, propertyEditor);
    }

    public boolean containsCustomEditor(Class<?> requiredType) {
        return customEditors.containsKey(requiredType);
    }

    @Override
    public PropertyEditor findCustomEditor(Class<?> requiredType) {
        return customEditors.get(requiredType);
    }

    /**
     * 根据给定的类型，在默认的PropertyEditors中查找并返回。
     */
    public PropertyEditor findDefaultEditor(Class<?> requiredType) {
        return defaultEditors.get(requiredType);
    }

}
