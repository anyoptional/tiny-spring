/*
 * Github: https://github.com/AnyOptional
 * Created by Archer on 2019/9/26.
 * All rights reserved.
 */

package com.archer.spring.xml;

import com.archer.spring.factory.config.BeanDefinition;
import com.archer.spring.factory.support.BeanDefinitionRegistry;
import com.archer.spring.factory.xml.DefaultXMLBeanDefinitionReader;
import com.archer.spring.factory.xml.XMLBeanDefinitionReader;
import com.archer.spring.io.ClassPathResource;
import com.archer.spring.io.Resource;
import org.junit.Test;

public class XMLBeanDefinitionReaderTests {

    @Test
    public void testReadDummyFile() {
        Resource resource = new ClassPathResource("config.xml");
        XMLBeanDefinitionReader reader = new DefaultXMLBeanDefinitionReader(new BeanDefinitionRegistry() {
            @Override
            public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {

            }
        });
        reader.loadBeanDefinition(resource);
    }

}
