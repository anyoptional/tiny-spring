/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/9/26.
 *  All rights reserved.
 */

package com.archer.spring.factory;

import com.sun.istack.internal.Nullable;

/**
 * 将PropertyValue.value转换成实际的类型，
 * 这个接口是使用PropertyEditor进行转换。
 */
public interface TypeConverter {

    /**
     * 将value转换成requiredType类型的实例。
     */
    @Nullable
    <T> T convertIfNecessary(@Nullable Object value, @Nullable Class<T> requiredType) throws BeansException;

}
