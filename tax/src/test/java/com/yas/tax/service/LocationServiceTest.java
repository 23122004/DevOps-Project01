package com.yas.tax.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.yas.tax.config.ServiceUrlConfig;
import com.yas.tax.viewmodel.location.StateOrProvinceAndCountryGetNameVm;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @Mock
    private ServiceUrlConfig serviceUrlConfig;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private LocationService locationService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getStateOrProvinceAndCountryNames_shouldReturnList() {
        List<Long> ids = List.of(1L);
        List<StateOrProvinceAndCountryGetNameVm> expectedResponse = List.of(
            new StateOrProvinceAndCountryGetNameVm(1L, "State", "Country")
        );

        when(serviceUrlConfig.location()).thenReturn("http://location");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getTokenValue()).thenReturn("mock-token");

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(expectedResponse);

        List<StateOrProvinceAndCountryGetNameVm> result = locationService.getStateOrProvinceAndCountryNames(ids);

        assertThat(result).isEqualTo(expectedResponse);
    }

    @Test
    void handleLocationNameListFallback_shouldCallHandleTypedFallback() {
        Throwable throwable = new RuntimeException("Test");
        assertThatThrownBy(() -> locationService.handleLocationNameListFallback(throwable))
            .isEqualTo(throwable);
    }
}
