/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/9/26.
 *  All rights reserved.
 */

package com.archer.spring.factory;

import com.archer.spring.pojo.Car;
import com.archer.spring.pojo.Person;
import org.junit.Test;
import static org.junit.Assert.*;

public class TypeConverterTests {

    @Test
    public void test0() {
        BeanWrapper wrapper = new BeanWrapper(Car.class);
        wrapper.setPropertyValue("price", "150000");
        Car car = (Car) wrapper.getWrappedInstance();
        assertEquals(car.getPrice(), 150000, 0);
        wrapper.setPropertyValue("brand", "BYD");
        assertEquals(car.getBrand(), "BYD");
    }

    @Test
    public void test1() {
        Person saber = new Person();
        BeanWrapper wrapper = new BeanWrapper(saber);
        wrapper.setPropertyValue("name", "saber");
        wrapper.setPropertyValue("age", "22");
        assertEquals(saber.getAge(), 22);
        assertEquals(saber.getName(), "saber");
    }
}
