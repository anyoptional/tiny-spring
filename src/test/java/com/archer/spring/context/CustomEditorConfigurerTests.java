/*
 *  Github: https://github.com/AnyOptional
 *  Created by Archer on 2019/10/12.
 *  All rights reserved.
 */

package com.archer.spring.context;

import com.archer.spring.context.processor.DateHolder;
import com.archer.spring.context.support.ClassPathXMLApplicationContext;
import org.junit.Test;
import static org.junit.Assert.*;

public class CustomEditorConfigurerTests {

    @Test
    public void testCustomEditorConfigurer() {
        ApplicationContext context = new ClassPathXMLApplicationContext("context.xml");
        DateHolder dateHolder = context.getBean("date-holder", DateHolder.class);
        assertNotNull(dateHolder);
        assertNotNull(dateHolder.getDate());
        System.out.println(dateHolder);
    }
}
