/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/10/12.
 *  All rights reserved.
 */

package com.archer.spring.context.processor;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatePropertyEditor extends PropertyEditorSupport {

    // 日期和时间格式
    private String datePattern;

    public String getDatePattern() {
        return datePattern;
    }

    public void setDatePattern(String datePattern) {
        this.datePattern = datePattern;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        try {
            // 1. 将String转成Date
            SimpleDateFormat format = new SimpleDateFormat(getDatePattern());
            Date date = format.parse(text);
            // 2. 调用setValue进行保存
            setValue(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException("转换失败", e);
        }
    }

    @Override
    public String getAsText() {
        // 1. 获取保存的Date
        Date date = (Date) getValue();
        // 2. 转回String
        return new SimpleDateFormat(getDatePattern()).format(date);
    }
}