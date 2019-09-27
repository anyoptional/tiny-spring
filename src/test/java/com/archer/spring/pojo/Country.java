/*
 * Github: https://github.com/AnyOptional
 * Created by Archer on 2019/9/26.
 * All rights reserved.
 */

package com.archer.spring.pojo;

// 国家
public class Country {

    // 国家名称
    private String name;

    // 国际编码
    private String isoCode;

    // 国家的首都
    private Capital capital;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public Capital getCapital() {
        return capital;
    }

    public void setCapital(Capital capital) {
        this.capital = capital;
    }

    @Override
    public String toString() {
        return "Country{" +
                "name='" + name + '\'' +
                ", isoCode='" + isoCode + '\'' +
                ", capital=" + capital.getName() +
                '}';
    }
}
