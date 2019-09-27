/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/9/26.
 *  All rights reserved.
 */

package com.archer.spring.factory;

/**
 * 类型转换的代理，TypeConverter将实际的转换工作代理给了此类。
 * @see TypeConverter
 */
public class TypeConverterDelegate {

    // 转换工作需要PropertyEditor的支持
    private final PropertyEditorRegistrySupport propertyEditorRegistry;

    public TypeConverterDelegate(PropertyEditorRegistrySupport propertyEditorRegistry) {
        this.propertyEditorRegistry = propertyEditorRegistry;
    }

    /**
     * 类型转换的核心算法。
     */
    public <T> T convertIfNecessary(Object value,
                                    Class<T> requiredType) throws BeansException {



        return null;
    }

}
