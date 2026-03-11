package com.makersacademy.acebook.controller;

import com.makersacademy.acebook.model.User;
import com.makersacademy.acebook.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class UsersController {
    @Autowired
    UserRepository userRepository;

    @GetMapping("/users/after-login")
    public RedirectView afterLogin() {
        DefaultOidcUser principal = (DefaultOidcUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        String email = (String) principal.getAttributes().get("email");
        User user = userRepository
                .findUserByEmail(email)
                .orElseGet(() -> userRepository.save(new User(email)));
        
        if (user.getFirstName() == null || user.getFirstName().isEmpty()) {
            return new RedirectView("/users/setup");
        }

        return new RedirectView("/");
    }

    @GetMapping("/users/setup")
    public ModelAndView setup() {
        ModelAndView setupPage = new ModelAndView("users/setup");
        return setupPage;
    }

    @PostMapping("/users/setup")
    public RedirectView saveProfile(@RequestParam String firstName, @RequestParam String lastName, @RequestParam String username) {
        DefaultOidcUser principal = (DefaultOidcUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        String email = (String) principal.getAttributes().get("email");
        User user = userRepository.findByEmail(email);

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        userRepository.save(user);
        return new RedirectView("/");
    }
}
