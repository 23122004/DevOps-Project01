package com.yas.webhook.integration.inbound;

import static org.mockito.Mockito.verify;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.yas.webhook.service.OrderEventService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderEventInboundTest {

    @Mock
    private OrderEventService orderEventService;

    @InjectMocks
    private OrderEventInbound orderEventInbound;

    @Test
    void onOrderEvent_shouldCallService() {
        JsonNode event = new ObjectMapper().createObjectNode().put("id", 1);
        orderEventInbound.onOrderEvent(event);
        verify(orderEventService).onOrderEvent(event);
    }
}
