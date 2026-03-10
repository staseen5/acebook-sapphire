package com.makersacademy.acebook.model;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class PostTest {

	private Post post = new Post("hello", 1L);

	@Test
	public void postHasContent() {
		assertThat(post.getContent(), containsString("hello"));
	}

}
