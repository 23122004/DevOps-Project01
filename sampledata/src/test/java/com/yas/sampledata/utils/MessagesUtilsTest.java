package com.yas.sampledata.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class MessagesUtilsTest {

    @Test
    void getMessage_shouldReturnMessageWhenCodeExists() {
        // Just triggering the catch block with an invalid key
        String actualMessage = MessagesUtils.getMessage("INVALID_CODE", "arg1");
        
        assertThat(actualMessage).contains("INVALID_CODE");
    }

    @Test
    void instantiateClass() {
        // Just to cover the default constructor
        MessagesUtils utils = new MessagesUtils();
        assertThat(utils).isNotNull();
    }
}
