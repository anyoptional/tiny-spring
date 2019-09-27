/*
 * Github: https://github.com/AnyOptional
 * Created by Archer on 2019/9/26.
 * All rights reserved.
 */

package com.archer.spring.io;

import com.sun.istack.internal.Nullable;

import java.io.IOException;
import java.io.InputStream;

/**
 * Spring Resource接口的父接口。
 * 资源最终是需要被读取的，这个接口
 * 为资源建立了一个更高的抽象。
 */
public interface InputStreamSource {

    /**
     * 返回代表资源的InputStream。
     */
    @Nullable
    InputStream getInputStream() throws IOException;

}
