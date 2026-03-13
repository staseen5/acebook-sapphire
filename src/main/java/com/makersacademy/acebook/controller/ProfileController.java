package com.makersacademy.acebook.controller;

import com.makersacademy.acebook.model.Friendship;
import com.makersacademy.acebook.model.Post;
import com.makersacademy.acebook.model.User;
import com.makersacademy.acebook.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    // tell Spring Boot this method handles the "GET '/'" request
    @GetMapping("/profile/{username}")
    public ModelAndView getProfile(@PathVariable String username) {
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

        return profilePage;
    }
}