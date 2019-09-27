/*
 * Github: https://github.com/AnyOptional
 * Created by Archer on 2019/9/26.
 * All rights reserved.
 */

package com.archer.spring.factory.xml;

import com.archer.spring.factory.BeansException;
import com.archer.spring.factory.ConstructorArgumentValues;
import com.archer.spring.factory.MutablePropertyValues;
import com.archer.spring.factory.PropertyValue;
import com.archer.spring.factory.config.BeanDefinition;
import com.archer.spring.factory.support.BeanDefinitionRegistry;
import com.archer.spring.factory.support.ManagedList;
import com.archer.spring.factory.support.RuntimeBeanReference;
import com.archer.spring.utils.StringUtils;
import org.w3c.dom.*;

/**
 * XMLBeanDefinitionParser的默认实现。
 */
public class DefaultXMLBeanDefinitionParser implements XMLBeanDefinitionParser {

    /// MARK - tiny spring支持的xml标签及其属性

    private static final String TRUE_VALUE = "true";

    private static final String BEAN_ELEMENT = "bean";
    private static final String CLASS_ATTRIBUTE = "class";
    private static final String ID_ATTRIBUTE = "id";
    private static final String NAME_ATTRIBUTE = "name";
    private static final String SINGLETON_ATTRIBUTE = "singleton";
    private static final String DEPENDS_ON_ATTRIBUTE = "depends-on";
    private static final String INIT_METHOD_ATTRIBUTE = "init-method";
    private static final String DESTROY_METHOD_ATTRIBUTE = "destroy-method";
    private static final String CONSTRUCTOR_ARG_ELEMENT = "constructor-arg";
    private static final String INDEX_ATTRIBUTE = "index";
    private static final String TYPE_ATTRIBUTE = "type";
    private static final String PROPERTY_ELEMENT = "property";
    private static final String REF_ELEMENT = "ref";
    private static final String BEAN_REF_ATTRIBUTE = "bean";
    private static final String LIST_ELEMENT = "list";
    private static final String VALUE_ELEMENT = "value";
    private static final String NULL_ELEMENT = "null";

    private static final String LAZY_INIT_ATTRIBUTE = "lazy-init";

    private static final String AUTOWIRE_ATTRIBUTE = "autowire";
    private static final String AUTOWIRE_BY_NAME_VALUE = "byName";
    private static final String AUTOWIRE_BY_TYPE_VALUE = "byType";
    private static final String AUTOWIRE_CONSTRUCTOR_VALUE = "constructor";
    private static final String AUTOWIRE_AUTODETECT_VALUE = "autodetect";

    /// MARK - XMLBeanDefinitionParser

    @Override
    public void registerBeanDefinitions(Document document, ClassLoader classLoader, BeanDefinitionRegistry registry) {
        // 获取顶层元素(也就是<beans>标签)
        Element root = document.getDocumentElement();
        // 获取<beans>下的子标签列表
        NodeList nodes = root.getChildNodes();
        // 统计<bean>标签的数量
        int numberOfBeans = 0;
        // 遍历子标签列表
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node node = nodes.item(i);
            // 找到<bean>标签
            if (node instanceof Element &&
                    BEAN_ELEMENT.equals(node.getNodeName())) {
                // 每一个<bean>标签就对应一个BeanDefinition
                numberOfBeans++;
                // 加载其配置信息
                loadBeanDefinition((Element) node, classLoader, registry);
            }
        }
        System.out.println("一共找到" + numberOfBeans + "个<bean>标签");
    }

    /**
     * 解析并注册<bean>标签
     */
    private void loadBeanDefinition(Element element, ClassLoader classLoader, BeanDefinitionRegistry registry) {
        // tiny spring不支持inner bean，也不支持bean的别名，
        // 因此获取到的id就是bean的名称，也是关联对应BeanDefinition的key
        String beanName = element.getAttribute(ID_ATTRIBUTE);
        if (!StringUtils.hasLength(beanName)) {
            throw new BeansException("每个<bean>标签都必须明确指定id属性");
        }
        // 解析出对应的BeanDefinition
        BeanDefinition beanDefinition = parseBeanDefinition(beanName, element, classLoader);
        // 检验一下是否有效
        beanDefinition.validate();
        // 并注册进BeanFactory
        registry.registerBeanDefinition(beanName, beanDefinition);
        System.out.println("已解析出[" + beanName + "]对应的bean定义[" + beanDefinition + "]");
    }

    /**
     * 解析<bean>标签
     */
    private BeanDefinition parseBeanDefinition(String beanName, Element element, ClassLoader classLoader) {
        // tiny spring也没有支持BeanFactory的层次结构，
        // 因此每个bean也需要明确指明其所属的类
        String beanClassName = element.getAttribute(CLASS_ATTRIBUTE);
        if (!StringUtils.hasLength(beanClassName)) {
            throw new BeansException("每个<bean>标签都必须明确指定class属性");
        }
        try {
            // 加载这个类
            Class<?> beanClass = Class.forName(beanClassName, true, classLoader);
            // 获取所有<property>标签的内容
            MutablePropertyValues propertyValues = parseAllPropertyElements(beanName, element);
            // 获取所有<constructor-arg>标签的内容
            ConstructorArgumentValues constructorArgumentValues = parseAllConstructorArgElements(beanName, element);
            // 生成bean的定义信息
            BeanDefinition beanDefinition = new BeanDefinition(beanClass, propertyValues, constructorArgumentValues);
            // 获取依赖信息
            if (element.hasAttribute(DEPENDS_ON_ATTRIBUTE)) {
                String dependsOn = element.getAttribute(DEPENDS_ON_ATTRIBUTE);
                beanDefinition.setDependsOn(StringUtils.split(dependsOn, ",; ", true, true));
            }
            // 获取自动装配模式
            String autowire = element.getAttribute(AUTOWIRE_ATTRIBUTE);
            beanDefinition.setAutowireMode(getAutowireMode(autowire));
            // 获取自定义的初始化方法名
            String initMethodName = element.getAttribute(INIT_METHOD_ATTRIBUTE);
            if (StringUtils.hasLength(initMethodName)) {
                beanDefinition.setInitMethodName(initMethodName);
            }
            // 获取自定义的销毁方法名
            String destroyMethodName = element.getAttribute(DESTROY_METHOD_ATTRIBUTE);
            if (StringUtils.hasLength(destroyMethodName)) {
                beanDefinition.setDestroyMethodName(destroyMethodName);
            }
            // 获取是否配置成单例
            if (element.hasAttribute(SINGLETON_ATTRIBUTE)) {
                beanDefinition.setSingleton(TRUE_VALUE.equals(element.getAttribute(SINGLETON_ATTRIBUTE)));
            }
            // 获取是否配置成懒加载
            String lazyInit = element.getAttribute(LAZY_INIT_ATTRIBUTE);
            if (beanDefinition.isSingleton()) { // 此属性对单例的bean才有效
                beanDefinition.setLazyInit(TRUE_VALUE.equals(lazyInit));
            }
            return beanDefinition;
        } catch (ClassNotFoundException e) {
            throw new BeansException("找不到[" + beanClassName + "]对应的类", e);
        }
    }

    /**
     * 解析<bean>标签下的所有<property>标签。
     * <property>标签描述的是setter注入的信息。
     */
    private MutablePropertyValues parseAllPropertyElements(String beanName, Element element) {
        // 获取<bean>下的子标签
        NodeList nodes = element.getChildNodes();
        // 初始化一个保存结果的容器
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node node = nodes.item(i);
            // 找到<property>标签进行解析
            if (node instanceof Element && PROPERTY_ELEMENT.equals(node.getNodeName())) {
                // 保存解析<property>的结果到容器
                parsePropertyElement(beanName, (Element) node, propertyValues);
            }
        }
        return propertyValues;
    }

    /**
     * 解析一个<property>标签
     */
    private void parsePropertyElement(String beanName, Element element, MutablePropertyValues propertyValues) {
        // 获取属性名
        String name = element.getAttribute(NAME_ATTRIBUTE);
        if (!StringUtils.hasLength(name)) {
            throw new BeansException("<property>标签必须明确指定name属性");
        }
        // 提取属性值
        Object value = getPropertyValue(beanName, element);
        // 解析完成，加入容器
        propertyValues.addPropertyValue(new PropertyValue(name, value));
    }

    /**
     * 解析<property>标签，获取值
     */
    private Object getPropertyValue(String beanName, Element element) {
       // 获取<property>下的子标签，从中提取值
        NodeList nodes = element.getChildNodes();
        Element poiElement = null;
        for (int i = 0; i < nodes.getLength(); ++i) {
            if (nodes.item(i) instanceof Element) {
                poiElement = (Element) nodes.item(i);
                break;
            }
        }
        if (poiElement == null) {
            throw new BeansException("[" + beanName + "]下的<property>标签至少有一个<value>、<list>或<ref>");
        }
        return parsePropertySubElement(beanName, poiElement);
    }

    /**
     * 解析带有属性值的标签，提取值
     */
    private Object parsePropertySubElement(String beanName, Element element) {
        // <property>标签下有<value>/<list>/<ref>三种标签标识了属性值
        // <set>/<map>/inner bean这些这里就不做支持了
        if (element.getTagName().equals(REF_ELEMENT)) {
            // 如果是<ref>，它指向另一个bean的定义
            String beanRef = element.getAttribute(BEAN_REF_ATTRIBUTE);
            if (!StringUtils.hasLength(beanRef)) {
                throw new BeansException("[" + beanName + "] - <ref>标签必须通过bean属性指明引用的其他bean");
            }
            // 返回一个包装引用的对象
            return new RuntimeBeanReference(beanRef);
        } else if (element.getTagName().equals(LIST_ELEMENT)) {
            // 是一个List
            return getList(beanName, element);
        } else if (element.getTagName().equals(VALUE_ELEMENT)) {
            // 是字面值
            return getTextValue(beanName, element);
        } else if (element.getTagName().equals(NULL_ELEMENT)) {
            // 是一个null标签
            return null;
        }
        throw new BeansException("[" + beanName + "] - 发现一个<property>标签下未知的子标签<" + element.getTagName() + ">");
    }

    /**
     * 解析<list>标签，返回代表其值的ManagedList
     */
    private ManagedList<Object> getList(String beanName, Element element) {
        // 返回一个包装<list>的对象
        ManagedList<Object> list = new ManagedList<>();
        NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            if (nodes.item(i) instanceof Element) {
                // <list>下的子元素类型和<property>是一致的
                Element node = (Element) nodes.item(i);
                list.add(parsePropertySubElement(beanName, node));
            }
        }
        return list;
    }

    /**
     * 获取<value>标签下的字面值
     */
    private Object getTextValue(String beanName, Element element) {
        NodeList nodes = element.getChildNodes();
        // 啥也没写 当空串处理
        if (nodes.item(0) == null) {
            return "";
        }
        if (nodes.getLength() != 1 || !(nodes.item(0) instanceof Text)) {
            throw new BeansException("[" + beanName + "] - <value>标签下有且只能有字符串字面量");
        }
        Text t = (Text) nodes.item(0);
        return t.getData();
    }

    /**
     * 解析<bean>标签下的所有<constructor-arg>标签。
     * <constructor-arg>标签描述的是构造函数注入的信息。
     */
    private ConstructorArgumentValues parseAllConstructorArgElements(String beanName, Element element) {
        // 获取<bean>下的子标签
        NodeList nodes = element.getChildNodes();
        // 初始化一个保存结果的容器
        ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node node = nodes.item(i);
            // 找到<property>标签进行解析
            if (node instanceof Element && CONSTRUCTOR_ARG_ELEMENT.equals(node.getNodeName())) {
                // 保存解析<constructor-arg>的结果到容器
                parseConstructorArgElement(beanName, (Element) node, constructorArgumentValues);
            }
        }
        return constructorArgumentValues;
    }

    /**
     * 解析一个<constructor-arg>标签
     */
    private void parseConstructorArgElement(String beanName, Element element, ConstructorArgumentValues constructorArgumentValues) {
        // 可能指定了顺序
        String indexAttribute = element.getAttribute(INDEX_ATTRIBUTE);
        // 也可能指定了类型
        String typeAttribute = element.getAttribute(TYPE_ATTRIBUTE);
        // <constructor-arg>下支持的子标签类型和<property>是一样的，
        // 和<property>标签一样解析就可以了
        Object value = getPropertyValue(beanName, element);
        if (StringUtils.hasLength(indexAttribute)) {
            try {
                // 确定index有效
                int index = Integer.parseInt(indexAttribute);
                if (index < 0) {
                    throw new BeansException("[" + beanName + "] - <constructor-arg>标签的index属性值不能是负数");
                }
                // 有index也有type
                if (StringUtils.hasLength(typeAttribute)) {
                    constructorArgumentValues.addIndexedArgumentValue(index, value, typeAttribute);
                } else {
                    // 只有index
                    constructorArgumentValues.addIndexedArgumentValue(index, value);
                }

            } catch (NumberFormatException e) {
                throw new BeansException("[" + beanName + "] - <constructor-arg>标签的index属性值必须是一个整形");
            }
        } else {
            // 只有type
            if (StringUtils.hasLength(typeAttribute)) {
                constructorArgumentValues.addGenericArgumentValue(value, typeAttribute);
            } else {
                // 啥也没有
                constructorArgumentValues.addGenericArgumentValue(value);
            }
        }
    }

    /**
     * 获取attribute对应的装配模式
     */
    private int getAutowireMode(String attribute) {
        int autowire = BeanDefinition.AUTOWIRE_NO;
        if (AUTOWIRE_BY_NAME_VALUE.equals(attribute)) {
            autowire = BeanDefinition.AUTOWIRE_BY_NAME;
        }
        else if (AUTOWIRE_BY_TYPE_VALUE.equals(attribute)) {
            autowire = BeanDefinition.AUTOWIRE_BY_TYPE;
        }
        else if (AUTOWIRE_CONSTRUCTOR_VALUE.equals(attribute)) {
            autowire = BeanDefinition.AUTOWIRE_CONSTRUCTOR;
        }
        else if (AUTOWIRE_AUTODETECT_VALUE.equals(attribute)) {
            autowire = BeanDefinition.AUTOWIRE_AUTODETECT;
        }
        return autowire;
    }

}
