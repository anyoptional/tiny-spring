/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/9/26.
 *  All rights reserved.
 */

package com.archer.spring.factory.support;

import com.archer.spring.factory.BeansException;
import com.archer.spring.factory.FactoryBean;
import com.archer.spring.factory.config.BeanDefinition;
import com.archer.spring.factory.config.ConfigurableListableBeanFactory;

import java.util.*;

/**
 * 基础的BeanFactory实现。
 */
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory
        implements BeanDefinitionRegistry, ConfigurableListableBeanFactory {

    /// MARK - Properties

    // 容器中所有BeanDefinition对应的标识名
    private List<String> beanDefinitionNames = new ArrayList<>();

    // 容器中所有的BeanDefinition
    private Map<String, BeanDefinition> registeredBeanDefinitions = new HashMap<>();

    /// MARK - Initializers

    public DefaultListableBeanFactory() { }

    /// MARK - AbstractAutowireCapableBeanFactory

    @Override
    public BeanDefinition getBeanDefinition(String beanName) throws BeansException {
        BeanDefinition bd = registeredBeanDefinitions.get(beanName);
        if (bd == null) {
            throw new BeansException("找不到[" + beanName + "]对应的BeanDefinition");
        }
        return bd;
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return registeredBeanDefinitions.containsKey(beanName);
    }

    @Override
    protected Map<String, Object> findMatchingBeans(Class<?> requiredType) throws BeansException {
        return getBeansOfType(requiredType, true, true);
    }

    /// MARK - BeanDefinitionRegistry

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        if (registeredBeanDefinitions.containsKey(beanName)) {
            throw new BeansException("已经注册了名为[" + beanName + "]的BeanDefinition");
        }
        beanDefinitionNames.add(beanName);
        registeredBeanDefinitions.put(beanName, beanDefinition);
    }

    /// MARK - ConfigurableListableBeanFactory

    @Override
    public void preInstantiateSingletons() {
        for (String beanName : beanDefinitionNames) {
            if (containsBeanDefinition(beanName)) {
                BeanDefinition bd = getBeanDefinition(beanName);
                if (bd.isSingleton() && !bd.isLazyInit()) {
                    if (FactoryBean.class.isAssignableFrom(bd.getBeanClass())) {
                        FactoryBean factory = (FactoryBean) getBean( FactoryBean.FACTORY_BEAN_PREFIX + beanName);
                        if (factory.isSingleton()) {
                            getBean(beanName);
                        }
                    } else {
                        getBean(beanName);
                    }
                }
            }
        }
    }

    @Override
    public int getBeanDefinitionCount() {
        return registeredBeanDefinitions.size();
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return getBeanDefinitionNames(null);
    }

    @Override
    public String[] getBeanDefinitionNames(Class<?> type) {
        List<String> matches = new ArrayList<>();
        for (String beanName : beanDefinitionNames) {
            if (type == null ||
                    type.isAssignableFrom(getBeanDefinition(beanName).getBeanClass())) {
                matches.add(beanName);
            }
        }
        return matches.toArray(new String[0]);
    }

    @Override
    public Map<String, Object> getBeansOfType(Class<?> type, boolean includePrototypes, boolean includeFactoryBeans) throws BeansException {
        String[] beanNames = getBeanDefinitionNames(type);
        Map<String, Object> result = new HashMap<>();
        for (String beanName : beanNames) {
            if (includePrototypes || isSingleton(beanName)) {
                result.put(beanName, getBean(beanName));
            }
        }

        String[] singletonNames = getSingletonNames(type);
        for (String beanName : singletonNames) {
            result.put(beanName, getBean(beanName));
        }

        if (includeFactoryBeans) {
            String[] factoryNames = getBeanDefinitionNames(FactoryBean.class);
            for (String factoryName : factoryNames) {
                try {
                    FactoryBean factory = (FactoryBean) getBean(FactoryBean.FACTORY_BEAN_PREFIX + factoryName);
                    Class objectType = factory.getObjectType();
                    if ((objectType == null && factory.isSingleton()) ||
                            ((factory.isSingleton() || includePrototypes) &&
                                    objectType != null && type.isAssignableFrom(objectType))) {
                        Object createdObject = getBean(factoryName);
                        if (type.isInstance(createdObject)) {
                            result.put(factoryName, createdObject);
                        }
                    }
                } catch (BeansException ex) {
                    System.out.println("从FactoryBean中创建对象失败");
                }
            }
        }

        return result;
    }
}
