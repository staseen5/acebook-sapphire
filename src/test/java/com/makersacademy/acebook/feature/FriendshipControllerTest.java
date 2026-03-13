package com.makersacademy.acebook.feature;

import com.cloudinary.Cloudinary;
import com.makersacademy.acebook.model.Friendship;
import com.makersacademy.acebook.model.User;
import com.makersacademy.acebook.repository.CommentRepository;
import com.makersacademy.acebook.repository.FriendshipRepository;
import com.makersacademy.acebook.repository.PostRepository;
import com.makersacademy.acebook.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FriendshipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private Cloudinary cloudinary;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    private User requester;
    private User addressee;

    private static final String REQUESTER_EMAIL = "user@test.com";

    @BeforeEach
    public void setup() {
        friendshipRepository.deleteAll();
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();

        requester = userRepository.save(new User(REQUESTER_EMAIL));

        addressee = new User("otheruser@test.com");
        addressee.setUsername("otheruser");
        addressee = userRepository.save(addressee);
    }

    private Friendship makeFriendship(User from, User to, String status) {
        Friendship f = new Friendship(from.getId(), to.getId());
        f.setRequester(from);
        f.setAddressee(to);
        f.setStatus(status);
        return f;
    }

//    @Test
//    @WithMockUser
//    public void sendingFriendRequestCreatesPendingFriendship() throws Exception {
//        mockMvc.perform(post("/friendships/request/otheruser"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(header().string("Location", "/profile/otheruser"));
//
//        Optional<Friendship> friendship = friendshipRepository
//                .findByIdRequesterIdAndIdAddresseeId(requester.getId(), addressee.getId());
//
//        assertTrue(friendship.isPresent());
//        assertEquals("PENDING", friendship.get().getStatus());
//    }
//
//    @Test
//    @WithMockUser
//    public void acceptingFriendRequestUpdatesStatusToAccepted() throws Exception {
//        // otheruser sent a request TO the logged in user
//        friendshipRepository.save(new Friendship(addressee.getId(), requester.getId()));
//
//        mockMvc.perform(post("/friendships/accept/otheruser"))
//                .andExpect(status().is3xxRedirection());
//
//        Optional<Friendship> friendship = friendshipRepository
//                .findByIdRequesterIdAndIdAddresseeId(addressee.getId(), requester.getId());
//
//        assertTrue(friendship.isPresent());
//        assertEquals("ACCEPTED", friendship.get().getStatus());
//    }
//
//    @Test
//    @WithMockUser
//    public void decliningFriendRequestDeletesRecord() throws Exception {
//        // otheruser sent a request TO the logged in user
//        friendshipRepository.save(new Friendship(addressee.getId(), requester.getId()));
//
//        mockMvc.perform(post("/friendships/decline/otheruser"))
//                .andExpect(status().is3xxRedirection());
//
//        Optional<Friendship> friendship = friendshipRepository
//                .findByIdRequesterIdAndIdAddresseeId(addressee.getId(), requester.getId());
//
//        assertFalse(friendship.isPresent());
//    }
//
//    @Test
//    @WithMockUser
//    public void blockingUserCreatesBlockedFriendship() throws Exception {
//        mockMvc.perform(post("/friendships/block/otheruser"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(header().string("Location", "/profile/otheruser"));
//
//        Optional<Friendship> friendship = friendshipRepository
//                .findByIdRequesterIdAndIdAddresseeId(requester.getId(), addressee.getId());
//
//        assertTrue(friendship.isPresent());
//        assertEquals("BLOCKED", friendship.get().getStatus());
//    }
//
//    @Test
//    @WithMockUser
//    public void blockingExistingFriendUpdatesStatusToBlocked() throws Exception {
//        Friendship existing = new Friendship(requester.getId(), addressee.getId());
//        existing.setStatus("ACCEPTED");
//        friendshipRepository.save(existing);
//
//        mockMvc.perform(post("/friendships/block/otheruser"))
//                .andExpect(status().is3xxRedirection());
//
//        Optional<Friendship> friendship = friendshipRepository
//                .findByIdRequesterIdAndIdAddresseeId(requester.getId(), addressee.getId());
//
//        assertTrue(friendship.isPresent());
//        assertEquals("BLOCKED", friendship.get().getStatus());
//    }
//
//    @Test
//    @WithMockUser
//    public void unfriendingDeletesRecord() throws Exception {
//        Friendship existing = new Friendship(requester.getId(), addressee.getId());
//        existing.setStatus("ACCEPTED");
//        friendshipRepository.save(existing);
//
//        mockMvc.perform(post("/friendships/unfriend/otheruser"))
//                .andExpect(status().is3xxRedirection());
//
//        Optional<Friendship> friendship = friendshipRepository
//                .findByIdRequesterIdAndIdAddresseeId(requester.getId(), addressee.getId());
//
//        assertFalse(friendship.isPresent());
//    }
}