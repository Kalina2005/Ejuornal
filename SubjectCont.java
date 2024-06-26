package com.ejournal.controller;

import com.ejournal.controller.main.Main;
import com.ejournal.model.Absence;
import com.ejournal.model.Subject;
import com.ejournal.model.enums.Reason;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/subjects")
public class SubjectCont extends Main {
    @GetMapping
    public String subjects(Model model) {
        getCurrentUserAndRole(model);
        model.addAttribute("groups", groupRepo.findAll());
        return "subjects";
    }

    @PostMapping("/add")
    public String addSubject(@RequestParam String name, @RequestParam Long groupId) {
        subjectRepo.save(new Subject(name, groupRepo.getReferenceById(groupId), getUser()));
        return "redirect:/subjects";
    }

    @GetMapping("/{subjectId}")
    public String subject(Model model, @PathVariable Long subjectId) {
        getCurrentUserAndRole(model);
        model.addAttribute("subject", subjectRepo.getReferenceById(subjectId));
        model.addAttribute("reasons", Reason.values());
        return "subject";
    }

    @PostMapping("/{subjectId}/edit")
    public String editSubject(@RequestParam String name,@PathVariable Long subjectId) {
        Subject subject = subjectRepo.getReferenceById(subjectId);
        subject.setName(name);
        subjectRepo.save(subject);
        return "redirect:/subjects/{subjectId}";
    }

    @PostMapping("/{subjectId}/absences/add")
    public String addAbsence(@PathVariable Long subjectId, @RequestParam Long userId, @RequestParam String date, @RequestParam int count, @RequestParam Reason reason) {
        absenceRepo.save(new Absence(date, count, reason, subjectRepo.getReferenceById(subjectId), userRepo.getReferenceById(userId)));
        return "redirect:/subjects/{subjectId}";
    }

    @PostMapping("/{subjectId}/absences/{absenceId}/edit")
    public String editAbsence(@PathVariable Long subjectId,  @RequestParam String date, @RequestParam int count, @RequestParam Reason reason, @PathVariable Long absenceId) {
        Absence absence = absenceRepo.getReferenceById(absenceId);
        absence.set(date, count, reason);
        absenceRepo.save(absence);
        return "redirect:/subjects/{subjectId}";
    }

    @GetMapping("/{subjectId}/absences/{absenceId}/delete")
    public String deleteAbsence(@PathVariable Long subjectId, @PathVariable Long absenceId) {
        absenceRepo.deleteById(absenceId);
        return "redirect:/subjects/{subjectId}";
    }
}
