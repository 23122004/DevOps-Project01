package com.yas.webhook.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import org.junit.jupiter.api.Test;

class HmacUtilsTest {

    @Test
    void hash_shouldReturnDeterministicHash() throws NoSuchAlgorithmException, InvalidKeyException {
        String data = "some data";
        String key = "secret";
        String hash1 = HmacUtils.hash(data, key);
        String hash2 = HmacUtils.hash(data, key);
        assertThat(hash1).isEqualTo(hash2);
    }
}
