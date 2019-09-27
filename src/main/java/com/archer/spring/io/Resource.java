/*
 * Github: https://github.com/AnyOptional
 * Created by Archer on 2019/9/26.
 * All rights reserved.
 */

package com.archer.spring.io;

import com.sun.istack.internal.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * 提供了对资源的同一描述。
 * 这个接口将资源从它的实际类型抽象出来，
 * 例如文件系统或类路径资源。
 * 在tiny spring的实现中，我们主要针对
 * 类路径下的xml配置文件，因此仅实现ClassPathResource。
 */
public interface Resource extends InputStreamSource {

    /**
     * 从类路径加载的伪URL前缀。
     */
    String CLASSPATH_URL_PREFIX = "classpath:";

    /**
     * 文件系统中文件的URL协议名。
     */
    String FILESYSTEM_URL_PROTOCOL = "file";

    /**
     * 检查资源是否真实存在。
     */
    boolean exists();

    /**
     * 返回指向此资源的URL。
     */
    @Nullable
    URL getURL() throws IOException;

    /**
     * 返回表示此资源的文件。
     */
    @Nullable
    File getFile() throws IOException;

}
