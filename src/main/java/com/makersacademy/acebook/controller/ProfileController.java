package com.makersacademy.acebook.controller;

import com.makersacademy.acebook.model.Friendship;
import com.makersacademy.acebook.model.Post;
import com.makersacademy.acebook.model.User;
import com.makersacademy.acebook.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

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
    public ModelAndView getProfile(@PathVariable String username) {
        if (username == null || username.isBlank()) {
                return new ModelAndView("redirect:/");
        }
        ModelAndView profilePage = new ModelAndView("profiles/profile_page");

        User currentUser = userRepository.findByUsername(username).orElseThrow();
        profilePage.addObject("user", currentUser);

        // Create list of friends
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

        profilePage.addObject("friends", friends);

        // Create hash of post id : amount of likes
        Map<Long, Long> likeCounts = new HashMap<>();
        for (Post post : currentUser.getPosts()) {
            likeCounts.put(post.getId(), postLikeRepository.countByIdPostId(post.getId()));
        }
        profilePage.addObject("likeCounts", likeCounts);

        List<Post> taggedPosts = postTagRepository.findByIdUserId(currentUser.getId())
                .stream()
                .map(tag -> postRepository.findById(tag.getId().getPostId()).orElse(null))
                .filter(Objects::nonNull)
                .toList();

        List<Post> ownPosts = currentUser.getPosts() != null
                ? new ArrayList<>(currentUser.getPosts())
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

        return profilePage;
    }
}