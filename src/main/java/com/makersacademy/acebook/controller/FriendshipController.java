package com.makersacademy.acebook.controller;

import com.makersacademy.acebook.model.Friendship;
import com.makersacademy.acebook.model.User;
import com.makersacademy.acebook.repository.FriendshipRepository;
import com.makersacademy.acebook.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.util.Optional;

// I have built this from the perspective that we will be using usernames in the routes

@Controller
@RequestMapping("/friendships")
public class FriendshipController {

    @Autowired
    FriendshipRepository friendshipRepository;

    @Autowired
    UserRepository userRepository;

    @PostMapping("/request/{username}")
    public RedirectView sendRequest(@PathVariable String username, Principal principal) {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) principal;
        String email = token.getPrincipal().getAttribute("email");
        User requester = userRepository.findByUsername(email);
        User addressee = userRepository.findByUsername(username);

        Friendship friendship = new Friendship(requester.getId(), addressee.getId());
        friendshipRepository.save(friendship);

        return new RedirectView("/profile/" + username);
    }

    @PostMapping("/accept/{username}")
    public RedirectView acceptRequest(@PathVariable String username, Principal principal) {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) principal;
        String email = token.getPrincipal().getAttribute("email");
        User addressee = userRepository.findByUsername(email);
        User requester = userRepository.findByUsername(username);

        Optional<Friendship> friendship = friendshipRepository
                .findByIdRequesterIdAndIdAddresseeId(requester.getId(), addressee.getId());

        friendship.ifPresent(f -> {
            f.setStatus("ACCEPTED");
            friendshipRepository.save(f);
        });

        return new RedirectView("/friendships");
    }

    @PostMapping("/decline/{username}")
    public RedirectView declineRequest(@PathVariable String username, Principal principal) {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) principal;
        String email = token.getPrincipal().getAttribute("email");
        User addressee = userRepository.findByUsername(email);
        User requester = userRepository.findByUsername(username);

        Optional<Friendship> friendship = friendshipRepository
                .findByIdRequesterIdAndIdAddresseeId(requester.getId(), addressee.getId());

        friendship.ifPresent(f -> friendshipRepository.delete(f));

        return new RedirectView("/friendships");
    }

    @PostMapping("/block/{username}")
    public RedirectView blockUser(@PathVariable String username, Principal principal) {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) principal;
        String email = token.getPrincipal().getAttribute("email");
        User requester = userRepository.findByUsername(email);
        User addressee = userRepository.findByUsername(username);

        Optional<Friendship> existing = friendshipRepository
                .findByIdRequesterIdAndIdAddresseeId(requester.getId(), addressee.getId());

        if (existing.isPresent()) {
            existing.get().setStatus("BLOCKED");
            friendshipRepository.save(existing.get());
        } else {
            Friendship friendship = new Friendship(requester.getId(), addressee.getId());
            friendship.setStatus("BLOCKED");
            friendshipRepository.save(friendship);
        }

        return new RedirectView("/profile/" + username);
    }

    @PostMapping("/unfriend/{username}")
    public RedirectView unfriend(@PathVariable String username, Principal principal) {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) principal;
        String email = token.getPrincipal().getAttribute("email");
        User user = userRepository.findByUsername(email);
        User other = userRepository.findByUsername(username);

        Optional<Friendship> friendship = friendshipRepository
                .findByIdRequesterIdAndIdAddresseeId(user.getId(), other.getId());

        if (friendship.isEmpty()) {
            friendship = friendshipRepository
                    .findByIdRequesterIdAndIdAddresseeId(other.getId(), user.getId());
        }

        friendship.ifPresent(f -> friendshipRepository.delete(f));

        return new RedirectView("/profile/" + username);
    }
}