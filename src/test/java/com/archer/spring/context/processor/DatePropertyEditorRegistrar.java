/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/10/12.
 *  All rights reserved.
 */

package com.archer.spring.context.processor;

import com.archer.spring.factory.PropertyEditorRegistrar;
import com.archer.spring.factory.PropertyEditorRegistry;

import java.beans.PropertyEditor;
import java.util.Date;

public class DatePropertyEditorRegistrar implements PropertyEditorRegistrar {

    private PropertyEditor propertyEditor;

    public PropertyEditor getPropertyEditor() {
        return propertyEditor;
    }

    public void setPropertyEditor(PropertyEditor propertyEditor) {
        this.propertyEditor = propertyEditor;
    }

    @Override
    public void registerCustomEditors(PropertyEditorRegistry registry) {
        registry.registerCustomEditor(Date.class, propertyEditor);
    }
}
