package com.ejournal.controller;

import com.ejournal.controller.main.Main;
import com.ejournal.model.enums.Role;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/students")
public class StudentCont extends Main {
    @GetMapping
    public String students(Model model) {
        getCurrentUserAndRole(model);
        model.addAttribute("students", userRepo.findAllByRole(Role.USER));
        return "students";
    }

    @GetMapping("/search")
    public String student(Model model, @RequestParam String fio) {
        getCurrentUserAndRole(model);
        model.addAttribute("fio", fio);
        model.addAttribute("students", userRepo.findAllByRoleAndFioContaining(Role.USER, fio));
        return "students";
    }

    @GetMapping("/{studentId}")
    public String student(Model model, @PathVariable Long studentId) {
        getCurrentUserAndRole(model);
        model.addAttribute("student", userRepo.getReferenceById(studentId));
        return "student";
    }
}