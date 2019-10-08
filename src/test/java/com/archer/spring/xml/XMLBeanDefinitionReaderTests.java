/*
 * Github: https://github.com/AnyOptional
 * Created by Archer on 2019/9/26.
 * All rights reserved.
 */

package com.archer.spring.xml;

import com.archer.spring.factory.BeansException;
import com.archer.spring.factory.PropertyValue;
import com.archer.spring.factory.config.BeanDefinition;
import com.archer.spring.factory.support.BeanDefinitionRegistry;
import com.archer.spring.factory.support.ManagedList;
import com.archer.spring.factory.support.RuntimeBeanReference;
import com.archer.spring.factory.xml.DefaultXMLBeanDefinitionReader;
import com.archer.spring.factory.xml.XMLBeanDefinitionReader;
import com.archer.spring.io.ClassPathResource;
import com.archer.spring.io.Resource;
import com.archer.spring.pojo.Car;
import com.archer.spring.pojo.People;
import com.archer.spring.xml.pojo.Dummy;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

public class XMLBeanDefinitionReaderTests {

    static class DefaultBeanDefinitionRegistry implements BeanDefinitionRegistry {

        private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

        @Override
        public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
            if (beanDefinitionMap.containsKey(beanName)) {
                throw new BeansException("[" + beanName + "]已经注册了相关定义，不能覆盖");
            }
            beanDefinitionMap.put(beanName, beanDefinition);
        }

        BeanDefinition getBeanDefinition(String beanName) {
            return beanDefinitionMap.get(beanName);
        }

        boolean containsBeanDefinition(String beanName) {
            return beanDefinitionMap.containsKey(beanName);
        }

        int getBeanDefinitionCount() {
            return beanDefinitionMap.size();
        }
    }

    @Test
    public void testReadConfigFile() {
        Resource resource = new ClassPathResource("config.xml");
        DefaultBeanDefinitionRegistry registry = new DefaultBeanDefinitionRegistry();
        XMLBeanDefinitionReader reader = new DefaultXMLBeanDefinitionReader(registry);
        reader.loadBeanDefinitions(resource);

        assertEquals(7, registry.getBeanDefinitionCount());

        BeanDefinition byd = registry.getBeanDefinition("byd");
        assertNotNull(byd);
        assertEquals(byd.getBeanClass(), Car.class);
        PropertyValue price = byd.getPropertyValues().getPropertyValue("price");
        assertEquals(price.getValue(), "250000");
        PropertyValue brand = byd.getPropertyValues().getPropertyValue("brand");
        assertEquals(brand.getValue(), "BYD");

        BeanDefinition people = registry.getBeanDefinition("people");
        assertNotNull(people);
        assertEquals(people.getBeanClass(), People.class);
        PropertyValue citizens = people.getPropertyValues().getPropertyValue("citizens");
        assertEquals(citizens.getValue().getClass(), ManagedList.class);
        ManagedList<RuntimeBeanReference> list = (ManagedList<RuntimeBeanReference>) citizens.getValue();
        assertEquals(2, list.size());
        assertEquals(list.get(0).getBeanName(), "dai-mao-wang");
        assertEquals(list.get(1).getBeanName(), "emiya-shiro");
    }

    @Test(expected = BeansException.class)
    public void testWithoutId() {
        Resource resource = new ClassPathResource("zombie.xml");
        DefaultBeanDefinitionRegistry registry = new DefaultBeanDefinitionRegistry();
        XMLBeanDefinitionReader reader = new DefaultXMLBeanDefinitionReader(registry);
        reader.loadBeanDefinitions(resource); // throws exception
    }

    @Test(expected = BeansException.class)
    public void testWithoutClass() {
        Resource resource = new ClassPathResource("joker.xml");
        DefaultBeanDefinitionRegistry registry = new DefaultBeanDefinitionRegistry();
        XMLBeanDefinitionReader reader = new DefaultXMLBeanDefinitionReader(registry);
        reader.loadBeanDefinitions(resource); // throws exception
    }

    @Test
    public void testReadDummyFile() {
        Resource resource = new ClassPathResource("dummy.xml");
        DefaultBeanDefinitionRegistry registry = new DefaultBeanDefinitionRegistry();
        XMLBeanDefinitionReader reader = new DefaultXMLBeanDefinitionReader(registry);
        reader.loadBeanDefinitions(resource);

        BeanDefinition dummy1 = registry.getBeanDefinition("dummy1");
        assertNotNull(dummy1);
        assertEquals("initialize", dummy1.getInitMethodName());
        assertEquals("dispose", dummy1.getDestroyMethodName());
        assertNull(dummy1.getDependsOn());

        BeanDefinition dummy2 = registry.getBeanDefinition("dummy2");
        assertNotNull(dummy2);
        assertEquals(dummy2.getBeanClass(), Dummy.class);
        String[] dependsOn = dummy2.getDependsOn();
        assertNotNull(dependsOn);
        assertEquals(dependsOn.length, 2);
        assertEquals(dependsOn[0], "dummy1");
        assertEquals(dependsOn[1], "zombie");
        assertTrue(dummy2.isSingleton());
        assertFalse(dummy2.isLazyInit());

        BeanDefinition dummy3 = registry.getBeanDefinition("dummy3");
        assertNotNull(dummy3);
        assertTrue(dummy3.isSingleton());
        assertTrue(dummy3.isLazyInit());

        BeanDefinition dummy4 = registry.getBeanDefinition("dummy4");
        assertNotNull(dummy4);
        assertFalse(dummy4.isSingleton());
    }

}
