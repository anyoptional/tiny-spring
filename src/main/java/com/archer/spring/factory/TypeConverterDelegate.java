/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/9/26.
 *  All rights reserved.
 */

package com.archer.spring.factory;

import com.archer.spring.utils.ClassUtils;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;

/**
 * 类型转换的代理，TypeConverter将实际的转换工作代理给了此类。
 * @see TypeConverter
 */
public class TypeConverterDelegate {

    // 转换工作需要PropertyEditor的支持
    private final PropertyEditorRegistrySupport propertyEditorRegistry;

    public TypeConverterDelegate(PropertyEditorRegistrySupport propertyEditorRegistry) {
        this.propertyEditorRegistry = propertyEditorRegistry;
    }

    @Nullable
    public Object convertIfNecessary(@Nullable Object value,
                                     @NotNull PropertyDescriptor descriptor) throws BeansException {
        return convertIfNecessary(descriptor.getName(), value, descriptor.getPropertyType(), descriptor);
    }

    public Object convertIfNecessary(String propertyName, Object value, Class<?> requiredType) {
        return convertIfNecessary(propertyName, value, requiredType, null);
    }

    /**
     * 类型转换的核心算法。
     */
    private Object convertIfNecessary(String propertyName, Object value, Class<?> requiredType, PropertyDescriptor descriptor) {
        Object convertedValue = value;

        // 查找一下这个类型有没有对应的PropertyEditor
        PropertyEditor editor = propertyEditorRegistry.findCustomEditor(requiredType);

        // 若没有，尝试从PropertyDescriptor生成
        if (editor == null && descriptor != null) {
            Class<?> editorClass = descriptor.getPropertyEditorClass();
            if (editorClass != null) {
                editor = (PropertyEditor) ClassUtils.instantiateClass(editorClass);
            }
        }
        // 仍没有
        if (editor == null) {
            // 获取默认的PropertyEditor
            editor = propertyEditorRegistry.findDefaultEditor(requiredType);
            // 也没有默认的PropertyEditor
            if (editor == null) {
                // 查询标准JavaBean的PropertyEditor
                editor = PropertyEditorManager.findEditor(requiredType);
            }
        }

        // 若类型不匹配，执行转换
        if (editor != null || (requiredType != null && !ClassUtils.isAssignableValue(requiredType, convertedValue))) {
            convertedValue = doConvertValue(convertedValue, requiredType, editor);
        }

        if (requiredType != null) {
            if (convertedValue != null) {
                if (String.class.equals(requiredType) && ClassUtils.isPrimitiveOrWrapper(convertedValue.getClass())) {
                    // 基本类型转String
                    return convertedValue.toString();
                } else if (requiredType.isArray()) {
                    // 转数组
                    return convertToTypedArray(convertedValue, propertyName, requiredType.getComponentType());
                } else {
                    // handled above
                }
            }

            if (!ClassUtils.isAssignableValue(requiredType, convertedValue)) {
                throw new IllegalArgumentException("无法将name = [" + propertyName + "], value = [" + value + "]" + "的属性转换成[" + requiredType + "]类型");
            }
        }
        return convertedValue;
    }

    private Object doConvertValue(Object value, Class<?> requiredType, PropertyEditor editor) {
        Object convertedValue = value;
        if (editor != null) {
            if (convertedValue instanceof String) {
                System.out.println("正在使用[" + editor + "]将字符串转换成[" + requiredType + "]类型");
                String newTextValue = (String) convertedValue;
                return doConvertTextValue(newTextValue, editor);
            } else {
                try {
                    editor.setValue(convertedValue);
                    Object newConvertedValue = editor.getValue();
                    if (newConvertedValue != convertedValue) {
                        convertedValue = newConvertedValue;
                    }
                } catch (Exception ex) {
                    System.out.println("[" + editor.getClass().getName() + "]不支持setValue方法");
                }
            }
        }
        return convertedValue;
    }

    private Object doConvertTextValue(String newTextValue, PropertyEditor editor) {
        editor.setAsText(newTextValue);
        return editor.getValue();
    }

    private Object convertToTypedArray(Object input, String propertyName, Class componentType) {
        if (input instanceof Collection) {
            Collection coll = (Collection) input;
            Object result = Array.newInstance(componentType, coll.size());
            int i = 0;
            for (Iterator it = coll.iterator(); it.hasNext(); i++) {
                Object value = convertIfNecessary(buildIndexedPropertyName(propertyName, i), it.next(), componentType);
                Array.set(result, i, value);
            }
            return result;
        } else if (input.getClass().isArray()) {
            // 看一下是否需要转换数组中的元素
            if (componentType.equals(input.getClass().getComponentType()) &&
                    !this.propertyEditorRegistry.containsCustomEditor(componentType)) {
                return input;
            }
            int arrayLength = Array.getLength(input);
            Object result = Array.newInstance(componentType, arrayLength);
            for (int i = 0; i < arrayLength; i++) {
                Object value = convertIfNecessary(buildIndexedPropertyName(propertyName, i), Array.get(input, i), componentType);
                Array.set(result, i, value);
            }
            return result;
        } else {
            // 就一个值，包装进数组
            Object result = Array.newInstance(componentType, 1);
            Object value = convertIfNecessary(buildIndexedPropertyName(propertyName, 0), input, componentType);
            Array.set(result, 0, value);
            return result;
        }
    }

    private String buildIndexedPropertyName(String propertyName, int index) {
        return (propertyName != null ?
                propertyName + "[" + index + "]" :
                null);
    }
}
