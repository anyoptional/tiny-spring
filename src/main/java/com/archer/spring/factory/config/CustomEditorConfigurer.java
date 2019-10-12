/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/10/12.
 *  All rights reserved.
 */

package com.archer.spring.factory.config;

import com.archer.spring.factory.BeansException;
import com.archer.spring.factory.PropertyEditorRegistrar;

/**
 * 用来帮助注册自定义PropertyEditor的BeanFactory后置处理器。
 */
public class CustomEditorConfigurer implements BeanFactoryPostProcessor {

    // PropertyEditor注册器
    private PropertyEditorRegistrar[] propertyEditorRegistrars;

    public void setPropertyEditorRegistrars(PropertyEditorRegistrar[] propertyEditorRegistrars) {
        this.propertyEditorRegistrars = propertyEditorRegistrars;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (propertyEditorRegistrars != null) {
            for (PropertyEditorRegistrar registrar : propertyEditorRegistrars) {
                beanFactory.addPropertyEditorRegistrar(registrar);
            }
        }
    }
}
