package com.yas.webhook.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.webhook.model.viewmodel.webhook.WebhookDetailVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookListGetVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookPostVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookVm;
import com.yas.webhook.service.WebhookService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

@ExtendWith(MockitoExtension.class)
class WebhookControllerTest {

    @Mock
    private WebhookService webhookService;

    @InjectMocks
    private WebhookController webhookController;

    private WebhookDetailVm webhookDetailVm;
    private WebhookVm webhookVm;
    private WebhookListGetVm webhookListGetVm;

    @BeforeEach
    void setUp() {
        webhookDetailVm = new WebhookDetailVm();
        webhookDetailVm.setId(1L);
        webhookDetailVm.setPayloadUrl("https://example.com/webhook");

        webhookVm = new WebhookVm();
        webhookListGetVm = WebhookListGetVm.builder().build();
    }

    @Test
    void getPageableWebhooks_shouldReturnOk() {
        when(webhookService.getPageableWebhooks(anyInt(), anyInt())).thenReturn(webhookListGetVm);

        ResponseEntity<WebhookListGetVm> response = webhookController.getPageableWebhooks(0, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(webhookListGetVm);
    }

    @Test
    void listWebhooks_shouldReturnOk() {
        when(webhookService.findAllWebhooks()).thenReturn(List.of(webhookVm));

        ResponseEntity<List<WebhookVm>> response = webhookController.listWebhooks();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void getWebhook_shouldReturnOk() {
        when(webhookService.findById(1L)).thenReturn(webhookDetailVm);

        ResponseEntity<WebhookDetailVm> response = webhookController.getWebhook(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(webhookDetailVm);
    }

    @Test
    void createWebhook_shouldReturnCreated() {
        WebhookPostVm postVm = new WebhookPostVm("https://example.com", "secret", "application/json", true, List.of());
        when(webhookService.create(postVm)).thenReturn(webhookDetailVm);

        ResponseEntity<WebhookDetailVm> response = webhookController.createWebhook(
            postVm, UriComponentsBuilder.newInstance());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(webhookDetailVm);
    }

    @Test
    void updateWebhook_shouldReturnNoContent() {
        WebhookPostVm postVm = new WebhookPostVm("https://example.com/updated", "secret", "application/json", true, List.of());
        doNothing().when(webhookService).update(any(), anyLong());

        ResponseEntity<Void> response = webhookController.updateWebhook(1L, postVm);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(webhookService).update(postVm, 1L);
    }

    @Test
    void deleteWebhook_shouldReturnNoContent() {
        doNothing().when(webhookService).delete(anyLong());

        ResponseEntity<Void> response = webhookController.deleteWebhook(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(webhookService).delete(1L);
    }
}
