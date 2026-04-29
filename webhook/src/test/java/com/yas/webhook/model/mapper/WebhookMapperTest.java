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
        Webhook webhook = new Webhook();
        webhook.setId(1L);
        webhook.setPayloadUrl("url");
        webhook.setIsActive(true);
        webhook.setSecret("secret");

        WebhookVm vm = webhookMapper.toWebhookVm(webhook);
        assertThat(vm.getId()).isEqualTo(1L);
        assertThat(vm.getPayloadUrl()).isEqualTo("url");
        assertThat(vm.getIsActive()).isTrue();
    }

    @Test
    void toWebhookEventVms_shouldMapList() {
        WebhookEvent event = new WebhookEvent();
        event.setEventId(1L);

        List<EventVm> vms = webhookMapper.toWebhookEventVms(List.of(event));
        assertThat(vms).hasSize(1);
        assertThat(vms.getFirst().getId()).isEqualTo(1L);
    }

    @Test
    void toWebhookListGetVm_shouldMapPage() {
        Webhook webhook = new Webhook();
        webhook.setId(1L);

        Page<Webhook> page = new PageImpl<>(List.of(webhook), PageRequest.of(0, 10), 1);
        WebhookListGetVm result = webhookMapper.toWebhookListGetVm(page, 0, 10);
        assertThat(result.getWebhooks()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void toWebhookDetailVm_shouldMapDetail() {
        Webhook webhook = new Webhook();
        webhook.setId(1L);
        webhook.setPayloadUrl("url");

        WebhookDetailVm result = webhookMapper.toWebhookDetailVm(webhook);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getPayloadUrl()).isEqualTo("url");
    }
}
