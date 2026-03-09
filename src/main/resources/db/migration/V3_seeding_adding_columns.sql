-- Add user_id foreign key and created_at timestamp to posts
ALTER TABLE posts
    ADD COLUMN user_id bigint NOT NULL REFERENCES users(id),
    ADD COLUMN created_at timestamptz NOT NULL DEFAULT now();

-- Seed users
INSERT INTO users (username, enabled) VALUES
                                          ('alice',   true),
                                          ('bob',     true),
                                          ('charlie', false);

-- Seed posts
INSERT INTO posts (content, user_id, created_at) VALUES
                                                     ('Hello from Alice! This is my first post.',          (SELECT id FROM users WHERE username = 'alice'),   now() - interval '3 days'),
                                                     ('Alice again, sharing some thoughts today.',         (SELECT id FROM users WHERE username = 'alice'),   now() - interval '1 day'),
                                                     ('Hey everyone, Bob here!',                           (SELECT id FROM users WHERE username = 'bob'),     now() - interval '2 days'),
                                                     ('Bob posting again. Loving this platform so far.',   (SELECT id FROM users WHERE username = 'bob'),     now() - interval '6 hours'),
                                                     ('Charlie checking in. Not sure I like it here yet.', (SELECT id FROM users WHERE username = 'charlie'), now() - interval '12 hours');