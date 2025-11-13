package com.property_broker.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
 
class ResourceNotFoundExceptionTest {
 
    @Test
    void testExceptionMessage() {
        String msg = "Resource not found";
        ResourceNotFoundException ex = new ResourceNotFoundException(msg);
 
        assertEquals(msg, ex.getMessage(), "Exception message should match");
    }
 
    @Test
    void testIsRuntimeException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Error!");
        assertTrue(ex instanceof RuntimeException, "Exception must be a RuntimeException");
    }
 
    @Test
    void testThrowingException() {
        assertThrows(ResourceNotFoundException.class, () -> {
            throw new ResourceNotFoundException("Test exception");
        });
    }
}
