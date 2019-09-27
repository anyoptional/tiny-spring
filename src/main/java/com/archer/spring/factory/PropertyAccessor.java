/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/9/26.
 *  All rights reserved.
 */

package com.archer.spring.factory;

import java.beans.PropertyDescriptor;

/**
 * 定义了访问JavaBean getter/setter 的方式，
 * 不支持级联属性。
 */
public interface PropertyAccessor {

    /**
     * 给bean的propertyName属性设置值propertyValue，相当于bean.setPropertyName(propertyValue)
     */
    void setPropertyValue(String propertyName, Object propertyValue) throws BeansException;

    /**
     * 获取bean的名为propertyName的属性的值，相当于bean.getPropertyName()
     */
    Object getPropertyValue(String propertyName) throws BeansException;

    /**
     * 获取特定属性的描述信息，我们关心属性对应的setter和getter是否存在，是否可访问等
     */
    PropertyDescriptor getPropertyDescriptor(String propertyName) throws BeansException;

    /**
     * 给bean的propertyName属性设置值propertyValue，相当于 bean.setPropertyName(propertyValue)
     */
    default void setPropertyValue(PropertyValue propertyValue) throws BeansException {
        setPropertyValue(propertyValue.getName(), propertyValue.getValue());
    }

    /**
     * 一次给bean的许多属性赋值。
     */
    default void setPropertyValues(MutablePropertyValues propertyValues) throws BeansException {
        setPropertyValues(propertyValues, false);
    }

    /**
     * 一次给bean的许多属性赋值。
     */
    default void setPropertyValues(MutablePropertyValues propertyValues,
                                   boolean ignoreUnknown) throws BeansException {
        for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {
            try {
                setPropertyValue(propertyValue);
            } catch (Exception ex) {
                if (!ignoreUnknown) {
                    throw ex;
                }
            }
        }
    }
}
