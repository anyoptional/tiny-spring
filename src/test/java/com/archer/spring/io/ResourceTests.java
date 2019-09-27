/*
 * Github: https://github.com/AnyOptional
 * Created by Archer on 2019/9/26.
 * All rights reserved.
 */

package com.archer.spring.io;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class ResourceTests {

    @Test(expected = NullPointerException.class)
    public void testLoadFromEmptyPath() {
        Resource resource = new ClassPathResource(null);
    }

    @Test(expected = IOException.class)
    public void testLoadFromFileSystem() throws Exception {
        String filePath = ResourceTests.class.getClassLoader()
                .getResource("config.xml")
                .getPath();
        System.out.println(filePath);

        Resource resource = new ClassPathResource(filePath);
        assertNotNull(resource);
        assertFalse(resource.exists());
        assertNull(resource.getURL());
        assertNull(resource.getFile());
    }

    @Test
    public void testLoadFromClassPath() throws IOException {
        Resource resource = new ClassPathResource("config.xml");
        assertNotNull(resource);
        assertTrue(resource.exists());
        assertNotNull(resource.getFile());
        assertNotNull(resource.getURL());
    }

    @Test
    public void testLoadFromClassPathWithPrefix1() throws IOException {
        Resource resource = new ClassPathResource("classpath:config.xml");
        assertNotNull(resource);
        assertTrue(resource.exists());
        assertNotNull(resource.getFile());
        assertNotNull(resource.getURL());
    }

    @Test
    public void testLoadFromClassPathWithPrefix2() throws IOException {
        Resource resource = new ClassPathResource("/config.xml");
        assertNotNull(resource);
        assertTrue(resource.exists());
        assertNotNull(resource.getFile());
        assertNotNull(resource.getURL());
    }

    @Test
    public void testLoadFromClassPathWithPrefix3() throws IOException {
        Resource resource = new ClassPathResource("classpath:/config.xml");
        assertNotNull(resource);
        assertTrue(resource.exists());
        assertNotNull(resource.getFile());
        assertNotNull(resource.getURL());
    }
}
