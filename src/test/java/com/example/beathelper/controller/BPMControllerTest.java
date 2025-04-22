package com.example.beathelper.controller;

import com.example.beathelper.controllers.BPMController;
import com.example.beathelper.entities.BPM;
import com.example.beathelper.entities.User;
import com.example.beathelper.services.BPMService;
import com.example.beathelper.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(BPMController.class)
public class BPMControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BPMService bpmService;

    @MockBean
    private UserService userService;

    private User mockUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        user.setProfileImage("/img/default_profile.png");
        user.setRole("USER");
        user.setBanned(false);
        user.setDeleted(false);
        user.setRegistrationDate(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        return user;
    }

    @Test
    @DisplayName("GET /mybpms/random - displays random BPM form")
    public void testShowRandomBPM() throws Exception {
        mockMvc.perform(get("/mybpms/random"))
                .andExpect(status().isOk())
                .andExpect(view().name("randomBPM"))
                .andExpect(model().attributeExists("bpm"));
    }

    @Test
    @DisplayName("POST /mybpms/random - successfully generates random BPM")
    @WithMockUser(username = "test@example.com")
    public void testRandomBPM() throws Exception {
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser()));

        mockMvc.perform(post("/mybpms/random").param("min", "60").param("max", "120"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mybpms"));

        verify(bpmService).randomBPM(any(User.class), eq(60), eq(120));
    }

    @Test
    @DisplayName("POST /mybpms/random - validation fails due to invalid BPM range")
    @WithMockUser(username = "test@example.com")
    public void testRandomBPMValidationFail() throws Exception {
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser()));
        doThrow(new IllegalArgumentException("...")).when(bpmService).randomBPM(any(), eq(130), eq(100));

        mockMvc.perform(post("/mybpms/random").param("min", "130").param("max", "100"))
                .andExpect(status().isOk())
                .andExpect(view().name("randomBPM"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @DisplayName("GET /mybpms - fetches user BPM list")
    @WithMockUser(username = "test@example.com")
    public void testMyBPMs() throws Exception {
        User user = mockUser();
        Page<BPM> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);

        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(bpmService.findFilteredBPMs(eq(user), any(), any(), any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/mybpms"))
                .andExpect(status().isOk())
                .andExpect(view().name("myBPMs"))
                .andExpect(model().attributeExists("bpms", "totalPages", "currentPage"));
    }

    @Test
    @DisplayName("GET /mybpms/edit/{id} - displays BPM edit form")
    @WithMockUser(username = "test@example.com")
    public void testShowEditBPM() throws Exception {
        BPM bpm = new BPM();
        bpm.setId(1L);
        bpm.setCreatedBy(mockUser());

        when(bpmService.findById(1L)).thenReturn(bpm);

        mockMvc.perform(get("/mybpms/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("editBPM"))
                .andExpect(model().attributeExists("bpm"));
    }

    @Test
    @DisplayName("POST /mybpms/edit/{id} - successfully edits BPM")
    @WithMockUser(username = "test@example.com")
    public void testEditBPM() throws Exception {
        BPM bpm = new BPM();
        bpm.setId(1L);
        bpm.setCreatedBy(mockUser());

        when(bpmService.findById(1L)).thenReturn(bpm);
        when(bpmService.generateRandomBPMValue(60, 120)).thenReturn(90);

        mockMvc.perform(post("/mybpms/edit/1").param("min", "60").param("max", "120"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mybpms"));

        verify(bpmService).updateBPM(any(BPM.class));
    }

    @Test
    @DisplayName("POST /mybpms/edit/{id} - validation fails due to invalid range")
    @WithMockUser(username = "test@example.com")
    public void testEditBPMValidationFail() throws Exception {
        BPM bpm = new BPM();
        bpm.setId(1L);
        bpm.setCreatedBy(mockUser());

        when(bpmService.findById(1L)).thenReturn(bpm);
        doThrow(new IllegalArgumentException()).when(bpmService).generateRandomBPMValue(120, 60);

        mockMvc.perform(post("/mybpms/edit/1").param("min", "120").param("max", "60"))
                .andExpect(status().isOk())
                .andExpect(view().name("editBPM"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @DisplayName("GET /mybpms/delete/{id} - deletes user's BPM")
    @WithMockUser(username = "test@example.com")
    public void testDeleteBPM() throws Exception {
        BPM bpm = new BPM();
        bpm.setId(1L);
        bpm.setCreatedBy(mockUser());

        when(bpmService.findById(1L)).thenReturn(bpm);

        mockMvc.perform(get("/mybpms/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mybpms"));

        verify(bpmService).deleteBPM(1L);
    }
}
