package com.yas.webhook.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.webhook.integration.api.WebhookApi;
import com.yas.webhook.model.Webhook;
import com.yas.webhook.model.WebhookEvent;
import com.yas.webhook.model.WebhookEventNotification;
import com.yas.webhook.model.dto.WebhookEventNotificationDto;
import com.yas.webhook.model.mapper.WebhookMapper;
import com.yas.webhook.model.viewmodel.webhook.WebhookDetailVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookListGetVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookPostVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookVm;
import com.yas.webhook.repository.EventRepository;
import com.yas.webhook.repository.WebhookEventNotificationRepository;
import com.yas.webhook.repository.WebhookEventRepository;
import com.yas.webhook.repository.WebhookRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class WebhookServiceFullTest {

    @Mock
    private WebhookRepository webhookRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private WebhookEventRepository webhookEventRepository;

    @Mock
    private WebhookEventNotificationRepository webhookEventNotificationRepository;

    @Mock
    private WebhookMapper webhookMapper;

    @Mock
    private WebhookApi webHookApi;

    @InjectMocks
    private WebhookService webhookService;

    private Webhook webhook;
    private WebhookDetailVm webhookDetailVm;
    private WebhookVm webhookVm;
    private WebhookListGetVm webhookListGetVm;

    @BeforeEach
    void setUp() {
        webhook = new Webhook();
        webhook.setId(1L);
        webhook.setPayloadUrl("https://example.com/webhook");
        webhook.setWebhookEvents(Collections.emptyList());

        webhookDetailVm = new WebhookDetailVm();
        webhookDetailVm.setId(1L);

        webhookVm = new WebhookVm();
        webhookListGetVm = WebhookListGetVm.builder().webhooks(List.of(webhookVm)).build();
    }

    @Test
    void getPageableWebhooks_shouldReturnWebhookListGetVm() {
        Page<Webhook> page = new PageImpl<>(List.of(webhook));
        when(webhookRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(webhookMapper.toWebhookListGetVm(page, 0, 10)).thenReturn(webhookListGetVm);

        WebhookListGetVm result = webhookService.getPageableWebhooks(0, 10);

        assertThat(result).isEqualTo(webhookListGetVm);
    }

    @Test
    void findAllWebhooks_shouldReturnAllWebhookVms() {
        when(webhookRepository.findAll(any(Sort.class))).thenReturn(List.of(webhook));
        when(webhookMapper.toWebhookVm(webhook)).thenReturn(webhookVm);

        List<WebhookVm> result = webhookService.findAllWebhooks();

        assertThat(result).hasSize(1).contains(webhookVm);
    }

    @Test
    void findById_whenExists_shouldReturnWebhookDetailVm() {
        when(webhookRepository.findById(1L)).thenReturn(Optional.of(webhook));
        when(webhookMapper.toWebhookDetailVm(webhook)).thenReturn(webhookDetailVm);

        WebhookDetailVm result = webhookService.findById(1L);

        assertThat(result).isEqualTo(webhookDetailVm);
    }

    @Test
    void findById_whenNotFound_shouldThrowNotFoundException() {
        when(webhookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> webhookService.findById(99L))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void create_withEmptyEvents_shouldSaveAndReturnDetailVm() {
        WebhookPostVm postVm = new WebhookPostVm("https://example.com", "secret", "application/json", true, Collections.emptyList());
        when(webhookMapper.toCreatedWebhook(postVm)).thenReturn(webhook);
        when(webhookRepository.save(webhook)).thenReturn(webhook);
        when(webhookMapper.toWebhookDetailVm(webhook)).thenReturn(webhookDetailVm);

        WebhookDetailVm result = webhookService.create(postVm);

        assertThat(result).isEqualTo(webhookDetailVm);
    }

    @Test
    void update_whenExists_shouldUpdateAndSave() {
        WebhookPostVm postVm = new WebhookPostVm("https://example.com/updated", "secret", "application/json", true, Collections.emptyList());
        when(webhookRepository.findById(1L)).thenReturn(Optional.of(webhook));
        when(webhookMapper.toUpdatedWebhook(webhook, postVm)).thenReturn(webhook);

        webhookService.update(postVm, 1L);

        verify(webhookRepository).save(webhook);
    }

    @Test
    void update_whenNotFound_shouldThrowNotFoundException() {
        WebhookPostVm postVm = new WebhookPostVm();
        when(webhookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> webhookService.update(postVm, 99L))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void delete_whenExists_shouldDeleteWebhookAndEvents() {
        when(webhookRepository.existsById(1L)).thenReturn(true);

        webhookService.delete(1L);

        verify(webhookEventRepository).deleteByWebhookId(1L);
        verify(webhookRepository).deleteById(1L);
    }

    @Test
    void delete_whenNotFound_shouldThrowNotFoundException() {
        when(webhookRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> webhookService.delete(99L))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void notifyToWebhook_shouldNotifyAndUpdateStatus() {
        WebhookEventNotificationDto dto = WebhookEventNotificationDto.builder()
            .notificationId(1L)
            .url("https://example.com/hook")
            .secret("secret123")
            .build();
        WebhookEventNotification notification = new WebhookEventNotification();
        when(webhookEventNotificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        webhookService.notifyToWebhook(dto);

        verify(webHookApi).notify(dto.getUrl(), dto.getSecret(), dto.getPayload());
        verify(webhookEventNotificationRepository).save(notification);
    }
}
