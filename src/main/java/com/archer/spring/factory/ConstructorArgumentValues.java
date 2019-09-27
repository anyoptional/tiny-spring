/*
 * Github: https://github.com/AnyOptional
 * Created by Archer on 2019/9/26.
 * All rights reserved.
 */

package com.archer.spring.factory;

import java.util.*;

/**
 * <bean>标签下的<constructor-arg>标签对应的管理类。
 */
public class ConstructorArgumentValues {

    // 明确指定了参数位置的情况
    private Map<Integer, ValueHolder> indexedArgumentValues = new HashMap<>();

    // 没有指定参数位置，但可能指明了参数类型
    private Set<ValueHolder> genericArgumentValues = new HashSet<>();

    public Map<Integer, ValueHolder> getIndexedArgumentValues() {
        return Collections.unmodifiableMap(indexedArgumentValues);
    }

    public Set<ValueHolder> getGenericArgumentValues() {
        return Collections.unmodifiableSet(genericArgumentValues);
    }

    /// MARK - Public methods

    /**
     * 解析出了一个带位置的<constructor-arg>标签
     */
    public void addIndexedArgumentValue(int index, Object value) {
        this.indexedArgumentValues.put(index, new ValueHolder(value));
    }

    /**
     * 解析出了一个带位置和参数类型的<constructor-arg>标签
     */
    public void addIndexedArgumentValue(int index, Object value, String type) {
        this.indexedArgumentValues.put(index, new ValueHolder(value, type));
    }

    /**
     * 获取指定位置的参数值，如果参数值没有指定类型信息，那么requiredType会被忽略，
     * 否则比对requiredType和ValueHolder.type是否相同。
     */
    public ValueHolder getIndexedArgumentValue(int index, Class requiredType) {
        ValueHolder valueHolder = this.indexedArgumentValues.get(index);
        if (valueHolder != null) {
            if (valueHolder.getType() == null || requiredType.getName().equals(valueHolder.getType())) {
                return valueHolder;
            }
        }
        return null;
    }

    /**
     * 解析出了一个不带位置和参数类型的<constructor-arg>标签
     */
    public void addGenericArgumentValue(Object value) {
        this.genericArgumentValues.add(new ValueHolder(value));
    }

    /**
     * 解析出了一个不带位置带参数类型的<constructor-arg>标签
     */
    public void addGenericArgumentValue(Object value, String type) {
        this.genericArgumentValues.add(new ValueHolder(value, type));
    }

    /**
     * 返回匹配指定类型的参数值。
     */
    public ValueHolder getGenericArgumentValue(Class requiredType) {
        // 遍历genericArgumentValues列表
        for (Iterator it = this.genericArgumentValues.iterator(); it.hasNext();) {
            ValueHolder valueHolder = (ValueHolder) it.next();
            Object value = valueHolder.getValue();
            // 如果给定了类型，检查类型是否相同
            if (valueHolder.getType() != null) {
                if (valueHolder.getType().equals(requiredType.getName())) {
                    return valueHolder;
                }
            }
            // 没有指定类型，查看
            // 1、参数值是否是一个requiredType
            // 2、第1步失败的基础上，如果requiredType是一个Array并且value是一个list，也认为匹配
            // 这里就是做了一个扩展了，因为<list>标签可以支持数组和list。
            else if (requiredType.isInstance(value) || (requiredType.isArray() && (value instanceof List))) {
                return valueHolder;
            }
        }
        return null;
    }

    /**
     * getIndexedArgumentValue和getGenericArgumentValue组合。
     * 现根据index和requiredType在map中查找，如果没有则根据requiredType
     * 在list中查找。
     */
    public ValueHolder getArgumentValue(int index, Class requiredType) {
        ValueHolder valueHolder = getIndexedArgumentValue(index, requiredType);
        if (valueHolder == null) {
            valueHolder = getGenericArgumentValue(requiredType);
        }
        return valueHolder;
    }

    /**
     * 返回持有的所有参数值，包括map和list中的。
     */
    public int getNumberOfArguments() {
        return this.indexedArgumentValues.size() + this.genericArgumentValues.size();
    }

    /**
     * 检查是否不持有参数值，包括map和list中的。
     */
    public boolean isEmpty() {
        return this.indexedArgumentValues.isEmpty() && this.genericArgumentValues.isEmpty();
    }

    @Override
    public String toString() {
        return "ConstructorArgumentValues{" +
                "indexedArgumentValues=" + indexedArgumentValues +
                ", genericArgumentValues=" + genericArgumentValues +
                '}';
    }

    /**
     * 构造函数参数值的持有类，拥有一个可选的type属性，标识构造函数参数的实际类型。
     */
    public static class ValueHolder {

        private Object value;

        private String type;

        private ValueHolder(Object value) {
            this.value = value;
        }

        private ValueHolder(Object value, String type) {
            this.value = value;
            this.type = type;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public Object getValue() {
            return value;
        }

        public String getType() {
            return type;
        }
    }

}
