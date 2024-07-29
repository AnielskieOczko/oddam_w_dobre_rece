package org.jankowskirafal.oddamwdobrerece.institutions.admin;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.jankowskirafal.oddamwdobrerece.institutions.Institution;
import org.jankowskirafal.oddamwdobrerece.institutions.InstitutionService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin/institutions")
@AllArgsConstructor
public class InstitutionAdminViewController {

    private final InstitutionService institutionService;

    @GetMapping
    public String listInstitutions(Model model,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   @RequestParam(required = false) String search) {
        Page<Institution> institutionPage = institutionService.getAllInstitutions(page, size, search);
        model.addAttribute("institutions", institutionPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", institutionPage.getTotalPages());
        model.addAttribute("search", search);
        return "institutions-list";
    }


    @GetMapping("/add")
    public String showInstitutionForm(Model model) {
        model.addAttribute("institution", new Institution());
        return "institution-form";
    }

    @PostMapping("/save")
    public String saveInstitution(@Valid @ModelAttribute Institution institution,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "institution-form";
        }

        if (institution.getInstitutionId() == null) {
            institutionService.saveInstitution(institution);
        } else {
            institutionService.updateInstitution(institution);
        }

        institutionService.saveInstitution(institution);
        redirectAttributes.addFlashAttribute("message", "Fundacja została pomyślnie zapisana.");
        return "redirect:/admin/institutions";
    }

    @GetMapping("/edit/{id}")
    public String showEditInstitutionForm(@PathVariable Long id, Model model) {

        Optional<Institution> institution = institutionService.getInstitutionById(id);

        if (institution.isPresent()) {
            model.addAttribute("institution", institution);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Institution not found.");
        }

        return "institution-form";
    }

    @PostMapping("/delete/{id}")
    public String deleteInstitution(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        institutionService.deleteInstitution(id);
        redirectAttributes.addFlashAttribute("message", "Fundacja została usunięta.");
        return "redirect:/admin/institutions";
    }
}