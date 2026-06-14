SET NAMES utf8mb4;
USE nyamnyam;

CREATE TABLE IF NOT EXISTS quest_verifications (
    verification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quest_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    battle_id BIGINT NOT NULL,
    summary_id BIGINT NULL,
    diet_id BIGINT NULL,
    quest_type VARCHAR(50) NOT NULL,
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    damage_amount INT NOT NULL DEFAULT 0,
    message VARCHAR(500),
    verified_date DATE NULL,
    verified_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_quest_verifications_quest UNIQUE (quest_id),
    CONSTRAINT fk_quest_verifications_quest
        FOREIGN KEY (quest_id) REFERENCES quests(quest_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_quest_verifications_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_quest_verifications_battle
        FOREIGN KEY (battle_id) REFERENCES boss_battles(battle_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_quest_verifications_summary
        FOREIGN KEY (summary_id) REFERENCES daily_nutrition_summaries(summary_id)
        ON DELETE SET NULL,
    CONSTRAINT fk_quest_verifications_diet
        FOREIGN KEY (diet_id) REFERENCES diets(diet_id)
        ON DELETE SET NULL,
    INDEX idx_quest_verifications_quest (quest_id),
    INDEX idx_quest_verifications_user_date (user_id, verified_date),
    INDEX idx_quest_verifications_battle (battle_id)
) ENGINE=InnoDB;

SET @sql := (
    SELECT IF(COUNT(*) = 0,
        'ALTER TABLE quest_verifications ADD COLUMN user_id BIGINT NULL AFTER quest_id',
        'SELECT 1')
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'quest_verifications'
      AND COLUMN_NAME = 'user_id'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql := (
    SELECT IF(COUNT(*) = 0,
        'ALTER TABLE quest_verifications ADD COLUMN battle_id BIGINT NULL AFTER user_id',
        'SELECT 1')
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'quest_verifications'
      AND COLUMN_NAME = 'battle_id'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql := (
    SELECT IF(COUNT(*) = 0,
        'ALTER TABLE quest_verifications ADD COLUMN summary_id BIGINT NULL AFTER battle_id',
        'SELECT 1')
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'quest_verifications'
      AND COLUMN_NAME = 'summary_id'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql := (
    SELECT IF(COUNT(*) = 0,
        'ALTER TABLE quest_verifications ADD COLUMN quest_type VARCHAR(50) NULL AFTER diet_id',
        'SELECT 1')
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'quest_verifications'
      AND COLUMN_NAME = 'quest_type'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql := (
    SELECT IF(COUNT(*) = 0,
        'ALTER TABLE quest_verifications ADD COLUMN verified_date DATE NULL AFTER message',
        'SELECT 1')
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'quest_verifications'
      AND COLUMN_NAME = 'verified_date'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql := (
    SELECT IF(COUNT(*) > 0,
        'ALTER TABLE quest_verifications MODIFY COLUMN diet_id BIGINT NULL',
        'SELECT 1')
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'quest_verifications'
      AND COLUMN_NAME = 'diet_id'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql := (
    SELECT IF(COUNT(*) = 0,
        'CREATE UNIQUE INDEX uk_quest_verifications_quest ON quest_verifications (quest_id)',
        'SELECT 1')
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'quest_verifications'
      AND INDEX_NAME = 'uk_quest_verifications_quest'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql := (
    SELECT IF(COUNT(*) = 0,
        'CREATE INDEX idx_quest_verifications_user_date ON quest_verifications (user_id, verified_date)',
        'SELECT 1')
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'quest_verifications'
      AND INDEX_NAME = 'idx_quest_verifications_user_date'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql := (
    SELECT IF(COUNT(*) = 0,
        'CREATE INDEX idx_quest_verifications_battle ON quest_verifications (battle_id)',
        'SELECT 1')
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'quest_verifications'
      AND INDEX_NAME = 'idx_quest_verifications_battle'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

CREATE TABLE IF NOT EXISTS reward_claims (
    reward_claim_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    source_type VARCHAR(30) NOT NULL,
    source_id BIGINT NOT NULL,
    xp_amount INT NOT NULL DEFAULT 0,
    badge_id BIGINT NULL,
    guild_point INT NOT NULL DEFAULT 0,
    coin_amount INT NOT NULL DEFAULT 0,
    claimed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_reward_claims_user_source UNIQUE (user_id, source_type, source_id),
    CONSTRAINT fk_reward_claims_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_reward_claims_badge
        FOREIGN KEY (badge_id) REFERENCES badges(badge_id)
        ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS boss_battle_condition_progress (
    progress_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    battle_condition_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    current_streak_days INT NOT NULL DEFAULT 0,
    best_streak_days INT NOT NULL DEFAULT 0,
    progress_value DECIMAL(12,4),
    last_checked_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',
    completed_at DATETIME NULL,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_condition_progress_condition_user UNIQUE (battle_condition_id, user_id),
    CONSTRAINT fk_condition_progress_battle_condition
        FOREIGN KEY (battle_condition_id) REFERENCES boss_battle_conditions(battle_condition_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_condition_progress_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON DELETE CASCADE,
    INDEX idx_condition_progress_user_status (user_id, status),
    INDEX idx_condition_progress_condition_status (battle_condition_id, status)
) ENGINE=InnoDB;
