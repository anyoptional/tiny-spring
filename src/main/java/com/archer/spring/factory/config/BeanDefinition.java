/*
 * Github: https://github.com/AnyOptional
 * Created by Archer on 2019/9/26.
 * All rights reserved.
 */

package com.archer.spring.factory.config;

import com.archer.spring.factory.BeansException;
import com.archer.spring.factory.ConstructorArgumentValues;
import com.archer.spring.factory.FactoryBean;
import com.archer.spring.factory.MutablePropertyValues;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Objects;

/**
 * 保存从xml中解析出来的bean的定义信息。
 * 参考DefaultXMLBeanDefinitionParser中定义的
 * 标签和属性，BeanDefinition围绕这些信息来构建。
 * @see com.archer.spring.factory.xml.DefaultXMLBeanDefinitionParser
 */
public class BeanDefinition {

    /// MARK - Properties

    // 不进行自动装配
    public static final int AUTOWIRE_NO = 0;
    // 通过bean名称自动装配
    public static final int AUTOWIRE_BY_NAME = 1;
    // 通过bean类型自动装配
    public static final int AUTOWIRE_BY_TYPE = 2;
    // 自动装配构造函数
    public static final int AUTOWIRE_CONSTRUCTOR = 3;
    // 自适应装配模式
    // 具体是指，如果一个bean class有无参的构造函数，
    // 说明它的信息都是可配置的，此时就会选择by type的
    // 形式，否则使用by constructor的形式
    public static final int AUTOWIRE_AUTODETECT = 4;

    // bean所属的类 bean的名称由BeanFactoryRegistry管理
    private final Class<?> beanClass;

    // 是单实例还是每次获取都创建，默认为true
    private boolean singleton = true;

    // 对单世丽的bean是否需要懒加载，
    // 默认为false，在BeanFactory初始化时就
    // 初始化所有单实例bean
    private boolean lazyInit = false;

    // 自动装配的模式
    private int autowireMode = AUTOWIRE_NO;

    // 所依赖的其他bean的名称
    // dependsOn所代表的bean会在
    // 当前bean初始化之前得到初始化
    private String[] dependsOn;

    // 自定义的初始化方法名，要求无参
    private String initMethodName;

    // 自定义的销毁方法名，要求无参
    private String destroyMethodName;

    // setter注入的相关信息
    private MutablePropertyValues propertyValues;

    // 构造函数注入的相关信息
    private ConstructorArgumentValues constructorArgumentValues;

    /// MARK - Getters & Setters

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public boolean isSingleton() {
        return singleton;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public int getAutowireMode() {
        return autowireMode;
    }

    public void setAutowireMode(int autowireMode) {
        this.autowireMode = autowireMode;
    }

    public String[] getDependsOn() {
        return dependsOn;
    }

    public void setDependsOn(String[] dependsOn) {
        this.dependsOn = dependsOn;
    }

    public String getInitMethodName() {
        return initMethodName;
    }

    public void setInitMethodName(String initMethodName) {
        this.initMethodName = initMethodName;
    }

    public String getDestroyMethodName() {
        return destroyMethodName;
    }

    public void setDestroyMethodName(String destroyMethodName) {
        this.destroyMethodName = destroyMethodName;
    }

    public MutablePropertyValues getPropertyValues() {
        return propertyValues;
    }

    public void setPropertyValues(MutablePropertyValues propertyValues) {
        this.propertyValues = propertyValues;
    }

    public ConstructorArgumentValues getConstructorArgumentValues() {
        return constructorArgumentValues;
    }

    public void setConstructorArgumentValues(ConstructorArgumentValues constructorArgumentValues) {
        this.constructorArgumentValues = constructorArgumentValues;
    }


    /// MARK - Initializers

    public BeanDefinition(Class<?> beanClass) {
        this(beanClass, (MutablePropertyValues) null);
    }

    public BeanDefinition(Class<?> beanClass, MutablePropertyValues propertyValues) {
        this(beanClass, propertyValues, null);
    }

    public BeanDefinition(Class<?> beanClass, ConstructorArgumentValues constructorArgumentValues) {
        this(beanClass, null, constructorArgumentValues);
    }

    public BeanDefinition(Class<?> beanClass, MutablePropertyValues propertyValues,
                          ConstructorArgumentValues constructorArgumentValues) {
        Objects.requireNonNull(beanClass, "bean所属的类不能为空");
        this.beanClass = beanClass;
        this.propertyValues = propertyValues;
        this.constructorArgumentValues = constructorArgumentValues;
    }

    /// MARK - Public methods

    /**
     * 校验所表达的<bean>是否合法
     */
    public void validate() throws BeansException {
        // 只有单例的bean才需要设置延迟初始化
        if (this.lazyInit && !this.singleton) {
            throw new BeansException("只有单例的bean才可以设置延迟初始化");
        }
        // 接着检查是否设置了bean的类型
        if (this.beanClass == null) {
            throw new BeansException("beanClass不能为空");
        }
        // 如果此bean是FactoryBean，那么它必须是单例的
        if (FactoryBean.class.isAssignableFrom(getBeanClass()) && !isSingleton()) {
            throw new BeansException("FactoryBean必须配置成单例模式");
        }
        // bean类必须有共有的构造函数
        if (getBeanClass().getConstructors().length == 0) {
            throw new BeansException("[" + getBeanClass() + "]类必须有共有的无参构造函数，以便后续的创建工作");
        }
    }

    /**
     * 返回适当的装配模式。
     */
    public int getResolvedAutowireMode() {
        if (this.autowireMode == AUTOWIRE_AUTODETECT) {
            // 判定是使用setter注入还是构造函数注入。
            // 如果bean类有无参构造函数，就使用setter注入，否则使用构造函数注入。
            // 因为如果有无参构造函数，那么其他东西应该都是可配的，不然不是没意义了么
            Constructor[] constructors = getBeanClass().getConstructors();
            for (Constructor ctor : constructors) {
                if (ctor.getParameterTypes().length == 0) {
                    return AUTOWIRE_BY_TYPE;
                }
            }
            return AUTOWIRE_CONSTRUCTOR;
        } else {
            return this.autowireMode;
        }
    }

    /**
     * 是否持有构造函数参数
     */
    public boolean hasConstructorArgumentValues() {
        return (constructorArgumentValues != null && !constructorArgumentValues.isEmpty());
    }

    @Override
    public String toString() {
        return "BeanDefinition{" +
                "beanClass=" + beanClass +
                ", singleton=" + singleton +
                ", lazyInit=" + lazyInit +
                ", autowireMode=" + autowireMode +
                ", dependsOn=" + Arrays.toString(dependsOn) +
                ", initMethodName='" + initMethodName + '\'' +
                ", destroyMethodName='" + destroyMethodName + '\'' +
                ", propertyValues=" + propertyValues +
                ", constructorArgumentValues=" + constructorArgumentValues +
                '}';
    }
}
