package com.yas.webhook.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.yas.webhook.model.Webhook;
import com.yas.webhook.model.WebhookEvent;
import com.yas.webhook.model.viewmodel.webhook.EventVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookDetailVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookListGetVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookVm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

class WebhookMapperTest {

    private WebhookMapperImpl webhookMapper;

    @BeforeEach
    void setUp() {
        webhookMapper = new WebhookMapperImpl();
    }

    @Test
    void toWebhookVm_shouldMapCorrectly() {
        Webhook webhook = Webhook.builder().id(1L).payloadUrl("url").isActive(true).secret("secret").build();
        WebhookVm vm = webhookMapper.toWebhookVm(webhook);
        assertThat(vm.getId()).isEqualTo(1L);
        assertThat(vm.getPayloadUrl()).isEqualTo("url");
        assertThat(vm.getIsActive()).isTrue();
    }

    @Test
    void toWebhookEventVms_shouldMapList() {
        WebhookEvent event = WebhookEvent.builder().eventId("event1").build();
        List<EventVm> vms = webhookMapper.toWebhookEventVms(List.of(event));
        assertThat(vms).hasSize(1);
        assertThat(vms.getFirst().getId()).isEqualTo("event1");
    }

    @Test
    void toWebhookListGetVm_shouldMapPage() {
        Webhook webhook = Webhook.builder().id(1L).build();
        Page<Webhook> page = new PageImpl<>(List.of(webhook), PageRequest.of(0, 10), 1);
        WebhookListGetVm result = webhookMapper.toWebhookListGetVm(page, 0, 10);
        assertThat(result.getWebhooks()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void toWebhookDetailVm_shouldMapDetail() {
        Webhook webhook = Webhook.builder().id(1L).payloadUrl("url").build();
        WebhookDetailVm result = webhookMapper.toWebhookDetailVm(webhook);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getPayloadUrl()).isEqualTo("url");
    }
}
