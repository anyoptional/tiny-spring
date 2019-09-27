/*
 * Github: https://github.com/AnyOptional
 * Created by Archer on 2019/9/26.
 * All rights reserved.
 */

package com.archer.spring.io;

import com.archer.spring.utils.ResourcesUtils;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

/**
 * 代表classpath下的资源。
 */
public class ClassPathResource implements Resource {

    // 用来加载资源的类加载器
    @Nullable
    private ClassLoader classLoader;

    // 资源所在的位置
    @NotNull
    private String path;

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public String getPath() {
        return path;
    }

    /**
     * path的格式支持：
     * 1、Config.xml 无前缀
     * 2、classpath:Config.xml、
     *    /Config.xml 、
     *    classpath:/Config.xml 有前缀
     */
    public ClassPathResource(String path) {
        this(path, null);
    }

    public ClassPathResource(String path, ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.path = getResolvedPath(path);
    }

    /// MARK - Resource

    @Override
    public boolean exists() {
        return getResolvedURL() != null;
    }

    @Override
    public URL getURL() throws IOException {
       URL url = getResolvedURL();
       if (url == null) {
           throw new IOException("不存在与路径[" + path + "]相关联的URL");
       }
       return url;
    }

    @Override
    public File getFile() throws IOException {
        return ResourcesUtils.getFile(getURL());
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (classLoader != null) {
            return classLoader.getResourceAsStream(path);
        }
        return ClassLoader.getSystemResourceAsStream(path);
    }

    /// MARK - internal

    private String getResolvedPath(String path) {
        Objects.requireNonNull(path, "资源文件的路径不能为空");
        // 真正加载时是不需要前缀的
        if (path.startsWith(Resource.CLASSPATH_URL_PREFIX)) {
            path = path.substring(Resource.CLASSPATH_URL_PREFIX.length());
        }
        // class path下的资源只需要指定一个相对位置即可
        // 以"/"开头的绝对路径是不能被处理的
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return path;
    }

    private URL getResolvedURL() {
        if (classLoader != null) {
            return classLoader.getResource(path);
        }
        return ClassLoader.getSystemResource(path);
    }

}
