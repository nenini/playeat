DROP TABLE IF EXISTS guild_join_requests;
DROP TABLE IF EXISTS guild_notices;
DROP TABLE IF EXISTS guild_members;
DROP TABLE IF EXISTS guilds;
DROP TABLE IF EXISTS characters;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    nickname VARCHAR(50) NOT NULL,
    profile_image_url VARCHAR(500),
    selected_coach_id BIGINT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    onboarding_completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deactivated_at DATETIME,
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_nickname UNIQUE (nickname)
);

CREATE TABLE characters (
    character_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    level INT NOT NULL DEFAULT 1,
    xp INT NOT NULL DEFAULT 0,
    stage VARCHAR(30) NOT NULL DEFAULT 'EGG',
    mood VARCHAR(30) NOT NULL DEFAULT 'NORMAL',
    appearance_type VARCHAR(30) NOT NULL DEFAULT 'NORMAL',
    streak_days INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_characters_user UNIQUE (user_id)
);

CREATE TABLE guilds (
    guild_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    invite_code VARCHAR(50) NOT NULL,
    owner_user_id BIGINT NOT NULL,
    max_members INT NOT NULL DEFAULT 30,
    guild_point INT NOT NULL DEFAULT 0,
    visibility VARCHAR(20) NOT NULL DEFAULT 'PRIVATE',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_guilds_name UNIQUE (name),
    CONSTRAINT uk_guilds_invite_code UNIQUE (invite_code)
);

CREATE TABLE guild_members (
    guild_member_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    guild_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
    joined_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    left_at DATETIME,
    CONSTRAINT uk_guild_members_guild_user UNIQUE (guild_id, user_id)
);

CREATE TABLE guild_notices (
    notice_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    guild_id BIGINT NOT NULL,
    writer_user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE guild_join_requests (
    request_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    guild_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    message VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    handled_by BIGINT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    handled_at DATETIME
);
