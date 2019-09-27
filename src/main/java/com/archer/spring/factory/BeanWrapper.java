/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/9/26.
 *  All rights reserved.
 */

package com.archer.spring.factory;

import com.archer.spring.utils.ClassUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * JavaBean的包装类。
 */
public final class BeanWrapper extends TypeConverterSupport implements PropertyAccessor {

    /// MARK - Properties

    // 被包装的bean
    private Object object;

    // 为object缓存的自省信息
    private IntrospectionResults cachedIntrospectionResults;

    // 类型转换的代理
    private final TypeConverterDelegate typeConverterDelegate = new TypeConverterDelegate(this);

    /// MARK - Getters & Setters

    public void setWrappedInstance(Object object) {
        Objects.requireNonNull(object, "object不能为空");
        this.object = object;
        if (cachedIntrospectionResults == null ||
                cachedIntrospectionResults.getBeanClass().equals(object.getClass())) {
            cachedIntrospectionResults = IntrospectionResults.forClass(object.getClass());
        }
    }

    public Class<?> getWrappedClass() {
        Objects.requireNonNull(object, "object必须已被设置");
        return object.getClass();
    }

    public Object getWrappedInstance() {
        return object;
    }

    /// MARK - Initializers

    public BeanWrapper() { }

    public BeanWrapper(Object object) {
        setWrappedInstance(object);
    }

    public BeanWrapper(Class<?> clazz) {
        Objects.requireNonNull(clazz, "Class不能为空");
        setWrappedInstance(ClassUtils.instantiateClass(clazz));
    }

    /// MARK - PropertyAccessor

    @Override
    public void setPropertyValue(String propertyName, Object propertyValue) throws BeansException {
        PropertyDescriptor pd = getPropertyDescriptor(propertyName);
        if (pd == null) {
            throw new BeansException("找不到[" + propertyName + "]属性对应的PropertyDescriptor");
        }
        Method writeMethod = pd.getWriteMethod();
        if (writeMethod == null) {
            throw new BeansException("找不到[" + propertyName + "]属性对应的setter方法");
        }
        try {
            // 获取要转换成的类型
            Class<?> propertyType = pd.getPropertyType();
            // 做转换
            Object newValue = convertIfNecessary(propertyValue, propertyType);
            if (propertyType.isPrimitive() &&
                    (newValue == null || "".equals(newValue))) {
                throw new IllegalArgumentException("属性[" + propertyName + "]的类型是基本类型[" + propertyType + "]");
            }
            // 调用setter设置进去
            writeMethod.invoke(object, newValue);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new BeansException("无法调用此getter - " + writeMethod.getName());
        } catch (IllegalArgumentException e) {
            throw new BeansException(e);
        }
    }

    @Override
    public Object getPropertyValue(String propertyName) throws BeansException {
        PropertyDescriptor pd = getPropertyDescriptor(propertyName);
        if (pd == null) {
            throw new BeansException("找不到[" + propertyName + "]属性对应的PropertyDescriptor");
        }
        Method readMethod = pd.getReadMethod();
        if (readMethod == null) {
            throw new BeansException("找不到[" + propertyName + "]属性对应的getter方法");
        }
        try {
            // 调用getter来获取
            return readMethod.invoke(object);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new BeansException("无法调用此getter - " + readMethod.getName());
        }
    }

    @Override
    public PropertyDescriptor getPropertyDescriptor(String propertyName) throws BeansException {
        Objects.requireNonNull(propertyName, "属性名不能为空");
        Objects.requireNonNull(cachedIntrospectionResults, "object必须已被设置");
        return cachedIntrospectionResults.getPropertyDescriptor(propertyName);
    }

    /// MARK - Internal static class

    /**
     * 缓存了bean的自省信息，用以提高查询效率。
     */
    private static class IntrospectionResults {

        private static final Map<Class<?>, IntrospectionResults> cache = new HashMap<>();

        static IntrospectionResults forClass(Class<?> clazz) {
            IntrospectionResults results = cache.get(clazz);
            if (results == null) {
                results = new IntrospectionResults(clazz);
                cache.put(clazz, results);
            }
            return results;
        }

        private BeanInfo beanInfo;

        private Map<String, PropertyDescriptor> propertyDescriptorMap;

        BeanInfo getBeanInfo() {
            return beanInfo;
        }

        Class<?> getBeanClass() {
            return getBeanInfo().getBeanDescriptor().getBeanClass();
        }

        PropertyDescriptor getPropertyDescriptor(String propertyName) throws BeansException {
            PropertyDescriptor pd = propertyDescriptorMap.get(propertyName);
            if (pd == null) {
                throw new BeansException("[" + getBeanClass().getName() + "]没有名为[" + propertyName + "]的属性");
            }
            return pd;
        }

        private IntrospectionResults(Class<?> clazz) {
            try {
                beanInfo = Introspector.getBeanInfo(clazz);
                propertyDescriptorMap = new HashMap<>();
                for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
                    propertyDescriptorMap.put(pd.getName(), pd);
                }
            } catch (IntrospectionException ex) {
                throw new BeansException("无法从[" + clazz.getName() + "]类中获得自省信息", ex);
            }
        }
    }
}
