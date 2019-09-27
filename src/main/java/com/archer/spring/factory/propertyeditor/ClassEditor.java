/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/9/26.
 *  All rights reserved.
 */

package com.archer.spring.factory.propertyeditor;

import java.beans.PropertyEditorSupport;

/**
 * 提供了将字符串转换成类对象的能力。
 */
public class ClassEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        try {
            Class<?> clazz = Class.forName(text);
            setValue(clazz);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("[" + text + "]必须是全限定的类名");
        }
    }

    @Override
    public String getAsText() {
        Class<?> clazz = (Class<?>) getValue();
        return clazz.getName();
    }

}
