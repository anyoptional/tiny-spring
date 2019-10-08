/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/10/8.
 *  All rights reserved.
 */

package com.archer.spring.factory.pojo;

import com.archer.spring.factory.InitializingBean;

public class QQCoin implements InitializingBean {

    private int count;

    public QQCoin(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "QQCoin{" +
                "count=" + count +
                '}';
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("QQCoin invoke InitializingBean");
    }

    public void initMethod() {
        System.out.println("QQCoin invoke initMethod");
    }

}
