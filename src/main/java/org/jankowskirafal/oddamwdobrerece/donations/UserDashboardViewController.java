package org.jankowskirafal.oddamwdobrerece.donations;


import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.security.core.context.SecurityContextHolder;
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
@Slf4j
public class UserDashboardViewController {

    private static final Logger logger = LoggerFactory.getLogger(UserDashboardViewController.class);
    private final DonationService donationService;
    private final InstitutionService institutionService;
    private final CategoryService categoryService;


    @GetMapping
    public String displayDonationList(Model model, @ModelAttribute DonationFilterDTO donationFilterDTO, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int size, @RequestParam(required = false, defaultValue = "") String search) {

        Page<Donation> donationPage = donationService.getDonations(search, donationFilterDTO, page - 1, size);

        model.addAttribute("donations", donationPage.getContent());
        model.addAttribute("institutions", institutionService.getAllInstitutionsForDropdown());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("filter", donationFilterDTO);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", donationPage.getTotalPages());
        model.addAttribute("search", search);
        log.info("Logged in user: {}", SecurityContextHolder.getContext().getAuthentication().getName());

        return "user_donations_list";
    }

    @PostMapping("/save")
    public String saveDonation(Model model, @ModelAttribute("donationForm") AdminDonationFormDto adminDonationFormDto, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "user_donation_form";
        }

        Donation donation = adminDonationFormDto.donation();

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

        if (donation.isPresent()) {
            AdminDonationFormDto adminDonationFormDto = new AdminDonationFormDto(
                    categoryService.getAllCategories(),
                    institutionService.getAll(),
                    Arrays.stream(DonationStatus.values()).toList(),
                    donation.get(),
                    new ContactForm()
            );

            model.addAttribute("donationForm", adminDonationFormDto);

            logger.info(String.valueOf(donation.get().pickUpDate));
            logger.info(String.valueOf(donation.get().status));
            logger.info(String.valueOf(donation.get().pickUpTime));
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Donation not found.");
        }

        return "user_donation_form";
    }

    @PostMapping("/{donationId}/updateStatus")
    public String updateDonationStatus(@PathVariable Long donationId) {

        Optional<Donation> donation = donationService.getDonationById(donationId);
        // ... (Authorization check: Make sure the logged-in user owns the donation) ...

        if (donation.isPresent()) {
            if (donation.get().getStatus() == DonationStatus.NEW) {
                donation.get().setStatus(DonationStatus.READY_FOR_PICKUP);
            } else if (donation.get().getStatus() == DonationStatus.READY_FOR_PICKUP) {
                donation.get().setStatus(DonationStatus.PICKED_UP);
                // ... (Record date and time of pick-up) ...
            }
            donationService.saveDonation(donation.get());
        }

        return "redirect:/user/donations"; // Redirect to the user's donations page
    }

}
