package org.microprofileext.openapi.swaggerui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Test all the types of paths
 * @author Phillip Kruger (phillip.kruger@gmail.com)
 */
public class RequestInfoTest {
    
    @Test
    public void testContextPathWithRestPath() {
        String path = "/somecontext/api";
        
        RequestInfo requestInfo = new RequestInfo();
        String context = requestInfo.getContextPath(path);
        assertEquals("/somecontext", context);
        
        String rest = requestInfo.getRestPath(path);
        assertEquals("/api", rest);
    }

    @Test
    public void testWhenNoContextPathButWithRestPath() {
        String path = "/api";
        
        RequestInfo requestInfo = new RequestInfo();
        String context = requestInfo.getContextPath(path);
        assertEquals("", context);
        
        String rest = requestInfo.getRestPath(path);
        assertEquals("/api", rest);
    }
    
    @Test
    public void testWhenContextPathButNoRestPath() {
        String path = "/somecontext";
        
        RequestInfo requestInfo = new RequestInfo();
        String context = requestInfo.getContextPath(path);
        assertEquals("", context);
        
        String rest = requestInfo.getRestPath(path);
        assertEquals("/somecontext", rest);
    }
    
    @Test
    public void testWhenNoContextPathAndNoRestPath() {
        String path = "/";
        
        RequestInfo requestInfo = new RequestInfo();
        String context = requestInfo.getContextPath(path);
        assertEquals("", context);
        
        String rest = requestInfo.getRestPath(path);
        assertEquals("/", rest);
    }
    
    @Test
    public void testWhenNoContextPathAndNoRestPath2() {
        String path = "";
        
        RequestInfo requestInfo = new RequestInfo();
        String context = requestInfo.getContextPath(path);
        assertEquals("", context);
        
        String rest = requestInfo.getRestPath(path);
        assertEquals("/", rest);
    }
}
