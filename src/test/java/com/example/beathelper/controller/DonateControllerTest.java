package com.example.beathelper.controller;

import com.example.beathelper.controllers.DonateController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class DonateControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        ViewResolver viewResolver = new InternalResourceViewResolver("/WEB-INF/views/", ".jsp");

        mockMvc = MockMvcBuilders.standaloneSetup(new DonateController())
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    @DisplayName("GET /donate - displays the donation page")
    public void testDonatePage() throws Exception {
        mockMvc.perform(get("/donate"))
                .andExpect(status().isOk())
                .andExpect(view().name("donate"));
    }

    @Test
    @DisplayName("GET /donate/success - displays donation success page with session ID")
    public void testDonationSuccess() throws Exception {
        mockMvc.perform(get("/donate/success").param("session_id", "abc123"))
                .andExpect(status().isOk())
                .andExpect(view().name("donate_success"))
                .andExpect(model().attributeExists("sessionId"))
                .andExpect(model().attribute("sessionId", "abc123"));
    }

    @Test
    @DisplayName("GET /donate/cancel - displays donation cancellation page")
    public void testDonationCancelled() throws Exception {
        mockMvc.perform(get("/donate/cancel"))
                .andExpect(status().isOk())
                .andExpect(view().name("donate_cancel"));
    }
}
