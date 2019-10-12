/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/10/12.
 *  All rights reserved.
 */

package com.archer.spring.context.processor;

import com.archer.spring.factory.BeansException;
import com.archer.spring.factory.config.BeanFactoryPostProcessor;
import com.archer.spring.factory.config.ConfigurableListableBeanFactory;

public class LogBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        System.out.println("beanFactory's bean definition count is " + beanFactory.getBeanDefinitionCount());
    }

}
