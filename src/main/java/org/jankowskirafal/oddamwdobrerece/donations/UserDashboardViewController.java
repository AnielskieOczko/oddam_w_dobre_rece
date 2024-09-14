package org.jankowskirafal.oddamwdobrerece.donations;


import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.jankowskirafal.oddamwdobrerece.categories.CategoryService;
import org.jankowskirafal.oddamwdobrerece.contactform.ContactForm;
import org.jankowskirafal.oddamwdobrerece.dtos.AdminDonationFormDto;
import org.jankowskirafal.oddamwdobrerece.dtos.UserDonationFormDto;
import org.jankowskirafal.oddamwdobrerece.institutions.InstitutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@AllArgsConstructor
@RequestMapping("/user/donations")
public class UserDashboardViewController {

    private static final Logger logger = LoggerFactory.getLogger(UserDashboardViewController.class);
    private final DonationService donationService;
    private final InstitutionService institutionService;
    private final CategoryService categoryService;


    @GetMapping
    public String displayDonationList(Model model,
                                      @ModelAttribute DonationFilterDTO donationFilterDTO,
                                      @RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "5") int size,
                                      @RequestParam(required = false, defaultValue = "") String search) {

        Page<Donation> donationPage = donationService.getAllDonationsWithSearchAndFilters(search,
                donationFilterDTO,
                page - 1,
                size);

        model.addAttribute("donations", donationPage.getContent());
        model.addAttribute("institutions", institutionService.getAllInstitutionsForDropdown());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("filter", donationFilterDTO);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", donationPage.getTotalPages());
        model.addAttribute("search", search);

        return "user_donations_list";
    }

    @PostMapping("/save")
    public String saveDonation(@Valid @ModelAttribute Donation donation,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "user_donation_form";
        }

        if (donation.getDonationId() == null) {
            donationService.saveDonation(donation);
        } else {
            donationService.updateDonation(donation);
        }

        redirectAttributes.addFlashAttribute("message", "Dar został pomyślnie zapisany.");
        return "redirect:/user/donations";
    }

    @GetMapping("/edit/{id}")
    public String showEditDonationForm(@PathVariable Long id, Model model) {
        Optional<Donation> donation = donationService.getDonationById(id);

        List<DonationStatus> donationStatuses = Arrays.stream(DonationStatus.values()).toList();

        UserDonationFormDto userDonationFormDto = new UserDonationFormDto(
                categoryService.getAllCategories(),
                institutionService.getAll(),
                donationStatuses,
                new Donation(),
                new ContactForm()
        );

        model.addAttribute("donationForm", userDonationFormDto);

        if (donation.isPresent()) {
            model.addAttribute("donation", donation.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Donation not found.");
        }

        return "user_donation_form";
    }

}
