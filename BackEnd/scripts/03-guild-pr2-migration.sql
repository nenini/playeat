USE nyamnyam;

CREATE TABLE IF NOT EXISTS guild_notices (
    notice_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    guild_id BIGINT NOT NULL,
    writer_user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_guild_notices_guild
        FOREIGN KEY (guild_id)
        REFERENCES guilds(guild_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_guild_notices_writer
        FOREIGN KEY (writer_user_id)
        REFERENCES users(user_id)
        ON DELETE RESTRICT,
    INDEX idx_guild_notices_guild_created (guild_id, created_at)
) ENGINE=InnoDB;
