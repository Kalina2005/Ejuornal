package com.ejournal.controller;

import com.ejournal.controller.main.Main;
import com.ejournal.model.AppUser;
import com.ejournal.model.UserGroup;
import com.ejournal.model.enums.Role;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/groups")
public class GroupCont extends Main {
    @GetMapping
    public String groups(Model model) {
        getCurrentUserAndRole(model);
        model.addAttribute("groups", groupRepo.findAll());
        return "groups";
    }

    @PostMapping("/add")
    public String addGroup(@RequestParam String name) {
        groupRepo.save(new UserGroup(name));
        return "redirect:/groups";
    }

    @PostMapping("/{groupId}/edit")
    public String editGroup(@PathVariable Long groupId, @RequestParam String name) {
        UserGroup group = groupRepo.getReferenceById(groupId);
        group.setName(name);
        groupRepo.save(group);
        return "redirect:/groups";
    }

    @GetMapping("/{groupId}/delete")
    public String deleteGroup(@PathVariable Long groupId) {
        UserGroup group = groupRepo.getReferenceById(groupId);
        List<AppUser> users = group.getUsers();
        for (AppUser i : users) {
            i.setGroup(null);
        }
        groupRepo.deleteById(groupId);
        return "redirect:/groups";
    }

    @GetMapping("/{groupId}")
    public String group(Model model, @PathVariable Long groupId) {
        getCurrentUserAndRole(model);
        model.addAttribute("group", groupRepo.getReferenceById(groupId));
        model.addAttribute("users", userRepo.findAllByRoleAndGroupNull(Role.USER));
        return "group";
    }

    @PostMapping("/{groupId}/users/add")
    public String addUserInGroup(@PathVariable Long groupId, @RequestParam Long userId) {
        AppUser user = userRepo.getReferenceById(userId);
        user.setGroup(groupRepo.getReferenceById(groupId));
        userRepo.save(user);
        return "redirect:/groups/{groupId}";
    }

    @GetMapping("/{groupId}/users/{userId}/remove")
    public String removeUserFromGroup(@PathVariable Long groupId, @PathVariable Long userId) {
        AppUser user = userRepo.getReferenceById(userId);
        user.setGroup(null);
        userRepo.save(user);
        return "redirect:/groups/{groupId}";
    }
}