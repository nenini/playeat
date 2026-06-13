USE nyamnyam;

CREATE TABLE IF NOT EXISTS guild_chats (
    chat_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    guild_id BIGINT NOT NULL,
    sender_user_id BIGINT NULL,
    message_type VARCHAR(20) NOT NULL DEFAULT 'USER',
    content VARCHAR(1000) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at DATETIME NULL,
    CONSTRAINT fk_guild_chats_guild
        FOREIGN KEY (guild_id)
        REFERENCES guilds(guild_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_guild_chats_sender
        FOREIGN KEY (sender_user_id)
        REFERENCES users(user_id)
        ON DELETE SET NULL,
    INDEX idx_guild_chats_guild_created (guild_id, created_at),
    INDEX idx_guild_chats_guild_chat_id (guild_id, chat_id),
    INDEX idx_guild_chats_sender (sender_user_id)
) ENGINE=InnoDB;

SET @has_guild_chat_message_type = (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'guild_chats'
      AND COLUMN_NAME = 'message_type'
);
SET @sql = IF(@has_guild_chat_message_type = 0, 'ALTER TABLE guild_chats ADD COLUMN message_type VARCHAR(20) NOT NULL DEFAULT ''USER'' AFTER sender_user_id', 'SELECT ''guild_chats.message_type exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

ALTER TABLE guild_chats
    MODIFY sender_user_id BIGINT NULL;

UPDATE guild_chats
SET message_type = 'USER'
WHERE message_type IS NULL OR message_type = '';

SET @has_idx_guild_chats_guild_created = (
    SELECT COUNT(1) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'guild_chats'
      AND INDEX_NAME = 'idx_guild_chats_guild_created'
);
SET @sql = IF(@has_idx_guild_chats_guild_created = 0, 'ALTER TABLE guild_chats ADD INDEX idx_guild_chats_guild_created (guild_id, created_at)', 'SELECT ''idx_guild_chats_guild_created exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_idx_guild_chats_guild_chat_id = (
    SELECT COUNT(1) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'guild_chats'
      AND INDEX_NAME = 'idx_guild_chats_guild_chat_id'
);
SET @sql = IF(@has_idx_guild_chats_guild_chat_id = 0, 'ALTER TABLE guild_chats ADD INDEX idx_guild_chats_guild_chat_id (guild_id, chat_id)', 'SELECT ''idx_guild_chats_guild_chat_id exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_idx_guild_chats_sender = (
    SELECT COUNT(1) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'guild_chats'
      AND INDEX_NAME = 'idx_guild_chats_sender'
);
SET @sql = IF(@has_idx_guild_chats_sender = 0, 'ALTER TABLE guild_chats ADD INDEX idx_guild_chats_sender (sender_user_id)', 'SELECT ''idx_guild_chats_sender exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
