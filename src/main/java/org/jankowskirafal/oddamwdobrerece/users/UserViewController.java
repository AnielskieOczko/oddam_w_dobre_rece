package org.jankowskirafal.oddamwdobrerece.users;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.jankowskirafal.oddamwdobrerece.donations.AdminDashboardViewController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
@AllArgsConstructor
public class UserViewController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserViewController.class);

    @GetMapping("/account")
    public String userAccount(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userService.getByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("user", user);
        return "/user/user-account";
    }


    @GetMapping("/edit")
    public String editUserForm(Model model) {
        User user = getCurrentUser();
        RegistrationDto registrationDto = new RegistrationDto(user.getEmail(), "", "");
        model.addAttribute("registrationDto", registrationDto);
        return "/user/user-edit-form";
    }

    @PostMapping("/edit")
    public String editUser(@Valid RegistrationDto registrationDto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "/user/user-edit-form";
        }

        User user = getCurrentUser();

        if (!registrationDto.email().equals(user.getEmail()) && userService.getByEmail(registrationDto.email()).isPresent()) {
            bindingResult.rejectValue("email", "error.registrationDto", "Email is already in use");
            return "/user/user-edit-form";
        }

        if (!registrationDto.password().isEmpty()) {
            if (!registrationDto.password().equals(registrationDto.confirmPassword())) {
                bindingResult.rejectValue("confirmPassword", "error.registrationDto", "Passwords do not match");
                return "/user/user-edit-form";
            }
            user.setPassword(registrationDto.password());
        }

        user.setEmail(registrationDto.email());
        userService.updateUser(user);

        redirectAttributes.addFlashAttribute("message", "Your account has been updated successfully");



        // Re-authenticate with the new user details
        UserDetails userDetails = .loadUserByUsername(user.getEmail());
        Authentication newAuth = new UsernamePasswordAuthenticationToken(userDetails, user.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        logger.info(email);



        return "redirect:/user/account";
    }


    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userService.getByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

}