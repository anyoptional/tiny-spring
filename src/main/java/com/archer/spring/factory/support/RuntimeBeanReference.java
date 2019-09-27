/*
 * Github: https://github.com/AnyOptional
 * Created by Archer on 2019/9/26.
 * All rights reserved.
 */

package com.archer.spring.factory.support;

/**
 * 用来标识<ref>标签对另一个bean的引用。
 */
public class RuntimeBeanReference {

    // 所指向的另一个bean的名字
    private final String beanName;

    public RuntimeBeanReference(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanName() {
        return beanName;
    }

    @Override
    public String toString() {
        return "RuntimeBeanReference{" +
                "beanName='" + beanName + '\'' +
                '}';
    }

}
