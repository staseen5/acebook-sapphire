package com.makersacademy.acebook.model;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class PostTest {
	private User testUser = new User("Test User");
	private Post post = new Post("hello", testUser);

	@Test
	public void postHasContent() {
		assertThat(post.getContent(), containsString("hello"));
	}
}
