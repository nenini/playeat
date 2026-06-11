USE nyamnyam;

CREATE TABLE IF NOT EXISTS quests (
    quest_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    battle_id BIGINT NOT NULL,
    guild_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    quest_type VARCHAR(50) NOT NULL,
    target_value INT NOT NULL,
    current_value INT NOT NULL DEFAULT 0,
    unit VARCHAR(50),
    damage INT NOT NULL DEFAULT 0,
    reward_exp INT NOT NULL DEFAULT 0,
    reward_coin INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',
    source_type VARCHAR(30) NOT NULL DEFAULT 'PLACEHOLDER',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at DATETIME NULL,
    rewarded_at DATETIME NULL,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_quests_battle_user UNIQUE (battle_id, user_id),
    CONSTRAINT fk_quests_battle
        FOREIGN KEY (battle_id)
        REFERENCES boss_battles(battle_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_quests_guild
        FOREIGN KEY (guild_id)
        REFERENCES guilds(guild_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_quests_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,
    INDEX idx_quests_battle_user (battle_id, user_id),
    INDEX idx_quests_battle_status (battle_id, status),
    INDEX idx_quests_user_status (user_id, status),
    INDEX idx_quests_guild_battle (guild_id, battle_id)
) ENGINE=InnoDB;

SET @has_assigned_user_id = (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'quests'
      AND COLUMN_NAME = 'assigned_user_id'
);
SET @has_user_id = (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'quests'
      AND COLUMN_NAME = 'user_id'
);
SET @sql = IF(
    @has_user_id = 0 AND @has_assigned_user_id > 0,
    'ALTER TABLE quests RENAME COLUMN assigned_user_id TO user_id',
    IF(@has_user_id = 0, 'ALTER TABLE quests ADD COLUMN user_id BIGINT NULL AFTER guild_id', 'SELECT ''quests.user_id exists''')
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_guild_id = (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'quests'
      AND COLUMN_NAME = 'guild_id'
);
SET @sql = IF(@has_guild_id = 0, 'ALTER TABLE quests ADD COLUMN guild_id BIGINT NULL AFTER battle_id', 'SELECT ''quests.guild_id exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE quests q
JOIN boss_battles bb ON bb.battle_id = q.battle_id
SET q.guild_id = bb.guild_id
WHERE q.guild_id IS NULL;

SET @has_condition_type = (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'quests'
      AND COLUMN_NAME = 'condition_type'
);
SET @has_quest_type = (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'quests'
      AND COLUMN_NAME = 'quest_type'
);
SET @sql = IF(
    @has_quest_type = 0 AND @has_condition_type > 0,
    'ALTER TABLE quests RENAME COLUMN condition_type TO quest_type',
    IF(@has_quest_type = 0, 'ALTER TABLE quests ADD COLUMN quest_type VARCHAR(50) NOT NULL DEFAULT ''RECORD_DIET'' AFTER description', 'SELECT ''quests.quest_type exists''')
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_progress_value = (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'quests'
      AND COLUMN_NAME = 'progress_value'
);
SET @has_current_value = (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'quests'
      AND COLUMN_NAME = 'current_value'
);
SET @sql = IF(
    @has_current_value = 0 AND @has_progress_value > 0,
    'ALTER TABLE quests RENAME COLUMN progress_value TO current_value',
    IF(@has_current_value = 0, 'ALTER TABLE quests ADD COLUMN current_value INT NOT NULL DEFAULT 0 AFTER target_value', 'SELECT ''quests.current_value exists''')
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_reward_xp = (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'quests'
      AND COLUMN_NAME = 'reward_xp'
);
SET @has_reward_exp = (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'quests'
      AND COLUMN_NAME = 'reward_exp'
);
SET @sql = IF(
    @has_reward_exp = 0 AND @has_reward_xp > 0,
    'ALTER TABLE quests RENAME COLUMN reward_xp TO reward_exp',
    IF(@has_reward_exp = 0, 'ALTER TABLE quests ADD COLUMN reward_exp INT NOT NULL DEFAULT 0 AFTER damage', 'SELECT ''quests.reward_exp exists''')
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_damage_amount = (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'quests'
      AND COLUMN_NAME = 'damage_amount'
);
SET @has_damage = (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'quests'
      AND COLUMN_NAME = 'damage'
);
SET @sql = IF(
    @has_damage = 0 AND @has_damage_amount > 0,
    'ALTER TABLE quests RENAME COLUMN damage_amount TO damage',
    IF(@has_damage = 0, 'ALTER TABLE quests ADD COLUMN damage INT NOT NULL DEFAULT 0 AFTER unit', 'SELECT ''quests.damage exists''')
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_unit = (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'quests'
      AND COLUMN_NAME = 'unit'
);
SET @sql = IF(@has_unit = 0, 'ALTER TABLE quests ADD COLUMN unit VARCHAR(50) NULL AFTER current_value', 'SELECT ''quests.unit exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_source_type = (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'quests'
      AND COLUMN_NAME = 'source_type'
);
SET @sql = IF(@has_source_type = 0, 'ALTER TABLE quests ADD COLUMN source_type VARCHAR(30) NOT NULL DEFAULT ''PLACEHOLDER'' AFTER status', 'SELECT ''quests.source_type exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_rewarded_at = (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'quests'
      AND COLUMN_NAME = 'rewarded_at'
);
SET @sql = IF(@has_rewarded_at = 0, 'ALTER TABLE quests ADD COLUMN rewarded_at DATETIME NULL AFTER completed_at', 'SELECT ''quests.rewarded_at exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_updated_at = (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'quests'
      AND COLUMN_NAME = 'updated_at'
);
SET @sql = IF(@has_updated_at = 0, 'ALTER TABLE quests ADD COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER rewarded_at', 'SELECT ''quests.updated_at exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_uk_quests_battle_user = (
    SELECT COUNT(1) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'quests'
      AND INDEX_NAME = 'uk_quests_battle_user'
);
SET @sql = IF(@has_uk_quests_battle_user = 0, 'ALTER TABLE quests ADD UNIQUE KEY uk_quests_battle_user (battle_id, user_id)', 'SELECT ''uk_quests_battle_user exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_idx_quests_battle_status = (
    SELECT COUNT(1) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'quests'
      AND INDEX_NAME = 'idx_quests_battle_status'
);
SET @sql = IF(@has_idx_quests_battle_status = 0, 'ALTER TABLE quests ADD INDEX idx_quests_battle_status (battle_id, status)', 'SELECT ''idx_quests_battle_status exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_idx_quests_user_status = (
    SELECT COUNT(1) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'quests'
      AND INDEX_NAME = 'idx_quests_user_status'
);
SET @sql = IF(@has_idx_quests_user_status = 0, 'ALTER TABLE quests ADD INDEX idx_quests_user_status (user_id, status)', 'SELECT ''idx_quests_user_status exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_idx_quests_guild_battle = (
    SELECT COUNT(1) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'quests'
      AND INDEX_NAME = 'idx_quests_guild_battle'
);
SET @sql = IF(@has_idx_quests_guild_battle = 0, 'ALTER TABLE quests ADD INDEX idx_quests_guild_battle (guild_id, battle_id)', 'SELECT ''idx_quests_guild_battle exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
