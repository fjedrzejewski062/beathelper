package com.example.beathelper.controller;

import com.example.beathelper.controllers.StripeController;
import com.example.beathelper.services.StripeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.*;

@SpringBootTest
public class StripeControllerTest {

    @Mock
    private StripeService stripeService;

    private MockMvc mockMvc;

    @InjectMocks
    private StripeController stripeController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(stripeController).build();
    }

    @Test
    @DisplayName("POST /api/donate - successfully creates checkout session with valid amount")
    public void testCreateCheckoutSession_Success() throws Exception {
        String mockCheckoutUrl = "http://checkout-session-url.com";
        when(stripeService.createCheckoutSession(any(), any(), any())).thenReturn(mockCheckoutUrl);

        String donationRequestJson = "{ \"amount\": \"100\" }";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/donate")
                        .contentType("application/json")
                        .content(donationRequestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(mockCheckoutUrl));

        verify(stripeService, times(1)).createCheckoutSession(any(), any(), any());
    }

    @Test
    @DisplayName("POST /api/donate - returns BadRequest for invalid amount")
    public void testCreateCheckoutSession_InvalidAmount() throws Exception {
        String donationRequestJson = "{ \"amount\": \"0\" }";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/donate")
                        .contentType("application/json")
                        .content(donationRequestJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Amount must be greater than zero."));

        verify(stripeService, never()).createCheckoutSession(any(), any(), any());
    }

    @Test
    @DisplayName("POST /api/donate - returns InternalServerError on Stripe service error")
    public void testCreateCheckoutSession_InternalServerError() throws Exception {
        when(stripeService.createCheckoutSession(any(), any(), any())).thenThrow(new RuntimeException("Stripe Error"));

        String donationRequestJson = "{ \"amount\": \"100\" }";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/donate")
                        .contentType("application/json")
                        .content(donationRequestJson))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Error: Stripe Error"));

        verify(stripeService, times(1)).createCheckoutSession(any(), any(), any());
    }
}
