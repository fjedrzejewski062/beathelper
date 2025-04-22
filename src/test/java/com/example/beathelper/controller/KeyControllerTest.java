package com.example.beathelper.controller;

import com.example.beathelper.controllers.KeyController;
import com.example.beathelper.enums.KeyType;
import com.example.beathelper.entities.Key;
import com.example.beathelper.entities.User;
import com.example.beathelper.services.KeyService;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(KeyController.class)
public class KeyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KeyService keyService;

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
    @DisplayName("GET /mykeys/random - displays the random key form")
    public void testShowRandomKey() throws Exception {
        mockMvc.perform(get("/mykeys/random"))
                .andExpect(status().isOk())
                .andExpect(view().name("randomKey"))
                .andExpect(model().attributeExists("key"));
    }

    @Test
    @DisplayName("POST /mykeys/random - successfully generates a random key")
    @WithMockUser(username = "test@example.com")
    public void testRandomKey() throws Exception {
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser()));

        mockMvc.perform(post("/mykeys/random").param("type", "major"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mykeys"));

        verify(keyService).randomKey(any(User.class), eq("major"));
    }

    @Test
    @DisplayName("GET /mykeys - successfully retrieves the list of keys")
    @WithMockUser(username = "test@example.com")
    public void testMyKeys() throws Exception {
        User user = mockUser();
        Page<Key> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);

        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(keyService.findFilteredKeys(eq(user), any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/mykeys"))
                .andExpect(status().isOk())
                .andExpect(view().name("myKeys"))
                .andExpect(model().attributeExists("keys", "totalPages", "currentPage"));
    }

    @Test
    @DisplayName("GET /mykeys/edit/{id} - valid access to key editing")
    @WithMockUser(username = "test@example.com")
    public void testShowEditKey() throws Exception {
        Key key = new Key();
        key.setId(1L);
        key.setCreatedBy(mockUser());

        when(keyService.findById(1L)).thenReturn(key);

        mockMvc.perform(get("/mykeys/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("editKey"))
                .andExpect(model().attributeExists("key"));
    }

    @Test
    @DisplayName("POST /mykeys/edit/{id} - successful key editing")
    @WithMockUser(username = "test@example.com")
    public void testEditKey() throws Exception {
        User user = mockUser();

        Key key = mock(Key.class);

        when(key.getId()).thenReturn(1L);
        when(key.getCreatedBy()).thenReturn(user);
        when(key.getRelatedKeys()).thenReturn(new ArrayList<>());

        when(keyService.findById(1L)).thenReturn(key);
        when(keyService.getRandomKeyType("C_MAJOR")).thenReturn(KeyType.C_MAJOR);
        when(keyService.findRelatedKeys(KeyType.C_MAJOR)).thenReturn(List.of());

        mockMvc.perform(post("/mykeys/edit/1").param("type", "C_MAJOR"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mykeys"));

        verify(keyService).updateKey(any(Key.class));
    }

    @Test
    @DisplayName("GET /mykeys/delete/{id} - successfully deletes the user's key")
    @WithMockUser(username = "test@example.com")
    public void testDeleteKey() throws Exception {
        Key key = new Key();
        key.setId(1L);
        key.setCreatedBy(mockUser());

        when(keyService.findById(1L)).thenReturn(key);

        mockMvc.perform(get("/mykeys/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mykeys"));

        verify(keyService).deleteKey(key);
    }
}
