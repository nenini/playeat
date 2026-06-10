USE nyamnyam;

CREATE TABLE IF NOT EXISTS guild_join_requests (
    request_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    guild_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    handled_by BIGINT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    handled_at DATETIME NULL,
    CONSTRAINT fk_join_requests_guild
        FOREIGN KEY (guild_id)
        REFERENCES guilds(guild_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_join_requests_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_join_requests_handled_by
        FOREIGN KEY (handled_by)
        REFERENCES users(user_id)
        ON DELETE SET NULL
) ENGINE=InnoDB;

SET @has_join_request_message = (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'guild_join_requests'
      AND COLUMN_NAME = 'message'
);
SET @sql = IF(
    @has_join_request_message > 0,
    'ALTER TABLE guild_join_requests DROP COLUMN message',
    'SELECT ''guild_join_requests.message already removed'''
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_idx_join_requests_guild_status = (
    SELECT COUNT(1)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'guild_join_requests'
      AND INDEX_NAME = 'idx_join_requests_guild_status'
);
SET @sql = IF(
    @has_idx_join_requests_guild_status = 0,
    'CREATE INDEX idx_join_requests_guild_status ON guild_join_requests (guild_id, status)',
    'SELECT ''idx_join_requests_guild_status already exists'''
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_idx_join_requests_user_status = (
    SELECT COUNT(1)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'guild_join_requests'
      AND INDEX_NAME = 'idx_join_requests_user_status'
);
SET @sql = IF(
    @has_idx_join_requests_user_status = 0,
    'CREATE INDEX idx_join_requests_user_status ON guild_join_requests (user_id, status)',
    'SELECT ''idx_join_requests_user_status already exists'''
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_idx_join_requests_guild_user_status = (
    SELECT COUNT(1)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'guild_join_requests'
      AND INDEX_NAME = 'idx_join_requests_guild_user_status'
);
SET @sql = IF(
    @has_idx_join_requests_guild_user_status = 0,
    'CREATE INDEX idx_join_requests_guild_user_status ON guild_join_requests (guild_id, user_id, status)',
    'SELECT ''idx_join_requests_guild_user_status already exists'''
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
