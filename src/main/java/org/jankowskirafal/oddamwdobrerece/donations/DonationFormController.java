package org.jankowskirafal.oddamwdobrerece.donations;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jankowskirafal.oddamwdobrerece.categories.CategoryService;
import org.jankowskirafal.oddamwdobrerece.contactform.ContactForm;
import org.jankowskirafal.oddamwdobrerece.dtos.AdminDonationFormDto;
import org.jankowskirafal.oddamwdobrerece.institutions.InstitutionServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;

@Controller
@RequestMapping("/donations")
@AllArgsConstructor
@Slf4j
public class DonationFormController {

    private final DonationService donationService;
    private final CategoryService categoryService;
    private final InstitutionServiceImpl institutionServiceImpl;


    @GetMapping("/add")
    public String displayDonationAddForm(Model model) {

        AdminDonationFormDto adminDonationFormDto = new AdminDonationFormDto(
                categoryService.getAllCategories(),
                institutionServiceImpl.getAll(),
                Arrays.stream(DonationStatus.values()).toList(),
                new Donation(),
                new ContactForm()
        );

        model.addAttribute("donationForm", adminDonationFormDto);

        return "/donation/add-donation-form";
    }

    @PostMapping("/form-confirmation")
    public String formConfirmation(@ModelAttribute("donationForm") AdminDonationFormDto adminDonationFormDto,
                                   Model model) {

        log.info("Received donation form data: {}", adminDonationFormDto);

        model.addAttribute("donationForm", adminDonationFormDto);
        donationService.saveDonation(adminDonationFormDto.donation());

        return "/donation/add-donation-confirmation";
    }


}
