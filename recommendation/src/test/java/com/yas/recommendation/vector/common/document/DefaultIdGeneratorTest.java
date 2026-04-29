package com.yas.recommendation.vector.common.document;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class DefaultIdGeneratorTest {

    @Test
    void generateId_shouldReturnDeterministicUuidString() {
        DefaultIdGenerator generator1 = new DefaultIdGenerator("PROD", 1L);
        DefaultIdGenerator generator2 = new DefaultIdGenerator("PROD", 1L);

        String id1 = generator1.generateId();
        String id2 = generator2.generateId();

        assertThat(id1).isEqualTo(id2);
        assertThat(UUID.fromString(id1)).isNotNull();
    }

    @Test
    void generateId_withDifferentValues_shouldReturnDifferentIds() {
        DefaultIdGenerator generator1 = new DefaultIdGenerator("PROD", 1L);
        DefaultIdGenerator generator2 = new DefaultIdGenerator("PROD", 2L);

        assertThat(generator1.generateId()).isNotEqualTo(generator2.generateId());
    }
}
