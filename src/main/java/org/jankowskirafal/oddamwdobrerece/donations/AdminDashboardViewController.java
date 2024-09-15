package org.jankowskirafal.oddamwdobrerece.donations;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.jankowskirafal.oddamwdobrerece.categories.CategoryService;
import org.jankowskirafal.oddamwdobrerece.contactform.ContactForm;
import org.jankowskirafal.oddamwdobrerece.dtos.AdminDonationFormDto;
import org.jankowskirafal.oddamwdobrerece.institutions.InstitutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.Optional;

@Controller
@AllArgsConstructor
@RequestMapping("/admin/donations")
public class AdminDashboardViewController {

    private static final Logger logger = LoggerFactory.getLogger(AdminDashboardViewController.class);
    private final DonationService donationService;
    private final InstitutionService institutionService;
    private final CategoryService categoryService;

    @GetMapping
    public String displayDonationList(Model model,
                                      @ModelAttribute DonationFilterDTO donationFilterDTO,
                                      @RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "5") int size,
                                      @RequestParam(required = false, defaultValue = "") String search) {

        Page<Donation> donationPage = donationService.getDonations(search,
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
        logger.info("listInstitutions");
        logger.info(search);
        logger.info(String.valueOf(donationPage.getTotalPages()));

        return "admin_donations_list";
    }

    @GetMapping("/add")
    public String showDonationForm(Model model) {
        model.addAttribute("donation", new Donation());
        return "admin_donation_form";
    }

    @PostMapping("/save")
    public String saveDonation(Model model,
                               @ModelAttribute("donationForm") AdminDonationFormDto adminDonationFormDto,
                               BindingResult result,
                               RedirectAttributes redirectAttributes
                               ) {
        if (result.hasErrors()) {
            return "admin_donation_form";
        }

        Donation donation = adminDonationFormDto.donation();

        if (donation.getDonationId() == null) {
            donationService.saveDonation(donation);
        } else {
            donationService.updateDonation(donation);
        }

        redirectAttributes.addFlashAttribute("message", "Dar został pomyślnie zapisany.");
        return "redirect:/admin/donations";
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

        return "admin_donation_form";
    }

    @PostMapping("/delete/{id}")
    public String deleteInstitution(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        donationService.deleteDonation(id);
        redirectAttributes.addFlashAttribute("message", "Dar została usunięta.");
        return "redirect:/admin/donations";
    }
    
}
