package org.jankowskirafal.oddamwdobrerece.donations;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jankowskirafal.oddamwdobrerece.categories.CategoryService;
import org.jankowskirafal.oddamwdobrerece.contactform.ContactForm;
import org.jankowskirafal.oddamwdobrerece.dtos.AddDonationFormDto;
import org.jankowskirafal.oddamwdobrerece.institutions.InstitutionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/donations")
@AllArgsConstructor
@Slf4j
public class DonationFormController {

    private final DonationService donationService;
    private final CategoryService categoryService;
    private final InstitutionService institutionService;


    @GetMapping("/add")
    public String displayDonationAddForm(Model model) {

        AddDonationFormDto addDonationFormDto = new AddDonationFormDto(
                categoryService.getAllCategories(),
                institutionService.getAll(),
                new Donation(),
                new ContactForm()
        );

        model.addAttribute("donationForm", addDonationFormDto);

        return "form_default";
    }

    @PostMapping("/form-confirmation")
    public String formConfirmation(@ModelAttribute("donationForm") AddDonationFormDto addDonationFormDto,
                                   Model model) {

        log.info("Received donation form data: {}", addDonationFormDto);

        model.addAttribute("donationForm", addDonationFormDto);
        donationService.createDonation(addDonationFormDto.donation());

        return "form-confirmation-default";
    }


}
