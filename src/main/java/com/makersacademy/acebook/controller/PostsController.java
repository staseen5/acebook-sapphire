package com.makersacademy.acebook.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.makersacademy.acebook.model.Post;
import com.makersacademy.acebook.model.User;
import com.makersacademy.acebook.repository.PostRepository;
import com.makersacademy.acebook.repository.PostLikeRepository;
import com.makersacademy.acebook.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

@Controller
public class PostsController {

    @Autowired
    PostRepository repository;
    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    UserRepository userRepository;

    @Autowired
    PostLikeRepository postLikeRepository;

    @GetMapping("/posts")
    public String index(Model model) {
        Iterable<Post> posts = repository.findAllByOrderByCreatedAtDesc();

        Map<Long, Long> likeCounts = new HashMap<>();
        for (Post post : posts) {
            likeCounts.put(post.getId(), postLikeRepository.countByIdPostId(post.getId()));
        }

        model.addAttribute("posts", posts);
        model.addAttribute("post", new Post());
        model.addAttribute("likeCounts", likeCounts);
        return "posts/index";
    }

    @PostMapping("/posts")
    public RedirectView create(@ModelAttribute Post post,
                               @RequestParam("file") MultipartFile file,
                               Principal principal) throws IOException {

        if (principal != null) {
            User user = userRepository.findByUsername(principal.getName());
            post.setUserId(user.getId());
        }

        if(!file.isEmpty()){
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

            String publicUrl = (String) uploadResult.get("secure_url");
            post.setImageUrl(publicUrl);
        }

        post.setCreatedAt(ZonedDateTime.now());
        repository.save(post);
        return new RedirectView("/posts");
    }
}
