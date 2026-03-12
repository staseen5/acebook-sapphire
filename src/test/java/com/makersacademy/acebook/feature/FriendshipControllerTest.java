package com.makersacademy.acebook.feature;

import com.cloudinary.Cloudinary;
import com.makersacademy.acebook.model.Friendship;
import com.makersacademy.acebook.model.User;
import com.makersacademy.acebook.repository.FriendshipRepository;
import com.makersacademy.acebook.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

    private User requester;
    private User addressee;

    @BeforeEach
    public void setup() {
        friendshipRepository.deleteAll();

        Optional<User> requesterUser = userRepository.findByUsername("user");
        if (requesterUser.isEmpty()) {
            requester = userRepository.save(new User("user"));
        } else {
            requester = requesterUser.get();
        }

        Optional<User> addresseeUser = userRepository.findByUsername("otheruser");
        if (addresseeUser.isEmpty()) {
            addressee = userRepository.save(new User("otheruser"));
        } else {
            addressee = addresseeUser.get();
        }
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