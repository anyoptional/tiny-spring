/*
 * Github: https://github.com/AnyOptional
 * Created by Archer on 2019/9/26.
 * All rights reserved.
 */

package com.archer.spring.utils;

import com.archer.spring.factory.BeansException;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class ClassUtils {

    /**
     * 获取默认类加载器。
     */
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ignore) { }
        if (cl == null) {
            cl = ClassUtils.class.getClassLoader();
            if (cl == null) {
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ignore) { }
            }
        }
        return cl;
    }

    /**
     * 根据类名初始化其一个对象。
     */
    public static <T> T instantiateClass(Class<T> clazz) throws BeansException {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new BeansException("[" + clazz.getName() + "]不能是抽象类且必须有公有的参数为空的构造函数。", ex);
        }
    }

    /**
     * 根据构造函数和参数初始化其一个对象。
     */
    public static <T> T instantiateClass(Constructor<T> ctor, Object... args) throws BeansException {
        try {
            return ctor.newInstance(args);
        } catch (Exception e) {
            throw new BeansException("无法生成新对象");
        }
    }

    private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new HashMap<>(8);

    static {
        primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
        primitiveWrapperTypeMap.put(Byte.class, byte.class);
        primitiveWrapperTypeMap.put(Character.class, char.class);
        primitiveWrapperTypeMap.put(Double.class, double.class);
        primitiveWrapperTypeMap.put(Float.class, float.class);
        primitiveWrapperTypeMap.put(Integer.class, int.class);
        primitiveWrapperTypeMap.put(Long.class, long.class);
        primitiveWrapperTypeMap.put(Short.class, short.class);
    }

    /**
     * 检查value是否是type及其子类型
     */
    public static boolean isAssignableValue(Class<?> type, Object value) {
        Objects.requireNonNull(type, "类型不能为空");
        return (value != null ? isAssignable(type, value.getClass()) : !type.isPrimitive());
    }

    /**
     * 是否类型兼容
     */
    public static boolean isAssignable(Class<?> lhsType, Class<?> rhsType) {
        Objects.requireNonNull(lhsType, "类型不能为空");
        Objects.requireNonNull(rhsType, "类型不能为空");
        return (lhsType.isAssignableFrom(rhsType) ||
                lhsType.equals(primitiveWrapperTypeMap.get(rhsType)));
    }

    /**
     * 检查是否是基本类型包装类
     */
    public static boolean isPrimitiveWrapper(Class<?> clazz) {
        Objects.requireNonNull(clazz, "类型不能为空");
        return primitiveWrapperTypeMap.containsKey(clazz);
    }

    /**
     * 检查是否是基本类型或对应的包装类
     */
    public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        Objects.requireNonNull(clazz, "类型不能为空");
        return (clazz.isPrimitive() || isPrimitiveWrapper(clazz));
    }

    /**
     * 检查clazz是否是简单属性--基本类型或字符串
     */
    public static boolean isSimpleProperty(Class<?> clazz) {
        Objects.requireNonNull(clazz);
        return clazz.isPrimitive() || String.class.equals(clazz);
    }

}
