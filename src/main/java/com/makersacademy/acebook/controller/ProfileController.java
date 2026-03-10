package com.makersacademy.acebook.controller;

import com.makersacademy.acebook.model.User;
import com.makersacademy.acebook.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class ProfileController {
    @Autowired
    UserRepository userRepository;

    // tell Spring Boot this method handles the "GET '/'" request
    @GetMapping("/profile/{username}")
    public ModelAndView getProfile(@PathVariable String username) {
        Optional<User> currentUser = userRepository.findUserByUsername(username);

        ModelAndView modelAndView = new ModelAndView("profiles/profile_page");
        return modelAndView.addObject("user", currentUser.get());
    }
}