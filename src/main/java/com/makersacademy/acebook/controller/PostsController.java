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
    CommentRepository commentRepository;

    @Autowired
    PostLikeRepository postLikeRepository;

    @GetMapping("/posts")
    public String index(Model model) {
        HashMap<Post, List<Comment>> postsWithComments = new HashMap<Post, List<Comment>>();
        Iterable<Post> posts = repository.findAllByOrderByCreatedAtDesc();

        Map<Long, Long> likeCounts = new HashMap<>();
        for (Post post : posts) {
            likeCounts.put(post.getId(), postLikeRepository.countByIdPostId(post.getId()));
        }

        for(Post p: posts) {
            List<Comment> comments = commentRepository.findByPostId(p.getId());
            postsWithComments.put(p, comments);
        }

        model.addAttribute("posts_with_comments", postsWithComments);
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

    @PostMapping("/comments/new")
    public RedirectView create(@ModelAttribute Comment new_comment, Principal principal) throws IOException {
        new_comment.setCommentedOn(ZonedDateTime.now());

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) principal;
        String email = token.getPrincipal().getAttribute("email");
        // This will need to be changed if we use username instead of email
        User user = userRepository.findByUsername(email);
        new_comment.setUserId(user.getId());


        commentRepository.save(new_comment);
        return new RedirectView("/posts");
    }
}
