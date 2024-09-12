package org.jankowskirafal.oddamwdobrerece.donations;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.jankowskirafal.oddamwdobrerece.categories.CategoryService;
import org.jankowskirafal.oddamwdobrerece.institutions.InstitutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

//        if (donationFilterDTO.getCategoryIds() != null && donationFilterDTO.getCategoryIds().contains(null)) {
//            donationFilterDTO.setCategoryIds(null);
//        }

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
        logger.info("listInstitutions");
        logger.info(search);
        logger.info(String.valueOf(donationPage.getTotalPages()));

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
