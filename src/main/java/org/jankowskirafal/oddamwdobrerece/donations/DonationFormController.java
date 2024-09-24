package org.jankowskirafal.oddamwdobrerece.donations;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jankowskirafal.oddamwdobrerece.categories.CategoryService;
import org.jankowskirafal.oddamwdobrerece.categories.CategoryServiceImpl;
import org.jankowskirafal.oddamwdobrerece.contactform.ContactForm;
import org.jankowskirafal.oddamwdobrerece.dtos.AdminDonationFormDto;
import org.jankowskirafal.oddamwdobrerece.institutions.InstitutionService;
import org.jankowskirafal.oddamwdobrerece.institutions.InstitutionServiceImpl;
import org.jankowskirafal.oddamwdobrerece.users.User;
import org.jankowskirafal.oddamwdobrerece.users.UserService;
import org.jankowskirafal.oddamwdobrerece.users.UserServiceImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.Optional;

@Controller
@RequestMapping("/donations")
@AllArgsConstructor
@Slf4j
public class DonationFormController {

    private final DonationService donationServiceImpl;
    private final CategoryService categoryServiceImpl;
    private final InstitutionService institutionServiceImpl;
    private final UserService userServiceImpl;


    @GetMapping("/add")
    public String displayDonationAddForm(Model model) {

        AdminDonationFormDto adminDonationFormDto = new AdminDonationFormDto(
                categoryServiceImpl.getAllCategories(),
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
                                   Model model,
                                   Authentication authentication) {

        log.info("Received donation form data: {}", adminDonationFormDto);

        // Check if the user is logged in
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {

            Optional<User> userOptional = userServiceImpl.findUserByEmail(userDetails.getUsername());
            userOptional.ifPresent(user -> adminDonationFormDto.donation().setUser(user));

        }

        model.addAttribute("donationForm", adminDonationFormDto);
        donationServiceImpl.saveDonation(adminDonationFormDto.donation());

        return "/donation/add-donation-confirmation";
    }


}
