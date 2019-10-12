/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/9/26.
 *  All rights reserved.
 */

/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/9/26.
 *  All rights reserved.
 */

package com.archer.spring.factory.config;

import com.archer.spring.factory.BeanFactory;
import com.archer.spring.factory.BeansException;
import com.archer.spring.factory.PropertyEditorRegistrar;

import java.beans.PropertyEditor;

/**
 * PropertyEditor：从XML配置文件中读取出来的配置都是java.lang.String类型，并不一定
 * 是最终的类型。PropertyEditor的作用就是将输入的String转换成最终的类型。比如Person类
 * 有一个age属性，在XML中配置
 * 		<property name="age">
 * 		 	<value>23</value>
 * 		 </property>
 * 那么这个23的最终类型是int而不是String，PropertyEditor帮我们做了这一层转换。
 *
 * 这个接口为BeanFactory提供了一定的配置能力，包括属性解析、bean后置处理器等，
 * 同时提供了不通过BeanDefinition直接注册单例bean的能力。
 */
public interface ConfigurableBeanFactory extends BeanFactory {

    /**
     * 向BeanFactory注册一个PropertyEditor，此PropertyEditor解析的目标类型是requiredType。
     */
    void registerCustomEditor(Class<?> requiredType, Class<? extends PropertyEditor> propertyEditorClass);

    /**
     * 添加一个bean的后置处理器
     */
    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

    /**
     * 添加一个PropertyEditor注册器。
     */
    void addPropertyEditorRegistrar(PropertyEditorRegistrar registrar);

    /**
     * 通过给定的bean名称返回对应的Bean定义信息，然后通过BeanDefinition就可以访问属性和构造
     * 函数参数值了。 和BeanDefinitionRegistry有重复的地方。BeanFactory的实现这两接口都有
     * 实现，不过如果遵循最小化原则，ConfigurableBeanFactory是无法转成BeanDefinitionRegistry
     * 的，有些冗余也是方便实现
     */
    BeanDefinition getBeanDefinition(String beanName) throws BeansException;

    /**
     * 将一个对象直接作为单例注册到BeanFactory中(也就是不通过BeanDefinition)。
     * 一般是BeanFactory启动时由spring内部调用，当然也可以用于运行时动态设置。
     */
    void registerSingleton(String beanName, Object singletonObject) throws BeansException;

    /**
     * 销毁此工厂中所有已注册的单例bean，通常在关闭工厂时调用。
     */
    void destroySingletons();

}
