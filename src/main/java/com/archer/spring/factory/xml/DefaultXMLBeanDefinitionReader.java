/*
 * Github: https://github.com/AnyOptional
 * Created by Archer on 2019/9/26.
 * All rights reserved.
 */

package com.archer.spring.factory.xml;

import com.archer.spring.factory.BeansException;
import com.archer.spring.factory.support.BeanDefinitionRegistry;
import com.archer.spring.io.Resource;
import com.archer.spring.utils.ClassUtils;
import com.sun.istack.internal.NotNull;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * XMLBeanDefinitionReader的默认实现。
 */
public class DefaultXMLBeanDefinitionReader implements XMLBeanDefinitionReader {

    /// MARK - Properties

    // bean的类加载器
    @NotNull
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    // 解析策略
    @NotNull
    private XMLBeanDefinitionParser beanDefinitionParser = new DefaultXMLBeanDefinitionParser();

    // bean定义注册器
    @NotNull
    private final BeanDefinitionRegistry beanDefinitionRegistry;

    /// MARK - Getters & Setters

    public ClassLoader getBeanClassLoader() {
        return beanClassLoader;
    }

    public XMLBeanDefinitionParser getBeanDefinitionParser() {
        return beanDefinitionParser;
    }

    public BeanDefinitionRegistry getBeanDefinitionRegistry() {
        return beanDefinitionRegistry;
    }

    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        Objects.requireNonNull(beanClassLoader);
        this.beanClassLoader = beanClassLoader;
    }

    public void setBeanDefinitionParser(XMLBeanDefinitionParser beanDefinitionParser) {
        Objects.requireNonNull(beanDefinitionParser);
        this.beanDefinitionParser = beanDefinitionParser;
    }

    /// MARK - Initializers

    public DefaultXMLBeanDefinitionReader(BeanDefinitionRegistry registry) {
        Objects.requireNonNull(registry, "BeanDefinitionRegistry不能为空");
        this.beanDefinitionRegistry = registry;
    }

    /// MARK - XMLBeanDefinitionReader

    @Override
    public void loadBeanDefinitions(Resource resource) {
        Objects.requireNonNull(resource, "Resource不能为空，需要提供一个xml配置文件");
        InputStream is = null;
        try {
            // 读取xml文件，创建Document
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            is = resource.getInputStream();
            Document doc = docBuilder.parse(is);
            // 代理给具体的解析策略去执行
            beanDefinitionParser.registerBeanDefinitions(doc, beanClassLoader, beanDefinitionRegistry);
        } catch (Exception ex) {
            throw new BeansException("解析位于[" + resource + "]处的xml文件出错，xml文件格式不正确什么的", ex);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                    System.out.println("无法关闭位于[" + resource + "]处的资源输入流");
                }
            }
        }
    }

}
