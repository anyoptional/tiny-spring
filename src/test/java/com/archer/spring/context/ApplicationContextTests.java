/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/10/12.
 *  All rights reserved.
 */

package com.archer.spring.context;

import com.archer.spring.context.support.ClassPathXMLApplicationContext;
import com.archer.spring.pojo.Capital;
import org.junit.Test;

import static org.junit.Assert.*;

public class ApplicationContextTests {

    @Test
    public void testApplicationContext() {

        ApplicationContext context = new ClassPathXMLApplicationContext("classpath:config.xml");
        Capital capital = context.getBean("capital", Capital.class);
        assertNotNull(capital);
        System.out.println(capital);

    }
}
