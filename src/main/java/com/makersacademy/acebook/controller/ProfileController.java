package com.makersacademy.acebook.controller;

import com.makersacademy.acebook.model.Friendship;
import com.makersacademy.acebook.model.Post;
import com.makersacademy.acebook.model.User;
import com.makersacademy.acebook.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
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
import java.util.*;
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
    PostTagRepository postTagRepository;
    @Autowired
    FriendshipRepository friendshipRepository;

    // tell Spring Boot this method handles the "GET '/'" request
    @GetMapping("/profile/{username}")
    public ModelAndView getProfile(@PathVariable String username, Principal principal) {

        if (username == null || username.isBlank()) {
                return new ModelAndView("redirect:/");
        }

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

        // Tagged + own posts merged (from theirs)
        List<Post> taggedPosts = postTagRepository.findByIdUserId(profileUser.getId())
                .stream()
                .map(tag -> postRepository.findById(tag.getId().getPostId()).orElse(null))
                .filter(Objects::nonNull)
                .toList();

        List<Post> ownPosts = profileUser.getPosts() != null
                ? new ArrayList<>(profileUser.getPosts())
                : new ArrayList<>();

        List<Post> allPosts = new ArrayList<>(ownPosts);
        for (Post tagged : taggedPosts) {
            if (allPosts.stream().noneMatch(p -> p.getId().equals(tagged.getId()))) {
                allPosts.add(tagged);
            }
        }
        allPosts.sort(Comparator.comparing(Post::getCreatedAt).reversed());
        profilePage.addObject("allPosts", allPosts);

        List<Post> photoPostsOwn = ownPosts.stream()
                .filter(p -> p.getImageUrl() != null && !p.getImageUrl().isBlank())
                .toList();

        List<Post> photoPostsTagged = taggedPosts.stream()
                .filter(p -> p.getImageUrl() != null && !p.getImageUrl().isBlank())
                .filter(p -> photoPostsOwn.stream().noneMatch(o -> o.getId().equals(p.getId())))
                .toList();

        List<Post> photoPosts = new ArrayList<>(photoPostsOwn);
        photoPosts.addAll(photoPostsTagged);
        photoPosts.sort(Comparator.comparing(Post::getCreatedAt).reversed());
        profilePage.addObject("photoPosts", photoPosts);

        // Friendship status relative to the logged-in user
        if (principal != null) {
            String currentUserEmail = getEmailFromPrincipal(principal);
            User currentUser = userRepository.findByEmail(currentUserEmail);

            if (currentUser != null && !currentUser.getId().equals(profileUser.getId())) {
                Optional<Friendship> iSentRequest = friendshipRepository
                        .findByIdRequesterIdAndIdAddresseeId(currentUser.getId(), profileUser.getId());
                Optional<Friendship> theySentRequest = friendshipRepository
                        .findByIdRequesterIdAndIdAddresseeId(profileUser.getId(), currentUser.getId());

                String friendshipStatus = "NONE";
                String friendshipDirection = null;

                if (iSentRequest.isPresent()) {
                    friendshipStatus = iSentRequest.get().getStatus();
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