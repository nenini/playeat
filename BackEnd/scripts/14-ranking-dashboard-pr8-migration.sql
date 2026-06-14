SET NAMES utf8mb4;
USE nyamnyam;

CREATE TABLE IF NOT EXISTS guild_score_logs (
    score_log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    guild_id BIGINT NOT NULL,
    user_id BIGINT NULL,
    battle_id BIGINT NULL,
    source_type VARCHAR(50) NOT NULL,
    source_id BIGINT NULL,
    score INT NOT NULL,
    score_date DATE NOT NULL,
    description VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_guild_score_logs_guild
        FOREIGN KEY (guild_id)
        REFERENCES guilds(guild_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_guild_score_logs_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE SET NULL,
    CONSTRAINT fk_guild_score_logs_battle
        FOREIGN KEY (battle_id)
        REFERENCES boss_battles(battle_id)
        ON DELETE SET NULL,
    INDEX idx_guild_score_logs_guild_date (guild_id, score_date),
    INDEX idx_guild_score_logs_battle (battle_id),
    INDEX idx_guild_score_logs_source (source_type, source_id),
    INDEX idx_guild_score_logs_date (score_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
