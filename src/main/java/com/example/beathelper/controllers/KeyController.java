package com.example.beathelper.controllers;

import com.example.beathelper.entities.BPM;
import com.example.beathelper.entities.Key;
import com.example.beathelper.entities.User;
import com.example.beathelper.services.KeyService;
import com.example.beathelper.services.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class KeyController {
    private final KeyService keyService;
    private final UserService userService;

    public KeyController(KeyService keyService, UserService userService) {
        this.keyService = keyService;
        this.userService = userService;
    }

    @GetMapping("/mykeys")
    public String myKeys(Model model, @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser){
        String email = currentUser.getUsername();
        User user = userService.findByEmail(email).orElse(null);
        model.addAttribute("user", user);
        return "myKeys";
    }

    @GetMapping("/mykeys/random")
    public String showRandomKey(Model model){
        model.addAttribute("key", new Key());
        return "randomKey";
    }

    @PostMapping("/mykeys/random")
    public String randomKey(BindingResult result,
                            @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser,
                            Model model,
                            @RequestParam(value = "type", required = false) String type){
        if(result.hasErrors()){
            return "randomKey";
        }

        String email = currentUser.getUsername();
        User user = userService.findByEmail(email).orElse(null);

        keyService.randomKey(user, type);
        return "redirect:/mykeys";
    }

    @GetMapping("/mykeys/edit/{id}")
    public String showEditKey(@PathVariable Long id, Model model,
                              @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser){
        Key key = keyService.findById(id);

        if(key == null || !key.getCreatedBy().getEmail().equals(currentUser.getUsername())){
            return "redirect:/mykeys";
        }

        model.addAttribute("key", key);
        return "editKey";
    }

    @PostMapping("/mykeys/edit/{id}")
    public String editKey(@PathVariable Long id,
                          @Valid @ModelAttribute("key") Key key,
                          BindingResult result,
                          @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser,
                          Model model){
        Key existingKey = keyService.findById(id);

        if(existingKey == null || !existingKey.getCreatedBy().getEmail().equals(currentUser.getUsername())){
            return "redirect:/mykeys";
        }

        if(result.hasErrors()){
            model.addAttribute("key", existingKey);
            return "editKey";
        }

        existingKey.setName(key.getName());
        existingKey.setRelatedKeys(key.getRelatedKeys());

        keyService.updateKey(existingKey);
        return "redirect:/mykeys";
    }

    @GetMapping("/mykeys/delete/{id}")
    public String deleteKey(@PathVariable Long id,
                            @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser){
        Key key = keyService.findById(id);
        if(key != null && key.getCreatedBy().getEmail().equals(currentUser.getUsername())){
            keyService.deleteKey(key);
        }
        return "redirect:/mykeys";
    }

}
