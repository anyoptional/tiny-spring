/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/10/8.
 *  All rights reserved.
 */

package com.archer.spring.factory.pojo;

import com.archer.spring.factory.BeanFactory;
import com.archer.spring.factory.BeanFactoryAware;
import com.archer.spring.factory.BeanNameAware;

// 腾讯服务需要Q币初始化，2333
public class Tencent implements BeanNameAware, BeanFactoryAware {

    private QQCoin coin;

    public Tencent(QQCoin coin) {
        this.coin = coin;
    }

    public QQCoin getCoin() {
        return coin;
    }

    public void setCoin(QQCoin coin) {
        this.coin = coin;
    }

    @Override
    public String toString() {
        return "Tencent{" +
                "coin=" + coin +
                '}';
    }

    @Override
    public void setBeanFactory(BeanFactory factory) {
        System.out.println("Tencent invoke setBeanFactory " + factory);
    }

    @Override
    public void setBeanName(String beanName) {
        System.out.println("Tencent invoke setBeanName " + beanName);
    }
}
