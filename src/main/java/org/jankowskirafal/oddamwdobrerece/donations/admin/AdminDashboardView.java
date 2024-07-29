package org.jankowskirafal.oddamwdobrerece.donations.admin;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.jankowskirafal.oddamwdobrerece.donations.Donation;
import org.jankowskirafal.oddamwdobrerece.donations.DonationService;
import org.jankowskirafal.oddamwdobrerece.institutions.Institution;
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
@AllArgsConstructor
@RequestMapping("/admin/donations")
public class AdminDashboardView {

    private final DonationService donationService;

    @GetMapping
    public String listInstitutions(Model model,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   @RequestParam(required = false) String search) {
        Page<Donation> donationPage = donationService.getAllDonations(page, size);
        model.addAttribute("donations", donationPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", donationPage.getTotalPages());
        model.addAttribute("search", search);
        return "donations-list";
    }

    @GetMapping("/add")
    public String showDonationForm(Model model) {
        model.addAttribute("donation", new Donation());
        return "donation-form";
    }

    @PostMapping("/save")
    public String saveDonation(@Valid @ModelAttribute Donation donation,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "donation-form";
        }

        if (donation.getDonationId() == null) {
            donationService.saveDonation(donation);
        } else {
            donationService.updateDonation(donation);
        }

        donationService.saveDonation(donation);
        redirectAttributes.addFlashAttribute("message", "Dar został pomyślnie zapisany.");
        return "redirect:/admin/donations";
    }

    @GetMapping("/edit/{id}")
    public String showEditDonationForm(@PathVariable Long id, Model model) {

        Optional<Donation> donation = donationService.getDonationById(id);

        if (donation.isPresent()) {
            model.addAttribute("donation", donation);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Donation not found.");
        }

        return "donation-form";
    }

    @PostMapping("/delete/{id}")
    public String deleteInstitution(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        donationService.deleteInstitution(id);
        redirectAttributes.addFlashAttribute("message", "Dar została usunięta.");
        return "redirect:/admin/donations";


    }
}
