package com.audition.common.enumeration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BusinessErrorCodeTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void itShouldGetCode() {
        // Given
        final BusinessErrorCode businessErrorCode = BusinessErrorCode.RESOURCE_NOT_FOUND;
        final String expectedCode = BusinessErrorCode.RESOURCE_NOT_FOUND.getCode();
        // When
        final String actualCode = businessErrorCode.getCode();

        // Then
        assertEquals(expectedCode, actualCode);

    }
}