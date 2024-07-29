package org.jankowskirafal.oddamwdobrerece.users.admin;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.jankowskirafal.oddamwdobrerece.institutions.Institution;
import org.jankowskirafal.oddamwdobrerece.institutions.InstitutionService;
import org.jankowskirafal.oddamwdobrerece.users.*;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@AllArgsConstructor
@RequestMapping("/admin/users")
public class AdminUsersViewController {

    private final AuthorityService authorityService;
    private final UserService userService;
    private final String ROLE_USER = "ROLE_USER";

    @GetMapping
    public String listUsers(Model model,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            @RequestParam(required = false) String search) {

        Page<User> usersPage = userService.getAllUsersByRoleName(page, size, ROLE_USER);
        model.addAttribute("users", usersPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("search", search);
        return "users-list";
    }

    @GetMapping("/add")
    public String displayAddUserForm(Model model) {
        User user = new User();
        List<Authority> allAuthorities = authorityService.getAllAuthorities();


        model.addAttribute("user", user);
        model.addAttribute("allAuthorieties", allAuthorities);
        return "admin-user-form";
    }

    @GetMapping("/edit/{id}")
    public String showEditInstitutionForm(@PathVariable Long id, Model model) {

        List<Authority> allAuthorities = authorityService.getAllAuthorities();
        model.addAttribute("allAuthorieties", allAuthorities);

        Optional<User> user = userService.getUserById(id);

        if (user.isPresent()) {
            model.addAttribute("user", user);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }

        return "admin-user-form";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute("user") @Valid User user,
                           BindingResult bindingResult,
                           Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("allAuthorities", authorityService.getAllAuthorities());
            return "admin-user-form";
        }

        Set<Authority> authorities = new HashSet<>();
        for (Authority authority : user.getAuthorities()) {
            authorities.add(authorityService.findAuthorityByRoleName(authority.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid authority name: " + authority.getName())));
        }
        user.setAuthorities(authorities);

        userService.saveUser(user);

        return "redirect:/admin/users";
    }


    @PostMapping("/delete/{id}")
    public String deleteInstitution(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteUserById(id);
        redirectAttributes.addFlashAttribute("message", "Użytkownik został usunięty.");
        return "redirect:/admin/users";
    }
}
