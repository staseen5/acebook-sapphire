package com.makersacademy.acebook.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.makersacademy.acebook.model.Post;
import com.makersacademy.acebook.model.Comment;
import com.makersacademy.acebook.model.User;
import com.makersacademy.acebook.repository.*;
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

    @Autowired
    FriendshipRepository friendshipRepository;

    @GetMapping("/")
    public String index(Model model, Principal principal) {
        Iterable<Post> allPosts = repository.findAllByOrderByCreatedAtDesc();

        // Build set of user IDs to hide posts from (blocked in either direction)
        java.util.Set<Long> blockedUserIds = new java.util.HashSet<>();
        if (principal instanceof OAuth2AuthenticationToken token) {
            String email = token.getPrincipal().getAttribute("email");
            User currentUser = userRepository.findByEmail(email);
            if (currentUser != null) {
                // People the current user has blocked
                friendshipRepository.findByIdRequesterIdAndStatus(currentUser.getId(), "BLOCKED")
                        .forEach(f -> blockedUserIds.add(f.getId().getAddresseeId()));
                // People who have blocked the current user
                friendshipRepository.findByIdAddresseeIdAndStatus(currentUser.getId(), "BLOCKED")
                        .forEach(f -> blockedUserIds.add(f.getId().getRequesterId()));
            }
        }

        // Filter posts
        List<Post> posts = new java.util.ArrayList<>();
        for (Post post : allPosts) {
            if (!blockedUserIds.contains(post.getUser().getId())) {
                posts.add(post);
            }
        }

        model.addAttribute("posts", posts);

        Map<Long, Long> likeCounts = new HashMap<>();
        java.util.Set<Long> likedPostIds = new java.util.HashSet<>();

        for (Post post : posts) {
            likeCounts.put(post.getId(), postLikeRepository.countByIdPostId(post.getId()));
        }

        if (principal instanceof OAuth2AuthenticationToken token) {
            String email = token.getPrincipal().getAttribute("email");
            User currentUser = userRepository.findByEmail(email);
            if (currentUser != null) {
                for (Post post : posts) {
                    if (postLikeRepository.existsByIdUserIdAndIdPostId(currentUser.getId(), post.getId())) {
                        likedPostIds.add(post.getId());
                    }
                }
            }
        }

        model.addAttribute("likeCounts", likeCounts);
        model.addAttribute("likedPostIds", likedPostIds);
        model.addAttribute("post", new Post());

        return "posts/index";
    }

    @GetMapping("/search")
    public String searchPosts(@RequestParam String keyword, Model model) {
        List<Post> filteredPosts = repository.findPostsByContentContainsIgnoreCase(keyword);
        model.addAttribute("posts", filteredPosts);

        // Create hash of post id : amount of likes
        Map<Long, Long> likeCounts = new HashMap<>();
        for (Post post : filteredPosts) {
            likeCounts.put(post.getId(), postLikeRepository.countByIdPostId(post.getId()));
        }
        model.addAttribute("likeCounts", likeCounts);

        model.addAttribute("post", new Post());

        return "posts/index";
    }

    @PostMapping("/")
    public RedirectView create(@ModelAttribute Post post,
                               @RequestParam("image") MultipartFile file,
                               Principal principal) throws IOException {

        if (principal != null) {
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) principal;
            String email = token.getPrincipal().getAttribute("email");
            User user = userRepository.findByEmail(email);
            post.setUser(user);
        }

        if (!file.isEmpty()) {
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
        User user = userRepository.findByEmail(email);
        new_comment.setUser(user);

        Post post = repository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid post id: " + postId));
        new_comment.setPost(post);

        if (new_comment.getBody() == null || new_comment.getBody().trim().isEmpty()) {
            return new RedirectView("/");
        }

        commentRepository.save(new_comment);
        return new RedirectView("/");
    }
}
