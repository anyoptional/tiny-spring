/*
 * Github: https://github.com/AnyOptional
 * Created by Archer on 2019/9/26.
 * All rights reserved.
 */

package com.archer.spring.pojo;

import java.util.Arrays;

// 人名
public class People {

    // 人民由很多很多人组成
    private Person[] citizens;

    public Person[] getCitizens() {
        return citizens;
    }

    public void setCitizens(Person[] citizens) {
        this.citizens = citizens;
    }

    @Override
    public String toString() {
        return "People{" +
                "citizens=" + Arrays.toString(citizens) +
                '}';
    }
}
