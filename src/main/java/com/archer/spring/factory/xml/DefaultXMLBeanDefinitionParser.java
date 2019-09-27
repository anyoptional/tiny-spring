/*
 * Github: https://github.com/AnyOptional
 * Created by Archer on 2019/9/26.
 * All rights reserved.
 */

package com.archer.spring.factory.xml;

import com.archer.spring.factory.BeansException;
import com.archer.spring.factory.config.BeanDefinition;
import com.archer.spring.factory.support.BeanDefinitionRegistry;
import com.archer.spring.utils.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XMLBeanDefinitionParser的默认实现。
 */
public class DefaultXMLBeanDefinitionParser implements XMLBeanDefinitionParser {

    /// MARK - tiny spring支持的xml标签及其属性

    public static final String TRUE_VALUE = "true";
    public static final String DEFAULT_VALUE = "default";

    public static final String BEAN_ELEMENT = "bean";
    public static final String CLASS_ATTRIBUTE = "class";
    public static final String ID_ATTRIBUTE = "id";
    public static final String NAME_ATTRIBUTE = "name";
    public static final String SINGLETON_ATTRIBUTE = "singleton";
    public static final String DEPENDS_ON_ATTRIBUTE = "depends-on";
    public static final String INIT_METHOD_ATTRIBUTE = "init-method";
    public static final String DESTROY_METHOD_ATTRIBUTE = "destroy-method";
    public static final String CONSTRUCTOR_ARG_ELEMENT = "constructor-arg";
    public static final String INDEX_ATTRIBUTE = "index";
    public static final String TYPE_ATTRIBUTE = "type";
    public static final String PROPERTY_ELEMENT = "property";
    public static final String REF_ELEMENT = "ref";
    public static final String BEAN_REF_ATTRIBUTE = "bean";
    public static final String LIST_ELEMENT = "list";
    public static final String VALUE_ELEMENT = "value";
    public static final String NULL_ELEMENT = "null";

    public static final String LAZY_INIT_ATTRIBUTE = "lazy-init";

    public static final String AUTOWIRE_ATTRIBUTE = "autowire";
    public static final String AUTOWIRE_BY_NAME_VALUE = "byName";
    public static final String AUTOWIRE_BY_TYPE_VALUE = "byType";
    public static final String AUTOWIRE_CONSTRUCTOR_VALUE = "constructor";
    public static final String AUTOWIRE_AUTODETECT_VALUE = "autodetect";

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
        for (int i = 0; i < nodes.getLength(); i++) {
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
        BeanDefinition beanDefinition = parseBeanDefinition(element, classLoader);
        // 并注册进BeanFactory
        registry.registerBeanDefinition(beanName, beanDefinition);
        System.out.println("已解析出[" + beanName + "]对应的bean定义[" + beanDefinition + "]");
    }

    /**
     * 解析<bean>标签
     */
    private BeanDefinition parseBeanDefinition(Element element, ClassLoader classLoader) {
        // tiny spring也没有支持BeanFactory的层次结构，
        // 因此每个bean也需要明确指明其所属的类
        String beanClassName = element.getAttribute(CLASS_ATTRIBUTE);
        if (!StringUtils.hasLength(beanClassName)) {
            throw new BeansException("每个<bean>标签都必须明确指定class属性");
        }

        BeanDefinition beanDefinition = null;

        try {

            Class<?> clazz = Class.forName(beanClassName, true, classLoader);
        } catch (ClassNotFoundException e) {
            throw new BeansException("找不到[" + beanClassName + "]对应的类", e);
        }
        return beanDefinition;
    }

    /**
     * 解析<bean>标签下的所有<property>标签。
     * <property>标签描述的是setter注入的信息。
     */
    private void parseProertyElements(Element element) {

    }

    /**
     * 解析<bean>标签下的所有<constructor-arg>标签。
     * <constructor-arg>标签描述的是构造函数注入的信息。
     */
    private void parseConstructorArgElements(Element element) {

    }

}
