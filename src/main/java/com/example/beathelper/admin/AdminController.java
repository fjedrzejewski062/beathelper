package com.example.beathelper.admin;

import com.example.beathelper.entities.BPM;
import com.example.beathelper.entities.Key;
import com.example.beathelper.entities.User;
import com.example.beathelper.enums.KeyType;
import com.example.beathelper.enums.UserType;
import com.example.beathelper.services.BPMService;
import com.example.beathelper.services.KeyService;
import com.example.beathelper.services.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
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
    public String adminDashboard(Model model,
                                 RedirectAttributes redirectAttributes,
                                 @RequestParam(required = false) String name,
                                 @RequestParam(required = false) String email,
                                 @RequestParam(required = false) UserType userType,
                                 @RequestParam(required = false) String role,
                                 @RequestParam(required = false) Boolean banned,
                                 @RequestParam(required = false) Boolean deleted,
                                 @RequestParam(required = false) String startDateRegistration,
                                 @RequestParam(required = false) String endDateRegistration,
                                 @RequestParam(required = false) String startDateLastLogin,
                                 @RequestParam(required = false) String endDateLastLogin,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "id") String sortField,
                                 @RequestParam(defaultValue = "asc") String sortDirection) {

        // Sprawdzanie poprawności dat
        // Sprawdzanie poprawności zakresów dat rejestracji
        if (startDateRegistration != null && endDateRegistration != null &&
                !startDateRegistration.isEmpty() && !endDateRegistration.isEmpty()) {
            LocalDateTime start = LocalDateTime.parse(startDateRegistration + "T00:00:00");
            LocalDateTime end = LocalDateTime.parse(endDateRegistration + "T23:59:59");
            if (end.isBefore(start)) {
                redirectAttributes.addFlashAttribute("error", "End registration date cannot be earlier than start date.");
                return "redirect:/admin/dashboard";
            }
        }

        // Sprawdzanie poprawności zakresów dat ostatniego logowania
        if (startDateLastLogin != null && endDateLastLogin != null &&
                !startDateLastLogin.isEmpty() && !endDateLastLogin.isEmpty()) {
            LocalDateTime start = LocalDateTime.parse(startDateLastLogin + "T00:00:00");
            LocalDateTime end = LocalDateTime.parse(endDateLastLogin + "T23:59:59");
            if (end.isBefore(start)) {
                redirectAttributes.addFlashAttribute("error", "End last login date cannot be earlier than start date.");
                return "redirect:/admin/dashboard";
            }
        }


        Sort sort = sortDirection.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, 10, sort);

        // Pobieranie użytkowników z uwzględnieniem filtrów
        Page<User> userPage = userService.findFilteredUsers(name, email, userType, role, banned, deleted, startDateRegistration, endDateRegistration, startDateLastLogin, endDateLastLogin, pageable);

        // Dodawanie danych do modelu
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");

        return "admin/adminDashboard";
    }

    @GetMapping("/users/viewbpms/{id}")
    public String viewUserBPMs(@PathVariable Long id,
                               Model model,
                               RedirectAttributes redirectAttributes,
                               @RequestParam(required = false) Integer min,
                               @RequestParam(required = false) Integer max,
                               @RequestParam(required = false) Integer bpmValue,
                               @RequestParam(required = false) String startDate,
                               @RequestParam(required = false) String endDate,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "id") String sortField,
                               @RequestParam(defaultValue = "asc") String sortDirection){
        User user = userService.findById(id);
        if (user == null) {
            return "redirect:/admin/dashboard";
        }

        // Walidacja BPM
        if (min != null && (min < 1 || min > 250)) {
            redirectAttributes.addFlashAttribute("error", "Min BPM must be between 1 and 250.");
            return "redirect:/admin/users/viewbpms/" + id;
        }
        if (max != null && (max < 1 || max > 250)) {
            redirectAttributes.addFlashAttribute("error", "Max BPM must be between 1 and 250.");
            return "redirect:/admin/users/viewbpms/" + id;
        }
        if (min != null && max != null && min >= max) {
            redirectAttributes.addFlashAttribute("error", "Min BPM must be less than Max BPM.");
            return "redirect:/admin/users/viewbpms/" + id;
        }
        if (bpmValue != null && (bpmValue < 1 || bpmValue > 250)) {
            redirectAttributes.addFlashAttribute("error", "BPM value must be between 1 and 250.");
            return "redirect:/admin/users/viewbpms/" + id;
        }

        if (startDate != null && endDate != null && !startDate.isEmpty() && !endDate.isEmpty()) {
            LocalDateTime start = LocalDateTime.parse(startDate + "T00:00:00");
            LocalDateTime end = LocalDateTime.parse(endDate + "T23:59:59");
            if (end.isBefore(start)) {
                redirectAttributes.addFlashAttribute("error", "End date cannot be earlier than start date.");
                return "redirect:/admin/users/viewbpms/" + id;
            }
        }
        Sort sort = sortDirection.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, 10, sort);

        Page<BPM> bpmPage = bpmService.findFilteredBPMs(user, min, max, bpmValue, startDate, endDate, pageable);

        // Jeśli brak wyników, dodaj komunikat do modelu
//        if (bpmPage.getTotalElements() == 0) {
//            model.addAttribute("error", "No BPMs found matching the given criteria.");
//        }

        model.addAttribute("user", user);
        model.addAttribute("bpms", bpmPage.getContent());
        model.addAttribute("totalPages", bpmPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "admin/adminUserBPMsView";
    }
    @GetMapping("/users/viewkeys/{id}")
    public String viewUserKeys(@PathVariable Long id,
                               Model model,
                               RedirectAttributes redirectAttributes,
                               @RequestParam(required = false) String keyName,
                               @RequestParam(required = false) String startDate,
                               @RequestParam(required = false) String endDate,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "id") String sortField,
                               @RequestParam(defaultValue = "asc") String sortDirection){

        User user = userService.findById(id);
        if (user == null) {
            return "redirect:/admin/dashboard";
        }

        if (startDate != null && endDate != null && !startDate.isEmpty() && !endDate.isEmpty()) {
            LocalDateTime start = LocalDateTime.parse(startDate + "T00:00:00");
            LocalDateTime end = LocalDateTime.parse(endDate + "T23:59:59");
            if (end.isBefore(start)) {
                redirectAttributes.addFlashAttribute("error", "End date cannot be earlier than start date.");
                return "redirect:/admin/users/viewkeys/" + id;
            }
        }

        Sort sort = sortDirection.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, 10, sort);

        KeyType keyType = null;
        if (keyName != null && !keyName.isEmpty()) {
            try {
                keyType = KeyType.valueOf(keyName); // Konwertowanie stringa na enum
            } catch (IllegalArgumentException e) {
                System.out.println("Niepoprawny typ klucza: " + keyName);
            }
        }

        // Przekazujemy keyType do metody findFilteredKeys
        Page<Key> keyPage = keyService.findFilteredKeys(user, keyType, startDate, endDate, pageable);

        List<Key> keys = keyService.findKeysByUser(user);

        model.addAttribute("user", user);
        model.addAttribute("keys", keyPage.getContent());
        model.addAttribute("totalPages", keyPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", sortDirection.equals("asc") ? "desc" : "asc");
        model.addAttribute("keyName", keyName);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "admin/adminUserKeysView";
    }

    @GetMapping("/users/advanced/{id}")
    public String advancedUser(@PathVariable Long id, Model model,
                               @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser,
                               RedirectAttributes redirectAttributes){
        User user = userService.findById(id);

        if(user == null){
            return "redirect:/admin/dashboard";
        }

        if(user.getEmail().equals(currentUser.getUsername())){
            redirectAttributes.addFlashAttribute("error", "You cannot access advanced settings for yourself.");
            return "redirect:/admin/dashboard";
        }

        model.addAttribute("user", user);
        return "admin/adminUserAdvanced";
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
        return "redirect:/admin/dashboard";
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
            return "redirect:/admin/users/viewbpms/" + userId;
        }

        model.addAttribute("bpm", bpm);
        model.addAttribute("userId", userId);
        return "admin/adminBPMEdit";
    }

    @PostMapping("/users/{userId}/bpms/edit/{bpmId}")
    public String updateUserBPM(@PathVariable Long userId,
                                @PathVariable Long bpmId,
                                @Valid @ModelAttribute("bpm") BPM updatedBPM,
                                BindingResult result,
                                Model model,
                                @RequestParam(value = "min", required = false) Integer min,
                                @RequestParam(value = "max", required = false) Integer max){
        BPM existingBPM = bpmService.findById(bpmId);

        if(existingBPM == null || !existingBPM.getCreatedBy().getId().equals(userId)){
            return "redirect:/admin/users/viewbpms/" + userId;
        }

        if(result.hasErrors()){
            return "admin/adminBPMEdit";
        }

        existingBPM.setBpmValue(bpmService.generateRandomBPMValue(min, max));
        bpmService.updateBPM(existingBPM);
        return "redirect:/admin/users/viewbpms/" + userId;
    }

    @GetMapping("/users/{userId}/bpms/delete/{bpmId}")
    public String deleteUserBPM(@PathVariable Long userId, @PathVariable Long bpmId){
        BPM bpm = bpmService.findById(bpmId);

        if(bpm != null && bpm.getCreatedBy().getId().equals(userId)){
            bpmService.deleteBPM(bpmId);
        }

        return "redirect:/admin/users/viewbpms/" + userId;
    }

    @GetMapping("/users/{userId}/keys/edit/{keyId}")
    public String editUserKey(@PathVariable Long userId,
                              @PathVariable Long keyId,
                              Model model){
        User user = userService.findById(userId);

        if(user == null){
            return "redirect:/admin/dashboard";
        }

        Key key = keyService.findById(keyId);
        if(key == null || !key.getCreatedBy().getId().equals(userId)){
            return "redirect:/admin/users/viewkeys/" + userId;
        }

        model.addAttribute("key", key);
        model.addAttribute("userId", userId);
        return "admin/adminKeyEdit";
    }

    @PostMapping("/users/{userId}/keys/edit/{keyId}")
    public String updateUserKey(@PathVariable Long userId,
                                @PathVariable Long keyId,
                                @RequestParam(value = "type", required = false) String type,
                                Model model){
        Key existingKey = keyService.findById(keyId);

        if(existingKey == null || !existingKey.getCreatedBy().getId().equals(userId)){
            return "redirect:/admin/users/view/" + userId;
        }

        KeyType newKeyType = keyService.getRandomKeyType(type);
        List<KeyType> newRelatedKeys = keyService.findRelatedKeys(newKeyType);

//        if(result.hasErrors()){
//            return "admin/adminKeyEdit";
//        }

        existingKey.setName(newKeyType);
        existingKey.getRelatedKeys().clear();
        existingKey.getRelatedKeys().addAll(newRelatedKeys);
        keyService.updateKey(existingKey);
        return "redirect:/admin/users/viewkeys/" + userId;
    }

    @GetMapping("/users/{userId}/keys/delete/{keyId}")
    public String deleteUserKey(@PathVariable Long userId, @PathVariable Long keyId){
        Key key = keyService.findById(keyId);

        if(key != null && key.getCreatedBy().getId().equals(userId)){
            keyService.deleteKey(key);
        }
        return "redirect:/admin/users/viewkeys/" + userId;
    }
}
