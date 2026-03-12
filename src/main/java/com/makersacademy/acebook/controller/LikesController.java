package com.makersacademy.acebook.controller;

import com.makersacademy.acebook.model.PostLike;
import com.makersacademy.acebook.model.User;
import com.makersacademy.acebook.repository.PostLikeRepository;
import com.makersacademy.acebook.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
public class LikesController {

    @Autowired
    PostLikeRepository postLikeRepository;

    @Autowired
    UserRepository userRepository;

    @PostMapping("/posts/{id}/like")
    public Map<String, Object> toggleLike(@PathVariable Long id, Principal principal) {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) principal;
        String email = token.getPrincipal().getAttribute("email");
        User user = userRepository.findByEmail(email);

        boolean alreadyLiked = postLikeRepository.existsByIdUserIdAndIdPostId(user.getId(), id);

        if (alreadyLiked) {
            PostLike like = new PostLike(user.getId(), id);
            postLikeRepository.delete(like);
        } else {
            PostLike like = new PostLike(user.getId(), id);
            postLikeRepository.save(like);
        }

        long newCount = postLikeRepository.countByIdPostId(id);
        boolean nowLiked = !alreadyLiked;

        return Map.of("likes", newCount, "liked", nowLiked);
    }
}