package com.makersacademy.acebook.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.makersacademy.acebook.model.User;
import com.makersacademy.acebook.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.Map;

@RestController
public class UsersController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    private Cloudinary cloudinary;

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
    public RedirectView saveProfile(@RequestParam("profile-picture") MultipartFile profilePicture,
                                    @RequestParam String firstName,
                                    @RequestParam String lastName,
                                    @RequestParam String username) throws IOException {
        DefaultOidcUser principal = (DefaultOidcUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        String email = (String) principal.getAttributes().get("email");
        User user = userRepository.findByEmail(email);

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);

        if (!profilePicture.isEmpty()) {
            Map uploadResult = cloudinary.uploader().upload(profilePicture.getBytes(), ObjectUtils.emptyMap());
            String publicUrl = (String) uploadResult.get("secure_url");
            user.setProfilePictureUrl(publicUrl);
        }

        userRepository.save(user);
        return new RedirectView("/");
    }
}
