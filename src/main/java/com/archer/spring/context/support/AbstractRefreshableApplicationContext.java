/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/10/12.
 *  All rights reserved.
 */

package com.archer.spring.context.support;

import com.archer.spring.context.ApplicationContextException;
import com.archer.spring.factory.BeansException;
import com.archer.spring.factory.config.ConfigurableListableBeanFactory;
import com.archer.spring.factory.support.DefaultListableBeanFactory;

/**
 * 实现了AbstractApplicationContext的模板方法refreshBeanFactory()，
 * 并提供了模板方法loadBeanDefinitions()给子类去加载配置文件。
 */
public abstract class AbstractRefreshableApplicationContext extends AbstractApplicationContext {

    /// MARK - Properties

    // 内部持有的BeanFactory
    private DefaultListableBeanFactory beanFactory;

    @Override
    public ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException {
        return beanFactory;
    }

    /// MARK - Initializers

    public AbstractRefreshableApplicationContext() { }

    /// MARK - AbstractApplicationContext

    @Override
    protected ConfigurableListableBeanFactory refreshBeanFactory() {
        // 刷新时如果BeanFactory已经存在，首先要进行资源的清理
        if (beanFactory != null) {
            beanFactory.destroySingletons();
            beanFactory = null;
        }
        // 清理完毕重新创建BeanFactory并加载配置文件
        try {
            DefaultListableBeanFactory listableBeanFactory = createBeanFactory();
            loadBeanDefinitions(listableBeanFactory);
            beanFactory = listableBeanFactory;
            return listableBeanFactory;
        } catch (Exception e) {
            throw new ApplicationContextException("刷新BeanFactory失败", e);
        }
    }

    /**
     * 创建BeanFactory，可以由子类进一步定制。
     */
    protected DefaultListableBeanFactory createBeanFactory() {
        return new DefaultListableBeanFactory();
    }

    /// MARK - Template method

    /**
     * 将加载BeanDefinition的功能交由子类去实现，
     * 子类根据资源的类型，运用不同的加载方式，从而派生出不同的ApplicationContext。
     */
    protected abstract void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException;
}
