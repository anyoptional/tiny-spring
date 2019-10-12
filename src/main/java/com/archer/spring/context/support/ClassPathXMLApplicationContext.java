/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/10/12.
 *  All rights reserved.
 */

package com.archer.spring.context.support;

import com.archer.spring.io.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClassPathXMLApplicationContext extends AbstractXMLApplicationContext {

    /// MARK - Properties

    private String[] configLocations;

    /// MARK - Initializers

    public ClassPathXMLApplicationContext(String... configLocations) {
        Objects.requireNonNull(configLocations, "不能为空");
        if (configLocations.length == 0) {
            throw new IllegalArgumentException("至少要指定一个配置文件");
        }
        this.configLocations = getResolvedConfigLocations(configLocations);
        refresh();
    }

    /// MARK - AbstractXMLApplicationContext

    @Override
    protected String[] getConfigLocations() {
        return configLocations;
    }

    private String[] getResolvedConfigLocations(String... configLocations) {
        List<String> resolvedLocations = new ArrayList<>();
        for (String location : configLocations) {
            if (!location.startsWith(Resource.CLASSPATH_URL_PREFIX)) {
                resolvedLocations.add(Resource.CLASSPATH_URL_PREFIX + location);
            }
        }
        return resolvedLocations.toArray(new String[0]);
    }
}
