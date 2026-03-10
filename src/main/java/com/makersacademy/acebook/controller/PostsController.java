package com.makersacademy.acebook.controller;

import com.makersacademy.acebook.model.Post;
import com.makersacademy.acebook.model.Comment;
import com.makersacademy.acebook.repository.PostRepository;
import com.makersacademy.acebook.repository.CommentRepository;
import com.makersacademy.acebook.repository.PostLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;
import java.util.HashMap;

import java.util.List;

@Controller
public class PostsController {

    @Autowired
    PostRepository repository;

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
    public RedirectView create(@ModelAttribute Post post) {
        repository.save(post);
        return new RedirectView("/posts");
    }
}
