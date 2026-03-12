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

    @Test
    public void sendingFriendRequestCreatesPendingFriendship() throws Exception {
        mockMvc.perform(post("/friends/request/otheruser")
                        .with(oidcLogin().userInfoToken(t -> t.claim("email", REQUESTER_EMAIL)))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        Optional<Friendship> friendship = friendshipRepository
                .findByIdRequesterIdAndIdAddresseeId(requester.getId(), addressee.getId());

        assertTrue(friendship.isPresent());
        assertEquals("PENDING", friendship.get().getStatus());
    }

    @Test
    public void acceptingFriendRequestUpdatesStatusToAccepted() throws Exception {
        friendshipRepository.save(makeFriendship(addressee, requester, "PENDING"));

        mockMvc.perform(post("/friends/accept/otheruser")
                        .with(oidcLogin().userInfoToken(t -> t.claim("email", REQUESTER_EMAIL)))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        Optional<Friendship> friendship = friendshipRepository
                .findByIdRequesterIdAndIdAddresseeId(addressee.getId(), requester.getId());

        assertTrue(friendship.isPresent());
        assertEquals("ACCEPTED", friendship.get().getStatus());
    }

    @Test
    public void decliningFriendRequestDeletesRecord() throws Exception {
        friendshipRepository.save(makeFriendship(addressee, requester, "PENDING"));

        mockMvc.perform(post("/friends/decline/otheruser")
                        .with(oidcLogin().userInfoToken(t -> t.claim("email", REQUESTER_EMAIL)))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        Optional<Friendship> friendship = friendshipRepository
                .findByIdRequesterIdAndIdAddresseeId(addressee.getId(), requester.getId());

        assertFalse(friendship.isPresent());
    }

    @Test
    public void blockingUserCreatesBlockedFriendship() throws Exception {
        mockMvc.perform(post("/friends/block/otheruser")
                        .with(oidcLogin().userInfoToken(t -> t.claim("email", REQUESTER_EMAIL)))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        Optional<Friendship> friendship = friendshipRepository
                .findByIdRequesterIdAndIdAddresseeId(requester.getId(), addressee.getId());

        assertTrue(friendship.isPresent());
        assertEquals("BLOCKED", friendship.get().getStatus());
    }

    @Test
    public void blockingExistingFriendUpdatesStatusToBlocked() throws Exception {
        friendshipRepository.save(makeFriendship(requester, addressee, "ACCEPTED"));

        mockMvc.perform(post("/friends/block/otheruser")
                        .with(oidcLogin().userInfoToken(t -> t.claim("email", REQUESTER_EMAIL)))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        Optional<Friendship> friendship = friendshipRepository
                .findByIdRequesterIdAndIdAddresseeId(requester.getId(), addressee.getId());

        assertTrue(friendship.isPresent());
        assertEquals("BLOCKED", friendship.get().getStatus());
    }

    @Test
    public void unfriendingDeletesRecord() throws Exception {
        friendshipRepository.save(makeFriendship(requester, addressee, "ACCEPTED"));

        mockMvc.perform(post("/friends/unfriend/otheruser")
                        .with(oidcLogin().userInfoToken(t -> t.claim("email", REQUESTER_EMAIL)))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        Optional<Friendship> friendship = friendshipRepository
                .findByIdRequesterIdAndIdAddresseeId(requester.getId(), addressee.getId());

        assertFalse(friendship.isPresent());
    }

    @Test
    public void blockedUserCannotSendFriendRequestToBlocker() throws Exception {
        friendshipRepository.save(makeFriendship(addressee, requester, "BLOCKED"));

        mockMvc.perform(post("/friends/request/otheruser")
                        .with(oidcLogin().userInfoToken(t -> t.claim("email", REQUESTER_EMAIL)))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        Optional<Friendship> friendship = friendshipRepository
                .findByIdRequesterIdAndIdAddresseeId(requester.getId(), addressee.getId());

        assertFalse(friendship.isPresent());
    }

    @Test
    public void userCannotSendFriendRequestToBlockedUser() throws Exception {
        friendshipRepository.save(makeFriendship(requester, addressee, "BLOCKED"));

        mockMvc.perform(post("/friends/request/otheruser")
                        .with(oidcLogin().userInfoToken(t -> t.claim("email", REQUESTER_EMAIL)))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        Optional<Friendship> friendship = friendshipRepository
                .findByIdRequesterIdAndIdAddresseeId(requester.getId(), addressee.getId());

        assertTrue(friendship.isPresent());
        assertEquals("BLOCKED", friendship.get().getStatus());
    }
}