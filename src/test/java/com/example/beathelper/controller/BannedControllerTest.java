package com.example.beathelper.controller;

import com.example.beathelper.controllers.BannedController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BannedControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        ViewResolver viewResolver = new InternalResourceViewResolver("/WEB-INF/views/", ".jsp");

        mockMvc = MockMvcBuilders.standaloneSetup(new BannedController())
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    @DisplayName("Should display the banned page when accessing /banned URL")
    public void testBannedPage() throws Exception {
        mockMvc.perform(get("/banned"))
                .andExpect(status().isOk())
                .andExpect(view().name("banned"));
    }
}
