package com.ejournal.controller;

import com.ejournal.controller.main.Main;
import com.ejournal.model.AppUser;
import com.ejournal.model.UserGroup;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileCont extends Main {
    @GetMapping
    public String profile(Model model) {
        getCurrentUserAndRole(model);
        return "profile";
    }

    @PostMapping("/fio")
    public String fioProfile(@RequestParam String fio) {
        AppUser user = getUser();
        user.setFio(fio);
        userRepo.save(user);
        return "redirect:/profile";
    }

    @PostMapping("/tel")
    public String telProfile(@RequestParam String tel) {
        AppUser user = getUser();
        user.setTel(tel);
        userRepo.save(user);
        return "redirect:/profile";
    }

    @PostMapping("/email")
    public String emailProfile(@RequestParam String email) {
        AppUser user = getUser();
        user.setEmail(email);
        userRepo.save(user);
        return "redirect:/profile";
    }
}
