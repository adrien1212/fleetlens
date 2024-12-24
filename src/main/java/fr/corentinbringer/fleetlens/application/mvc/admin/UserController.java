package fr.corentinbringer.fleetlens.application.mvc.admin;

import fr.corentinbringer.fleetlens.application.dto.user.CreateUserRequest;
import fr.corentinbringer.fleetlens.application.dto.user.UserFilterRequest;
import fr.corentinbringer.fleetlens.application.dto.user.UserListView;
import fr.corentinbringer.fleetlens.domain.model.UserRole;
import fr.corentinbringer.fleetlens.domain.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public String getAllUsers(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "12") int size,
                              @Valid UserFilterRequest filterRequest,
                              Model model) {
        Page<UserListView> userPage = userService.findAll(page, size, filterRequest);

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("nextPage", page + 1 < userPage.getTotalPages() ? page + 1 : page);
        model.addAttribute("prevPage", page - 1 >= 0 ? page - 1 : page);
        model.addAttribute("searchTerm", filterRequest.getSearchTerm());

        return "admin/users/list";
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable UUID id) {
        userService.delete(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new CreateUserRequest());
        model.addAttribute("roles", UserRole.values());
        return "admin/users/create";
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute("user") @Valid CreateUserRequest userRequest, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", UserRole.values());
            return "admin/users/create";
        }

        userService.createUser(userRequest);
        return "redirect:/admin/users";
    }
}