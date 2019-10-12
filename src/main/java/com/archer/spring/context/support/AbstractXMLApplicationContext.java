/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/10/12.
 *  All rights reserved.
 */

package com.archer.spring.context.support;

import com.archer.spring.factory.BeansException;
import com.archer.spring.factory.support.DefaultListableBeanFactory;
import com.archer.spring.factory.xml.DefaultXMLBeanDefinitionReader;
import com.archer.spring.factory.xml.XMLBeanDefinitionReader;
import com.archer.spring.io.Resource;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractXMLApplicationContext extends AbstractRefreshableApplicationContext {

    /// MARK - Initializers

    public AbstractXMLApplicationContext() { }

    @Override
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException {
        // 实例化一个XMLBeanDefinitionReader去加载BeanDefinition
        XMLBeanDefinitionReader reader = new DefaultXMLBeanDefinitionReader(beanFactory);
        loadBeanDefinitions(reader);
    }

    /**
     * 获取xml配置文件的位置信息。
     */
    protected abstract String[] getConfigLocations();

    /**
     * ApplicationContext本身是ResourceLoader，
     * 因此可以通过位置信息进一步生成Resource。
     */
    private Resource[] getConfigResources() {
        String[] locations = getConfigLocations();
        if (locations != null && locations.length > 0) {
            List<Resource> resources = new ArrayList<>();
            for (String location : locations) {
                resources.add(getResource(location));
            }
            return resources.toArray(new Resource[0]);
        }
        return null;
    }

    private void loadBeanDefinitions(XMLBeanDefinitionReader reader) {
        int numberOfBeanDefinitions = 0;
        Resource[] resources = getConfigResources();
        if (resources != null && resources.length > 0) {
            for (Resource resource : resources) {
                 numberOfBeanDefinitions += reader.loadBeanDefinitions(resource);
            }
        }
        System.out.println("一共找到" + numberOfBeanDefinitions + "个<bean>标签");
    }
}
