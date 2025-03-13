package com.example.beathelper.admin;

import com.example.beathelper.entities.BPM;
import com.example.beathelper.entities.Key;
import com.example.beathelper.entities.User;
import com.example.beathelper.services.BPMService;
import com.example.beathelper.services.KeyService;
import com.example.beathelper.services.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final BPMService bpmService;
    private final KeyService keyService;

    public AdminController(UserService userService, BPMService bpmService, KeyService keyService) {
        this.userService = userService;
        this.bpmService = bpmService;
        this.keyService = keyService;
    }

    @GetMapping("/login")
    public String adminLogin(){
        return "admin/login";
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model){
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "admin/adminDashboard";
    }

    @GetMapping("/users/view/{id}")
    public String viewUser(@PathVariable Long id, Model model){
        User user = userService.findById(id);

        if(user == null){
            return "redirect:/admin/dashboard";
        }

        model.addAttribute("user", user);
        model.addAttribute("bpms", bpmService.findBPMsByUser(user));
        model.addAttribute("keys", keyService.findKeysByUser(user));
        return "admin/adminUserView";
    }

    @GetMapping("/users/advanced/{id}")
    public String advancedUser(@PathVariable Long id, Model model,
                               @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser){
        User user = userService.findById(id);

        if(user == null){
            return "redirect:/admin/dashboard";
        }

        if(user.getEmail().equals(currentUser.getUsername())){
            return "redirect:/admin/users/view/" + id;
        }

        model.addAttribute("user", user);
        return "adminUsersAdvanced";
    }

    @PostMapping("/users/advanced/{id}")
    public String updatedAdvancedUser(@PathVariable Long id,
                                      @RequestParam("role") String role,
                                      @RequestParam(value = "ban", required = false) String ban,
                                      Model model,
                                      @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser){
        User user = userService.findById(id);

        if(user == null || user.getEmail().equals(currentUser.getUsername())){
            return "redirect:/admin/dashboard";
        }

        user.setRole(role);

        user.setBanned(ban != null && ban.equals("on"));
        userService.update(user);
        return "redirect:/admin/users/view/" + id;
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id,
                             @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser){
        User user = userService.findById(id);

        if(user != null && !user.getEmail().equals(currentUser.getUsername())){
            userService.softDelete(user);
        }
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/users/{userId}/bpms/edit/{bpmId}")
    public String editUserBPM(@PathVariable Long userId,
                              @PathVariable Long bpmId,
                              Model model){
        User user = userService.findById(userId);
        if(user == null){
            return "redirect:/admin/dashboard";
        }

        BPM bpm = bpmService.findById(bpmId);
        if(bpm == null || !bpm.getCreatedBy().getId().equals(userId)){
            return "redirect:/admin/users/view/" + userId;
        }

        model.addAttribute("bpm", bpm);
        model.addAttribute("userId", userId);
        return "admin/adminBPMEdit";
    }

    @PostMapping("/users/{userId}/bpms/{bpmId}")
    public String updateUserBPM(@PathVariable Long userId,
                                @PathVariable Long bpmId,
                                @Valid @ModelAttribute("bpm") BPM updatedBPM,
                                BindingResult result,
                                Model model,
                                @RequestParam(value = "min", required = false) Integer min,
                                @RequestParam(value = "max", required = false) Integer max){
        BPM existingBPM = bpmService.findById(bpmId);

        if(existingBPM == null || !existingBPM.getCreatedBy().getId().equals(userId)){
            return "redirect:/admin/users/view/" + userId;
        }

        if(result.hasErrors()){
            return "admin/adminBPMEdit";
        }

        existingBPM.setBpmValue(bpmService.generateRandomBPMValue(min, max));
        bpmService.updateBPM(existingBPM);
        return "redirect:/admin/users/view/" + userId;
    }

    @GetMapping("/users/{userId}/bpms/delete/{bpmId}")
    public String deleteUserBPM(@PathVariable Long userId, @PathVariable Long bpmId){
        BPM bpm = bpmService.findById(bpmId);

        if(bpm != null && bpm.getCreatedBy().getId().equals(userId)){
            bpmService.deleteBPM(bpmId);
        }

        return "redirect:/admin/users/view/" + userId;
    }

    @GetMapping("/users/{userId}/keys/{keyId}")
    public String editUserKey(@PathVariable Long userId,
                              @PathVariable Long keyId,
                              Model model){
        User user = userService.findById(userId);

        if(user == null){
            return "redirect:/admin/dashboard";
        }

        Key key = keyService.findById(keyId);
        if(key == null || !key.getCreatedBy().getId().equals(userId)){
            return "redirect:/admin/users/view/" + userId;
        }

        model.addAttribute("key", key);
        model.addAttribute("userId", userId);
        return "admin/adminKeyEdit";
    }

    @PostMapping("/users/{userId}/keys/{keyId}")
    public String updateUserKey(@PathVariable Long userId,
                                @PathVariable Long keyId,
                                @Valid @ModelAttribute("key") Key updatedKey,
                                BindingResult result,
                                Model model){
        Key existingKey = keyService.findById(keyId);

        if(existingKey == null || !existingKey.getCreatedBy().getId().equals(userId)){
            return "redirect:/admin/users/view/" + userId;
        }

        if(result.hasErrors()){
            return "admin/adminKeyEdit";
        }

        existingKey.setName(updatedKey.getName());
        existingKey.setRelatedKeys(updatedKey.getRelatedKeys());
        keyService.updateKey(existingKey);
        return "redirect:/admin/users/view/" + userId;
    }

    @GetMapping("/users/{userId}/keys/delete/{keyId}")
    public String deleteUserKey(@PathVariable Long userId, @PathVariable Long keyId){
        Key key = keyService.findById(keyId);

        if(key != null && key.getCreatedBy().getId().equals(userId)){
            keyService.deleteKey(key);
        }
        return "redirect:/admin/users/view/" + userId;
    }
}
