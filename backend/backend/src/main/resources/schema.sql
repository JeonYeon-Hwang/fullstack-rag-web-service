CREATE TABLE user_entity (
    id BIGINT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(255) NOT NULL
);


CREATE TABLE jwt_refresh_entity (
    id BIGINT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    refresh VARCHAR(255) NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE post_entity (
    id BIGINT PRIMARY KEY,
    userId BIGINT,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);

CREATE TABLE post_tag (
    post_id BIGINT NOT NULL REFERENCES post_entity(id),
    tag VARCHAR(255)
);

CREATE TABLE comment_entity (
    id BIGINT PRIMARY KEY,
    postId BIGINT,
    userId BIGINT,
    comment TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_activity_entity (
    id BIGINT PRIMARY KEY,
    userId BIGINT,
    postId BIGINT,
    activity_type VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);