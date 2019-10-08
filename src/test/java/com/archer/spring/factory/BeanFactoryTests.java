/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/9/26.
 *  All rights reserved.
 */

package com.archer.spring.factory;

import com.archer.spring.factory.support.DefaultListableBeanFactory;
import com.archer.spring.factory.xml.DefaultXMLBeanDefinitionReader;
import com.archer.spring.factory.xml.XMLBeanDefinitionReader;
import com.archer.spring.io.ClassPathResource;
import com.archer.spring.pojo.Capital;
import org.junit.Test;

import static org.junit.Assert.*;

public class BeanFactoryTests {

    @Test
    public void testBeanFactory0() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XMLBeanDefinitionReader reader = new DefaultXMLBeanDefinitionReader(beanFactory);
        reader.loadBeanDefinitions(new ClassPathResource("config.xml"));
        Capital capital = (Capital) beanFactory.getBean("capital");
        assertNotNull(capital);
        System.out.println(capital);
    }
}
