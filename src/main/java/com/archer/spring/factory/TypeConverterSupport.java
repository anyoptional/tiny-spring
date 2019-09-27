/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/9/26.
 *  All rights reserved.
 */

package com.archer.spring.factory;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.Objects;

/**
 * TypeConverter的简单实现，将实际的转换工作代理给了TypeConverterDelegate。
 *
 * 之所以继承PropertyEditorRegistrySupport是因为类型转换离不开PropertyEditor的支持，
 * 而只有PropertyEditorRegistry才持有着所有默认的和自定义的PropertyEditor。
 */
public abstract class TypeConverterSupport extends PropertyEditorRegistrySupport implements TypeConverter {

    @NotNull
    private TypeConverterDelegate typeConverterDelegate = new TypeConverterDelegate(this);

    public TypeConverterDelegate getTypeConverterDelegate() {
        return typeConverterDelegate;
    }

    public void setTypeConverterDelegate(TypeConverterDelegate typeConverterDelegate) {
        Objects.requireNonNull(typeConverterDelegate, "TypeConverterDelegate不能为空");
        this.typeConverterDelegate = typeConverterDelegate;
    }

    @Override
    @Nullable
    public <T> T convertIfNecessary(Object value, Class<T> requiredType) throws BeansException {
        return typeConverterDelegate.convertIfNecessary(value, requiredType);
    }

}
