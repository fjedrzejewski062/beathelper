package com.example.beathelper.service;

import com.example.beathelper.services.StripeService;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StripeServiceTest {

    @Mock
    private Session sessionMock;

    @InjectMocks
    private StripeService stripeService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should create a Stripe Checkout Session successfully")
    public void testCreateCheckoutSession_Success() throws Exception {
        BigDecimal amount = BigDecimal.valueOf(100);
        String successUrl = "http://example.com/success";
        String cancelUrl = "http://example.com/cancel";

        Session session = mock(Session.class);
        when(session.getUrl()).thenReturn("http://checkout-session-url.com");

        try (MockedStatic<Session> mockedStatic = mockStatic(Session.class)) {
            mockedStatic.when(() -> Session.create(any(SessionCreateParams.class))).thenReturn(session);

            String checkoutUrl = stripeService.createCheckoutSession(amount, successUrl, cancelUrl);

            assertNotNull(checkoutUrl);
            assertEquals("http://checkout-session-url.com", checkoutUrl);
            mockedStatic.verify(() -> Session.create(any(SessionCreateParams.class)));
        }
    }
}
