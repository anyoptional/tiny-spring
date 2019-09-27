/*
 * Github: https://github.com/AnyOptional
 * Created by Archer on 2019/9/26.
 * All rights reserved.
 */

package com.archer.spring.factory;

import java.util.Objects;

/**
 * <property>标签对应的类，保存了属性名与属性值
 * 的对应关系，用于DI阶段给bean注入信息(通过setter)。
 *
 * 注意到<ref>标签指向另一个<bean>，这种引用
 * 关系用RuntimeBeanReference来表达；属性值
 * 也可能是数组、字典等，这种情况用ManagedList、
 * ManagedMap来表达。
 *
 * 在tiny spring中，仅仅对<list>标签做了支持。
 *
 * @see com.archer.spring.factory.support.ManagedList
 * @see com.archer.spring.factory.support.RuntimeBeanReference
 */
public class PropertyValue {

    // 属性名
    private final String name;

    // 属性值
    private final Object value;

    public PropertyValue(String name, Object value) {
        Objects.requireNonNull(name);
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "PropertyValue: name='" + name + "'; value=[" + value + "]";
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PropertyValue)) {
            return false;
        }
        PropertyValue otherPv = (PropertyValue) other;
        return (this.name.equals(otherPv.name) &&
                ((this.value == null && otherPv.value == null) || this.value.equals(otherPv.value)));
    }

    @Override
    public int hashCode() {
        return this.name.hashCode() * 29 + (this.value != null ? this.value.hashCode() : 0);
    }

}
