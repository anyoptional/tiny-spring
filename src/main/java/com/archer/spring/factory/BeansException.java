/*
 * Github: https://github.com/AnyOptional
 * Created by Archer on 2019/9/26.
 * All rights reserved.
 */

package com.archer.spring.factory;

/**
 * IoC容器相关的异常通常是不可恢复的，
 * 因此作为unchecked exception来处理。
 *
 * 在我们的简单实现中，就不去做细分了，统一定义为BeansException。
 */
public class BeansException extends RuntimeException {

    public BeansException(String message) {
        super(message);
    }

    public BeansException(Throwable throwable) {
        super(throwable);
    }

    public BeansException(String message, Throwable cause) {
        super(message, cause);
    }

}
