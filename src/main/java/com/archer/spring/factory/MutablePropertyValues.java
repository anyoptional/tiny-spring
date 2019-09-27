/*
 * Github: https://github.com/AnyOptional
 * Created by Archer on 2019/9/26.
 * All rights reserved.
 */

package com.archer.spring.factory;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * <bean>标签下的<property>标签对应的管理类。
 */
public class MutablePropertyValues {

    // 持有的PropertyValue列表，对应着一系列<property>标签
    @NotNull
    private List<PropertyValue> managedPropertyValues = new ArrayList<>();

    /// MARK - Initializers

    public MutablePropertyValues() { }

    public MutablePropertyValues(MutablePropertyValues other) {
        if (other != null) {
            for (PropertyValue value : other.getPropertyValues()) {
                addPropertyValue(new PropertyValue(value.getName(), value.getValue()));
            }
        }
    }

    /// MARK - Public methods

    /**
     * 添加一个PropertyValue，意味着扫描到一个
     * <property>标签。
     */
    public void addPropertyValue(PropertyValue propertyValue) {
        for (int i = 0; i < managedPropertyValues.size(); ++i) {
            PropertyValue current = managedPropertyValues.get(i);
            // 对相同的属性，进行值的覆盖
            if (current.getName().equals(propertyValue.getName())) {
                managedPropertyValues.set(i, propertyValue);
                return;
            }
        }
        // 否则就添加
        managedPropertyValues.add(propertyValue);
    }

    /**
     * 如果PropertyValue.value是RuntimeBeanReference或ManagedList，
     * 是需要进一步解析的，解析得到的值才是最终需要注入的值，因此，在解析完成
     * 之后，需要进行一次替换。
     */
    public void setPropertyValueAtIndex(int index, PropertyValue propertyValue) {
        managedPropertyValues.set(index, propertyValue);
    }

    /**
     * 返回给定属性名对应的值。
     */
    public PropertyValue getPropertyValue(String propertyName) {
        for (PropertyValue current : managedPropertyValues) {
            if (current.getName().equals(propertyName)) {
                return current;
            }
        }
        return null;
    }

    /**
     * 返回所包含的所有PropertyValue。
     */
    public PropertyValue[] getPropertyValues() {
        return managedPropertyValues.toArray(new PropertyValue[0]);
    }

    /**
     * 对于给定的属性名，是否有<property>标签与其对应。
     */
    public boolean contains(String propertyName) {
        return getPropertyValue(propertyName) != null;
    }

    @Override
    public String toString() {
        return "MutablePropertyValues{" +
                "managedPropertyValues=" + managedPropertyValues +
                '}';
    }
}
