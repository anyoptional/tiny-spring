/*
 * Github: https://github.com/AnyOptional
 * Created by Archer on 2019/9/26.
 * All rights reserved.
 */

package com.archer.spring.utils;

import com.archer.spring.factory.BeansException;

public class ClassUtils {

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
    public static Object instantiateClass(Class clazz) throws BeansException {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new BeansException("[" + clazz.getName() + "]不能是抽象类且必须有公有的参数为空的构造函数。", ex);
        }
    }

}
