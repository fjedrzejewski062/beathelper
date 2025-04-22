package com.example.beathelper.controller;

import com.example.beathelper.admin.AdminController;
import com.example.beathelper.entities.BPM;
import com.example.beathelper.entities.Key;
import com.example.beathelper.entities.User;
import com.example.beathelper.enums.KeyType;
import com.example.beathelper.enums.UserType;
import com.example.beathelper.services.BPMService;
import com.example.beathelper.services.KeyService;
import com.example.beathelper.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private BPMService bpmService;

    @MockBean
    private KeyService keyService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setUserType(UserType.ARTIST);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return admin dashboard with user list")
    void shouldReturnAdminDashboard() throws Exception {
        when(userService.findFilteredUsers(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(testUser)));

        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/adminDashboard"))
                .andExpect(model().attributeExists("users", "totalPages", "currentPage"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should redirect when BPM min is greater than max")
    void shouldRedirectOnInvalidBPMRange() throws Exception {
        when(userService.findById(1L)).thenReturn(testUser);

        mockMvc.perform(get("/admin/users/viewbpms/1")
                        .param("min", "150")
                        .param("max", "100"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users/viewbpms/1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should load advanced settings page for user")
    void shouldLoadAdvancedSettingsPage() throws Exception {
        when(userService.findById(1L)).thenReturn(testUser);

        mockMvc.perform(get("/admin/users/advanced/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/adminUserAdvanced"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    @DisplayName("Should not allow editing your own advanced settings")
    void shouldNotAllowAdvancedSettingsOnYourself() throws Exception {
        testUser.setEmail("admin@example.com");
        when(userService.findById(1L)).thenReturn(testUser);

        mockMvc.perform(get("/admin/users/advanced/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Should update user's advanced settings")
    void shouldUpdateAdvancedUserSettings() throws Exception {
        testUser.setEmail("user@example.com");
        when(userService.findById(1L)).thenReturn(testUser);

        mockMvc.perform(post("/admin/users/advanced/1")
                        .param("role", "USER")
                        .param("ban", "on")
                        .param("userType", "BEATMAKER")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"));

        verify(userService).update(any(User.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should soft delete user")
    void shouldDeleteUser() throws Exception {
        when(userService.findById(1L)).thenReturn(testUser);

        mockMvc.perform(post("/admin/users/delete/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"));

        verify(userService).softDelete(testUser);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should load edit BPM page for user")
    void shouldEditUserBPM() throws Exception {
        BPM bpm = new BPM();
        bpm.setId(2L);
        bpm.setCreatedBy(testUser);
        when(userService.findById(1L)).thenReturn(testUser);
        when(bpmService.findById(2L)).thenReturn(bpm);

        mockMvc.perform(get("/admin/users/1/bpms/edit/2"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/adminBPMEdit"))
                .andExpect(model().attributeExists("bpm", "userId"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should update BPM for user")
    void shouldUpdateUserBPM() throws Exception {
        BPM bpm = new BPM();
        bpm.setId(2L);
        bpm.setCreatedBy(testUser);
        when(bpmService.findById(2L)).thenReturn(bpm);
        when(userService.findById(1L)).thenReturn(testUser);

        mockMvc.perform(post("/admin/users/1/bpms/edit/2")
                        .param("min", "100")
                        .param("max", "120")
                        .flashAttr("bpm", bpm)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users/viewbpms/1"));

        verify(bpmService).updateBPM(any(BPM.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should delete BPM for user")
    void shouldDeleteUserBPM() throws Exception {
        BPM bpm = new BPM();
        bpm.setId(2L);
        bpm.setCreatedBy(testUser);
        when(bpmService.findById(2L)).thenReturn(bpm);

        mockMvc.perform(get("/admin/users/1/bpms/delete/2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users/viewbpms/1"));

        verify(bpmService).deleteBPM(2L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should load edit Key page for user")
    void shouldEditUserKey() throws Exception {
        Key key = new Key();
        key.setId(1L);
        key.setCreatedBy(testUser);

        when(userService.findById(1L)).thenReturn(testUser);
        when(keyService.findById(1L)).thenReturn(key);

        mockMvc.perform(get("/admin/users/1/keys/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/adminKeyEdit"))
                .andExpect(model().attributeExists("key", "userId"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should update Key for user")
    void shouldUpdateUserKey() throws Exception {
        Key key = new Key();
        key.setId(1L);
        key.setCreatedBy(testUser);
        key.setRelatedKeys(new ArrayList<>());

        when(keyService.findById(1L)).thenReturn(key);
        when(keyService.getRandomKeyType(anyString())).thenReturn(KeyType.C_MAJOR);
        when(keyService.findRelatedKeys(KeyType.C_MAJOR)).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/admin/users/1/keys/edit/1")
                        .param("type", "C_MAJOR")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users/viewkeys/1"));

        verify(keyService).updateKey(key);
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should delete Key for user")
    void shouldDeleteUserKey() throws Exception {
        Key key = new Key();
        key.setId(1L);
        key.setCreatedBy(testUser);

        when(keyService.findById(1L)).thenReturn(key);

        mockMvc.perform(get("/admin/users/1/keys/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users/viewkeys/1"));

        verify(keyService).deleteKey(key);
    }
}
