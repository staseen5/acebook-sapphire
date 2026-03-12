package com.makersacademy.acebook.model;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class FriendshipTest {

    private Friendship friendship = new Friendship(1L, 2L);

    @Test
    public void newFriendshipHasPendingStatus() {
        assertThat(friendship.getStatus(), is("PENDING"));
    }

    @Test
    public void friendshipStatusCanBeUpdatedToAccepted() {
        friendship.setStatus("ACCEPTED");
        assertThat(friendship.getStatus(), is("ACCEPTED"));
    }

    @Test
    public void friendshipStatusCanBeUpdatedToBlocked() {
        friendship.setStatus("BLOCKED");
        assertThat(friendship.getStatus(), is("BLOCKED"));
    }

    @Test
    public void friendshipHasCorrectRequesterId() {
        assertThat(friendship.getId().getRequesterId(), is(1L));
    }

    @Test
    public void friendshipHasCorrectAddresseeId() {
        assertThat(friendship.getId().getAddresseeId(), is(2L));
    }
}