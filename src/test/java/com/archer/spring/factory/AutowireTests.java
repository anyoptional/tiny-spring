/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/10/8.
 *  All rights reserved.
 */

package com.archer.spring.factory;

import com.archer.spring.factory.pojo.Tencent;
import com.archer.spring.factory.support.DefaultListableBeanFactory;
import com.archer.spring.factory.xml.DefaultXMLBeanDefinitionReader;
import com.archer.spring.factory.xml.XMLBeanDefinitionReader;
import com.archer.spring.io.ClassPathResource;
import org.junit.Test;
import static org.junit.Assert.*;

public class AutowireTests {

    @Test
    public void testAutowire0() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XMLBeanDefinitionReader reader = new DefaultXMLBeanDefinitionReader(beanFactory);
        reader.loadBeanDefinitions(new ClassPathResource("autowire.xml"));

        Tencent tencent = (Tencent) beanFactory.getBean("tencent");
        assertNotNull(tencent);
        assertNotNull(tencent.getCoin());
        assertEquals(tencent.getCoin().getCount(), 256);
    }
}
