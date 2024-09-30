package org.jankowskirafal.oddamwdobrerece.users;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminUsersViewController {

    private final AuthorityService authorityServiceImpl;
    private final UserService userServiceImpl;
    private static final String ROLE_USER = "ROLE_USER";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ALL_AUTHORITIES = "allAuthorities";
    private static final String ADMIN_ADD_FORM = "/admin/admin_add_form";

    @GetMapping("/users")
    public String listUsers(Model model,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            @RequestParam(required = false) String search) {

        Page<User> usersPage = userServiceImpl.getAllUsersByRoleName(page, size, ROLE_USER);
        model.addAttribute("users", usersPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("search", search);
        return "/admin/users-list";
    }

    @GetMapping({"/users/add"})
    public String displayAddUserForm(Model model) {
        User user = new User();
        List<Authority> allAuthorities = authorityServiceImpl.getAllAuthorities();
        allAuthorities = allAuthorities.stream()
                .filter(authority -> authority.getName().equals(ROLE_USER))
                .toList();
        model.addAttribute("user", user);
        model.addAttribute(ALL_AUTHORITIES, allAuthorities);
        return "/admin/admin_add_user_form";
    }

    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, Model model) {
        return handleSaveUser(user, bindingResult, model, "/admin/users");
    }

    @PostMapping({"users/delete/{id}"})
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userServiceImpl.deleteUserById(id);
        redirectAttributes.addFlashAttribute("message", "Użytkownik został usunięty.");
        return "redirect:/admin/users";
    }

    @GetMapping({"/users/edit/{id}"})
    public String displayEditUserForm(@PathVariable Long id, Model model) {

        model.addAttribute(ALL_AUTHORITIES, authorityServiceImpl.getAllAuthorities());
        Optional<User> user = userServiceImpl.getUserById(id);

        if (user.isPresent()) {
            model.addAttribute("user", user.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }

        return "/admin/admin_add_user_form";
    }

    @GetMapping("/admins")
    public String adminUsers(Model model,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             @RequestParam(required = false) String search) {

        Page<User> usersPage = userServiceImpl.getAllUsersByRoleName(page, size, ROLE_ADMIN);
        model.addAttribute("admins", usersPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("search", search);
        return "/admin/admins-list";
    }

    @GetMapping({"/admins/add"})
    public String displayAddAdminForm(Model model) {
        User user = new User();
        List<Authority> allAuthorities = authorityServiceImpl.getAllAuthorities();
        allAuthorities = allAuthorities.stream()
                .filter(authority -> authority.getName().equals(ROLE_ADMIN))
                .toList();
        model.addAttribute("user", user);
        model.addAttribute(ALL_AUTHORITIES, allAuthorities);
        return ADMIN_ADD_FORM;
    }

    @PostMapping("/admins/save")
    public String saveAdmin(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, Model model) {
        return handleSaveUser(user, bindingResult, model, "/admin/admins");
    }

    @GetMapping({"/admins/edit/{id}"})
    public String displayEditAdminForm(@PathVariable Long id, Model model) {

        model.addAttribute(ALL_AUTHORITIES, authorityServiceImpl.getAllAuthorities());

        Optional<User> user = userServiceImpl.getUserById(id);

        if (user.isPresent()) {
            model.addAttribute("user", user.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }

        return ADMIN_ADD_FORM;
    }

    @PostMapping({"admins/delete/{id}"})
    public String deleteAdmin(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userServiceImpl.deleteUserById(id);
        redirectAttributes.addFlashAttribute("message", "Użytkownik został usunięty.");
        return "redirect:/admin/admins";
    }


    private String handleSaveUser(User user, BindingResult bindingResult, Model model, String redirectUrl) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(ALL_AUTHORITIES, authorityServiceImpl.getAllAuthorities());
            return ADMIN_ADD_FORM;
        }

        Set<String> roleNames = user.getAuthorities().stream()
                .map(Authority::getName)
                .collect(Collectors.toSet());

        if (user.getId() == null) {
            userServiceImpl.createUser(user, roleNames);
        } else {
            userServiceImpl.updateUser(user, roleNames);
        }

        return "redirect:" + redirectUrl;
    }

//    private String handleSaveUser(User user, BindingResult bindingResult, Model model, String redirectUrl) {
//        if (bindingResult.hasErrors()) {
//            model.addAttribute(ALL_AUTHORITIES, authorityServiceImpl.getAllAuthorities());
//            return ADMIN_ADD_FORM;
//        }
//
//        // 1. Get authority names from the form (might be a comma-separated string or an array)
//        String[] authorityNames = user.getAuthorities() != null
//                ? user.getAuthorities().stream().map(Authority::getName).toArray(String[]::new)
//                : new String[0];
//
//        // 2. Convert authority names to a Set<Authority>
//        Set<Authority> authorities = Arrays.stream(authorityNames)
//                .map(authorityServiceImpl::findAuthorityByRoleName)
//                .filter(Optional::isPresent)
//                .map(Optional::get)
//                .collect(Collectors.toSet());
//
//        user.setAuthorities(authorities); // Set the authorities on the user object
//
//        if (user.getId() == null) {
//            userServiceImpl.createUser(user, authorities.stream().map(Authority::getName).collect(Collectors.toSet()));
//        } else {
//            userServiceImpl.updateUser(user, authorities.stream().map(Authority::getName).collect(Collectors.toSet()));
//        }
//
//        return "redirect:" + redirectUrl;
//    }
}
