/*
 * Github: https://github.com/AnyOptional
 * Created by Archer on 2019/9/26.
 * All rights reserved.
 */

package com.archer.spring.utils;

import com.archer.spring.io.Resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

public abstract class ResourcesUtils {

    /**
     * 通过URL生成File。
     */
    public static File getFile(URL resourceURL) throws FileNotFoundException {
        Objects.requireNonNull(resourceURL, "URL不能为空");
        if (!Resource.FILESYSTEM_URL_PROTOCOL.equals(resourceURL.getProtocol())) {
            throw new FileNotFoundException("[" + resourceURL + "]不是一个文件系统路径");
        }
        try {
            return new File(toURI(resourceURL).getSchemeSpecificPart());
        } catch (URISyntaxException ex) {
            return new File(resourceURL.getFile());
        }
    }

    /**
     * 通过URL生成URI。
     */
    public static URI toURI(URL url) throws URISyntaxException {
        Objects.requireNonNull(url, "URL不能为空");
        return toURI(url.toString());
    }

    /**
     * 通过URL生成URI。
     */
    public static URI toURI(String location) throws URISyntaxException {
        return new URI(StringUtils.replace(location, " ", "%20"));
    }
}
