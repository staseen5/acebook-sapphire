package com.makersacademy.acebook.controller;

import com.makersacademy.acebook.model.Friendship;
import com.makersacademy.acebook.model.Post;
import com.makersacademy.acebook.model.User;
import com.makersacademy.acebook.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class ProfileController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    PostLikeRepository postLikeRepository;
    @Autowired
    FriendshipRepository friendshipRepository;

    @GetMapping("/profile/{username}")
    public ModelAndView getProfile(@PathVariable String username, Principal principal) {
        ModelAndView profilePage = new ModelAndView("profiles/profile_page");

        User profileUser = userRepository.findByUsername(username).orElseThrow();
        profilePage.addObject("user", profileUser);

        // Friends list
        List<Friendship> acceptedFriendships = friendshipRepository
                .findByUserIdAndStatus(profileUser.getId(), "ACCEPTED");

        List<User> friends = acceptedFriendships.stream()
                .map(f -> {
                    Long friendId = f.getId().getRequesterId().equals(profileUser.getId())
                            ? f.getId().getAddresseeId()
                            : f.getId().getRequesterId();
                    return userRepository.findById(friendId).orElse(null);
                })
                .filter(u -> u != null)
                .collect(Collectors.toList());

        profilePage.addObject("friends", friends);

        // Like counts
        Map<Long, Long> likeCounts = new HashMap<>();
        for (Post post : profileUser.getPosts()) {
            likeCounts.put(post.getId(), postLikeRepository.countByIdPostId(post.getId()));
        }
        profilePage.addObject("likeCounts", likeCounts);

        // Friendship status relative to the logged-in user
        if (principal != null) {
            String currentUserEmail = getEmailFromPrincipal(principal);
            User currentUser = userRepository.findByEmail(currentUserEmail);

            if (currentUser != null && !currentUser.getId().equals(profileUser.getId())) {
                // Check all four directional combinations
                Optional<Friendship> iSentRequest = friendshipRepository
                        .findByIdRequesterIdAndIdAddresseeId(currentUser.getId(), profileUser.getId());
                Optional<Friendship> theySentRequest = friendshipRepository
                        .findByIdRequesterIdAndIdAddresseeId(profileUser.getId(), currentUser.getId());

                String friendshipStatus = "NONE";
                String friendshipDirection = null;

                if (iSentRequest.isPresent()) {
                    friendshipStatus = iSentRequest.get().getStatus(); // PENDING, ACCEPTED, or BLOCKED
                    friendshipDirection = "I_SENT";
                } else if (theySentRequest.isPresent()) {
                    friendshipStatus = theySentRequest.get().getStatus();
                    friendshipDirection = "THEY_SENT";
                }

                profilePage.addObject("friendshipStatus", friendshipStatus);
                profilePage.addObject("friendshipDirection", friendshipDirection);
            }
        }

        return profilePage;
    }

    private String getEmailFromPrincipal(Principal principal) {
        if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) principal;
            return token.getPrincipal().getAttribute("email");
        }
        return principal.getName();
    }
}