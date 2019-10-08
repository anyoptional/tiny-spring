/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/9/26.
 *  All rights reserved.
 */

package com.archer.spring.factory.propertyeditor;

import com.archer.spring.utils.StringUtils;
import com.sun.istack.internal.Nullable;

import java.beans.PropertyEditorSupport;

/**
 * 将String转成Boolean/boolean。
 */
public class CustomBooleanEditor extends PropertyEditorSupport {

    private static final String VALUE_TRUE = "true";

    private static final String VALUE_FALSE = "false";

    private static final String VALUE_ON = "on";

    private static final String VALUE_OFF = "off";

    private static final String VALUE_YES = "yes";

    private static final String VALUE_NO = "no";

    private static final String VALUE_1 = "1";

    private static final String VALUE_0 = "0";


    @Nullable
    private final String trueString;

    @Nullable
    private final String falseString;

    private final boolean allowEmpty;

    public CustomBooleanEditor(boolean allowEmpty) {
        this(null, null, allowEmpty);
    }

    public CustomBooleanEditor(@Nullable String trueString, @Nullable String falseString, boolean allowEmpty) {
        this.trueString = trueString;
        this.falseString = falseString;
        this.allowEmpty = allowEmpty;
    }


    @Override
    public void setAsText(@Nullable String text) throws IllegalArgumentException {
        String input = (text != null ? text.trim() : null);
        if (this.allowEmpty && !StringUtils.hasText(input)) {
            // Treat empty String as null value.
            setValue(null);
        } else if (this.trueString != null && this.trueString.equalsIgnoreCase(input)) {
            setValue(Boolean.TRUE);
        } else if (this.falseString != null && this.falseString.equalsIgnoreCase(input)) {
            setValue(Boolean.FALSE);
        } else if (this.trueString == null &&
                (VALUE_TRUE.equalsIgnoreCase(input) || VALUE_ON.equalsIgnoreCase(input) ||
                        VALUE_YES.equalsIgnoreCase(input) || VALUE_1.equals(input))) {
            setValue(Boolean.TRUE);
        } else if (this.falseString == null &&
                (VALUE_FALSE.equalsIgnoreCase(input) || VALUE_OFF.equalsIgnoreCase(input) ||
                        VALUE_NO.equalsIgnoreCase(input) || VALUE_0.equals(input))) {
            setValue(Boolean.FALSE);
        } else {
            throw new IllegalArgumentException("Invalid boolean value [" + text + "]");
        }
    }

    @Override
    public String getAsText() {
        if (Boolean.TRUE.equals(getValue())) {
            return (this.trueString != null ? this.trueString : VALUE_TRUE);
        }
        else if (Boolean.FALSE.equals(getValue())) {
            return (this.falseString != null ? this.falseString : VALUE_FALSE);
        }
        else {
            return "";
        }
    }

}
