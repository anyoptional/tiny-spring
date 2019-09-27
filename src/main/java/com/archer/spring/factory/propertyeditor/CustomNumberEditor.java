/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/9/26.
 *  All rights reserved.
 */

package com.archer.spring.factory.propertyeditor;

import com.archer.spring.utils.NumberUtils;
import com.archer.spring.utils.StringUtils;
import com.sun.istack.internal.Nullable;

import java.beans.PropertyEditorSupport;
import java.text.NumberFormat;

/**
 * 适用于任何Number子类的PropertyEditor。
 */
public class CustomNumberEditor extends PropertyEditorSupport {

    private final Class<? extends Number> numberClass;

    // 用来在string - number 之间转换
    @Nullable
    private final NumberFormat numberFormat;

    // 是否允许空字符串
    private final boolean allowEmpty;

    public CustomNumberEditor(Class<? extends Number> numberClass,
                              boolean allowEmpty) throws IllegalArgumentException {
        this(numberClass, null, allowEmpty);
    }

    public CustomNumberEditor(Class<? extends Number> numberClass,
                              @Nullable NumberFormat numberFormat,
                              boolean allowEmpty) throws IllegalArgumentException {

        if (!Number.class.isAssignableFrom(numberClass)) {
            throw new IllegalArgumentException("属性所属的类必须是Number的子类");
        }
        this.numberClass = numberClass;
        this.numberFormat = numberFormat;
        this.allowEmpty = allowEmpty;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        // 处理空串的情况
        if (this.allowEmpty && !StringUtils.hasLength(text)) {
            setValue(null);
        } else if (this.numberFormat != null) {
            setValue(NumberUtils.parseNumber(text, this.numberClass, this.numberFormat));
        } else {
            setValue(NumberUtils.parseNumber(text, this.numberClass));
        }
    }

    @Override
    public void setValue(@Nullable Object value) {
        if (value instanceof Number) {
            super.setValue(NumberUtils.convertNumberToTargetClass((Number) value, this.numberClass));
        } else {
            super.setValue(value);
        }
    }

    @Override
    public String getAsText() {
        Object value = getValue();
        if (value == null) {
            return "";
        }
        if (this.numberFormat != null) {
            return this.numberFormat.format(value);
        } else {
            return value.toString();
        }
    }

}

