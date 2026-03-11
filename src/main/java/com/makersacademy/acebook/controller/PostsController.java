package com.makersacademy.acebook.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.makersacademy.acebook.model.Post;
import com.makersacademy.acebook.model.Comment;
import com.makersacademy.acebook.model.User;
import com.makersacademy.acebook.repository.PostRepository;
import com.makersacademy.acebook.repository.CommentRepository;
import com.makersacademy.acebook.repository.PostLikeRepository;
import com.makersacademy.acebook.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
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
    CommentRepository commentRepository;

    @Autowired
    PostLikeRepository postLikeRepository;

    @GetMapping("/")
    public String index(Model model) {
        // Get all posts in descending order
        Iterable<Post> posts = repository.findAllByOrderByCreatedAtDesc();

        model.addAttribute("posts", posts);

        Map<Long, List<Comment>> commentsByPostId = new HashMap<>();
        for (Post p : posts) {
            commentsByPostId.put(p.getId(), commentRepository.findByPostId(p.getId()));
        }

        model.addAttribute("commentsByPostId", commentsByPostId);

        // Create hash of post id : amount of likes
        Map<Long, Long> likeCounts = new HashMap<>();
        for (Post post : posts) {
            likeCounts.put(post.getId(), postLikeRepository.countByIdPostId(post.getId()));
        }
        model.addAttribute("likeCounts", likeCounts);

        model.addAttribute("post", new Post());

        return "posts/index";
    }

    @PostMapping("/")
    public RedirectView create(@ModelAttribute Post post,
                               @RequestParam("file") MultipartFile file,
                               Principal principal) throws IOException {

        if (principal != null) {
            User user = userRepository.findByUsername(principal.getName());
            post.setUser(user);
        }

        if(!file.isEmpty()){
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

            String publicUrl = (String) uploadResult.get("secure_url");
            post.setImageUrl(publicUrl);
        }

        post.setCreatedAt(ZonedDateTime.now());
        repository.save(post);
        return new RedirectView("/");
    }

    @PostMapping("/comments/new")
    public RedirectView create(@ModelAttribute Comment new_comment, @RequestParam("postId") Long postId, Principal principal) throws IOException {

        new_comment.setCommentedOn(ZonedDateTime.now());

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) principal;
        String email = token.getPrincipal().getAttribute("email");
        // This will need to be changed if we use username instead of email
        User user = userRepository.findByUsername(email);
        new_comment.setUser(user);

        Post post = repository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid post id: " + postId));
        new_comment.setPost(post);

        if (new_comment.getBody() == null || new_comment.getBody().trim().isEmpty()) {
            return new RedirectView("/posts");
        }

        commentRepository.save(new_comment);
        return new RedirectView("/");
    }
}
