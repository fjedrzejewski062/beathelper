package com.example.beathelper.controller;

import com.example.beathelper.controllers.UserController;
import com.example.beathelper.entities.User;
import com.example.beathelper.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("GET /register - pokazuje formularz rejestracji")
    public void testShowRegistrationForm() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @DisplayName("POST /register - rejestracja i przekierowanie na /")
    public void testRegisterUserSuccess() throws Exception {
        when(userService.findByUsername(any(String.class))).thenReturn(Optional.empty());
        when(userService.findByEmail(any(String.class))).thenReturn(Optional.empty());

        User registeredUser = new User();
        registeredUser.setId(1L);
        registeredUser.setUsername("testuser");
        registeredUser.setEmail("testuser@example.com");
        registeredUser.setRegistrationDate(LocalDateTime.now());

        when(userService.register(any(User.class))).thenReturn(registeredUser);

        mockMvc.perform(post("/register")
                        .param("username", "testuser")
                        .param("email", "testuser@example.com")
                        .param("password", "password123")
                        .param("confirmPassword", "password123")
                        .param("terms", "on"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @DisplayName("GET /login")
    public void testLoginView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    @DisplayName("GET /myprofile")
    @WithMockUser(username = "testuser@example.com")
    public void testMyProfile() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");

        when(userService.findByEmail("testuser@example.com")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/myprofile"))
                .andExpect(status().isOk())
                .andExpect(view().name("myProfile"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @DisplayName("POST /deleteaccount")
    @WithMockUser(username = "testuser@example.com")
    public void testDeleteAccount() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("testuser@example.com");

        when(userService.findByEmail("testuser@example.com")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/deleteaccount"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/logout"));

        verify(userService).softDelete(user);
    }
}
