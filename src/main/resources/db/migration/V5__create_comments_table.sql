CREATE TABLE comments (
    id bigserial PRIMARY KEY,
    body VARCHAR(250) NOT NULL,
    -- The id of the post being commented on
    post_id bigint NOT NULL REFERENCES posts(id),
    -- The id of the user making the comment
    user_id bigint NOT NULL REFERENCES users(id)
);

INSERT INTO comments (body, post_id, user_id) VALUES
                                                ('This is Alice - nice post Bob!', (SELECT id FROM posts WHERE content = 'Hey everyone, Bob here!'), (SELECT id FROM users WHERE username = 'alice')),
                                                ('This is Alice - nice post Charlie!', (SELECT id FROM posts WHERE content = 'Charlie checking in. Not sure I like it here yet.'), (SELECT id FROM users WHERE username = 'alice')),
                                                ('Charlie', (SELECT id FROM posts WHERE content = 'Hello from Alice! This is my first post.'), (SELECT id FROM users WHERE username = 'charlie'));