package com.makersacademy.acebook.controller;

import com.makersacademy.acebook.model.Friendship;
import com.makersacademy.acebook.model.User;
import com.makersacademy.acebook.repository.FriendshipRepository;
import com.makersacademy.acebook.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/friends")
public class FriendshipController {

    @Autowired
    FriendshipRepository friendshipRepository;

    @Autowired
    UserRepository userRepository;

    private String getUsernameFromPrincipal(Principal principal) {
        if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) principal;
            return token.getPrincipal().getAttribute("email");
        }
        return principal.getName();
    }

    @GetMapping
    public String friendsPage(Model model, Principal principal) {
        User currentUser = userRepository.findByEmail(getUsernameFromPrincipal(principal));

        List<Friendship> acceptedFriendships = friendshipRepository
                .findByIdRequesterIdOrIdAddresseeIdAndStatus(currentUser.getId(), currentUser.getId(), "ACCEPTED");

        List<User> friends = acceptedFriendships.stream()
                .map(f -> {
                    Long friendId = f.getId().getRequesterId().equals(currentUser.getId())
                            ? f.getId().getAddresseeId()
                            : f.getId().getRequesterId();
                    return userRepository.findById(friendId).orElse(null);
                })
                .filter(u -> u != null)
                .collect(Collectors.toList());

        List<Friendship> incomingRequests = friendshipRepository
                .findByIdAddresseeIdAndStatus(currentUser.getId(), "PENDING");

        List<Friendship> outgoingRequests = friendshipRepository
                .findByIdRequesterIdAndStatus(currentUser.getId(), "PENDING");

        List<Friendship> blockedFriendships = friendshipRepository
                .findByIdRequesterIdAndStatus(currentUser.getId(), "BLOCKED");

        model.addAttribute("friends", friends);
        model.addAttribute("incomingRequests", incomingRequests);
        model.addAttribute("outgoingRequests", outgoingRequests);
        model.addAttribute("blockedFriendships", blockedFriendships);

        return "friendships/index";
    }

    @PostMapping("/request/{username}")
    public RedirectView sendRequest(@PathVariable String username, Principal principal) {
        User requester = userRepository.findByEmail(getUsernameFromPrincipal(principal));
        User addressee = userRepository.findByUsername(username);

        Friendship friendship = new Friendship(requester.getId(), addressee.getId());
        friendshipRepository.save(friendship);

        return new RedirectView("/profile/" + username);
    }

    @PostMapping("/accept/{username}")
    public RedirectView acceptRequest(@PathVariable String username, Principal principal) {
        User addressee = userRepository.findByEmail(getUsernameFromPrincipal(principal));
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
        User addressee = userRepository.findByEmail(getUsernameFromPrincipal(principal));
        User requester = userRepository.findByUsername(username);

        Optional<Friendship> friendship = friendshipRepository
                .findByIdRequesterIdAndIdAddresseeId(requester.getId(), addressee.getId());

        friendship.ifPresent(f -> friendshipRepository.delete(f));

        return new RedirectView("/friendships");
    }

    @PostMapping("/block/{username}")
    public RedirectView blockUser(@PathVariable String username, Principal principal) {
        User requester = userRepository.findByEmail(getUsernameFromPrincipal(principal));
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
        User user = userRepository.findByEmail(getUsernameFromPrincipal(principal));
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