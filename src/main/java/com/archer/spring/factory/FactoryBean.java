/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/9/26.
 *  All rights reserved.
 */

package com.archer.spring.factory;

/**
 * bean自身是一个factory.
 * 如果一个bean实现了这个接口，那么它会被当做工厂使用，而不再是普通的bean。
 */
public interface FactoryBean {

    /**
     * 此前缀用于区分是查询FactoryBean本身还是其创建的对象
     */
    String FACTORY_BEAN_PREFIX = "&";

    /**
     * 返回一个此工厂管理的对象，和BeanFactory一样，也支持singleton pattern和
     * prototype pattern。
     */
    Object getObject() throws Exception;

    /**
     * 返回此FactoryBean创建的对象的类型。
     */
    Class getObjectType();

    /**
     * 这个FactoryBean管理的bean是singleton还是prototype？
     * FactoryBean本身的scope由BeanFactory管理。
     *
     * 如果返回true，也必须保证getObject()永远返回同一对象。
     */
    boolean isSingleton();

}
