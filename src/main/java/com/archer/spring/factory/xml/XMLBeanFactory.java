/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/10/8.
 *  All rights reserved.
 */

package com.archer.spring.factory.xml;

import com.archer.spring.factory.BeansException;
import com.archer.spring.factory.support.DefaultListableBeanFactory;
import com.archer.spring.io.Resource;

/**
 * 组合了DefaultListableBeanFactory和XMLBeanDefinitionReader。
 */
public class XMLBeanFactory extends DefaultListableBeanFactory {

    /// MARK - Properties

    // xml配置文件读取器
    private final XMLBeanDefinitionReader reader = new DefaultXMLBeanDefinitionReader(this);


    /// MARK - Initializers

    public XMLBeanFactory(Resource resource) throws BeansException {
        this.reader.loadBeanDefinitions(resource);
    }

}
