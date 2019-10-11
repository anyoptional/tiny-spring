/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/10/11.
 *  All rights reserved.
 */

package com.archer.spring.context;

/**
 * ApplicationContext相关的异常。
 */
public class ApplicationContextException extends RuntimeException {

    public ApplicationContextException(String message) {
        super(message);
    }

    public ApplicationContextException(Throwable throwable) {
        super(throwable);
    }

    public ApplicationContextException(String message, Throwable cause) {
        super(message, cause);
    }

}
