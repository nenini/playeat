USE nyamnyam;

CREATE TABLE IF NOT EXISTS boss_battles (
    battle_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    guild_id BIGINT NOT NULL,
    boss_id BIGINT NOT NULL,
    season_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',
    max_hp INT NOT NULL,
    current_hp INT NOT NULL,
    total_damage INT NOT NULL DEFAULT 0,
    started_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ended_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_boss_battles_guild
        FOREIGN KEY (guild_id)
        REFERENCES guilds(guild_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_boss_battles_boss
        FOREIGN KEY (boss_id)
        REFERENCES bosses(boss_id)
        ON DELETE RESTRICT,
    INDEX idx_boss_battles_guild_status (guild_id, status),
    INDEX idx_boss_battles_guild_season (guild_id, season_id),
    INDEX idx_boss_battles_boss_id (boss_id)
) ENGINE=InnoDB;

SET @has_battle_season_id = (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'boss_battles'
      AND COLUMN_NAME = 'season_id'
);
SET @sql = IF(@has_battle_season_id = 0, 'ALTER TABLE boss_battles ADD COLUMN season_id BIGINT NULL AFTER boss_id', 'SELECT ''boss_battles.season_id exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE boss_battles bb
JOIN bosses b ON b.boss_id = bb.boss_id
SET bb.season_id = b.season_id
WHERE bb.season_id IS NULL;

SET @has_battle_total_damage = (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'boss_battles'
      AND COLUMN_NAME = 'total_damage'
);
SET @sql = IF(@has_battle_total_damage = 0, 'ALTER TABLE boss_battles ADD COLUMN total_damage INT NOT NULL DEFAULT 0 AFTER current_hp', 'SELECT ''boss_battles.total_damage exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_battle_created_at = (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'boss_battles'
      AND COLUMN_NAME = 'created_at'
);
SET @sql = IF(@has_battle_created_at = 0, 'ALTER TABLE boss_battles ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER ended_at', 'SELECT ''boss_battles.created_at exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_battle_updated_at = (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'boss_battles'
      AND COLUMN_NAME = 'updated_at'
);
SET @sql = IF(@has_battle_updated_at = 0, 'ALTER TABLE boss_battles ADD COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER created_at', 'SELECT ''boss_battles.updated_at exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

CREATE TABLE IF NOT EXISTS boss_battle_conditions (
    battle_condition_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    battle_id BIGINT NOT NULL,
    condition_id BIGINT NULL,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    target_type VARCHAR(50) NOT NULL,
    threshold_value DECIMAL(10,2) NULL,
    threshold_unit VARCHAR(50) NULL,
    target_value INT NOT NULL,
    required_days INT NULL,
    current_value INT NOT NULL DEFAULT 0,
    unit VARCHAR(50),
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    completed_at DATETIME NULL,
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_battle_conditions_battle
        FOREIGN KEY (battle_id)
        REFERENCES boss_battles(battle_id)
        ON DELETE CASCADE,
    INDEX idx_battle_conditions_battle (battle_id),
    INDEX idx_battle_conditions_completed (battle_id, completed)
) ENGINE=InnoDB;

SET @has_battle_condition_id = (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'boss_battle_conditions'
      AND COLUMN_NAME = 'condition_id'
);
SET @has_old_boss_condition_id = (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'boss_battle_conditions'
      AND COLUMN_NAME = 'boss_condition_id'
);
SET @sql = IF(
    @has_battle_condition_id = 0 AND @has_old_boss_condition_id > 0,
    'ALTER TABLE boss_battle_conditions RENAME COLUMN boss_condition_id TO condition_id',
    IF(@has_battle_condition_id = 0, 'ALTER TABLE boss_battle_conditions ADD COLUMN condition_id BIGINT NULL AFTER battle_id', 'SELECT ''boss_battle_conditions.condition_id exists''')
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_battle_condition_title = (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'boss_battle_conditions'
      AND COLUMN_NAME = 'title'
);
SET @sql = IF(@has_battle_condition_title = 0, 'ALTER TABLE boss_battle_conditions ADD COLUMN title VARCHAR(200) NOT NULL DEFAULT '''' AFTER condition_id', 'SELECT ''boss_battle_conditions.title exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_battle_condition_target_type = (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'boss_battle_conditions'
      AND COLUMN_NAME = 'target_type'
);
SET @sql = IF(@has_battle_condition_target_type = 0, 'ALTER TABLE boss_battle_conditions ADD COLUMN target_type VARCHAR(50) NOT NULL DEFAULT ''DIET_RECORD_MEMBER_COUNT'' AFTER description', 'SELECT ''boss_battle_conditions.target_type exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_battle_condition_threshold_value = (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'boss_battle_conditions'
      AND COLUMN_NAME = 'threshold_value'
);
SET @sql = IF(@has_battle_condition_threshold_value = 0, 'ALTER TABLE boss_battle_conditions ADD COLUMN threshold_value DECIMAL(10,2) NULL AFTER target_type', 'SELECT ''boss_battle_conditions.threshold_value exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_battle_condition_threshold_unit = (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'boss_battle_conditions'
      AND COLUMN_NAME = 'threshold_unit'
);
SET @sql = IF(@has_battle_condition_threshold_unit = 0, 'ALTER TABLE boss_battle_conditions ADD COLUMN threshold_unit VARCHAR(50) NULL AFTER threshold_value', 'SELECT ''boss_battle_conditions.threshold_unit exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_battle_condition_description = (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'boss_battle_conditions'
      AND COLUMN_NAME = 'description'
);
SET @sql = IF(@has_battle_condition_description = 0, 'ALTER TABLE boss_battle_conditions ADD COLUMN description VARCHAR(500) NULL AFTER title', 'SELECT ''boss_battle_conditions.description exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_battle_condition_target_value = (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'boss_battle_conditions'
      AND COLUMN_NAME = 'target_value'
);
SET @sql = IF(@has_battle_condition_target_value = 0, 'ALTER TABLE boss_battle_conditions ADD COLUMN target_value INT NOT NULL DEFAULT 0 AFTER target_type', 'SELECT ''boss_battle_conditions.target_value exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_battle_condition_required_days = (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'boss_battle_conditions'
      AND COLUMN_NAME = 'required_days'
);
SET @sql = IF(@has_battle_condition_required_days = 0, 'ALTER TABLE boss_battle_conditions ADD COLUMN required_days INT NULL AFTER target_value', 'SELECT ''boss_battle_conditions.required_days exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_battle_condition_current_value = (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'boss_battle_conditions'
      AND COLUMN_NAME = 'current_value'
);
SET @sql = IF(@has_battle_condition_current_value = 0, 'ALTER TABLE boss_battle_conditions ADD COLUMN current_value INT NOT NULL DEFAULT 0 AFTER target_value', 'SELECT ''boss_battle_conditions.current_value exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_battle_condition_unit = (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'boss_battle_conditions'
      AND COLUMN_NAME = 'unit'
);
SET @sql = IF(@has_battle_condition_unit = 0, 'ALTER TABLE boss_battle_conditions ADD COLUMN unit VARCHAR(50) NULL AFTER current_value', 'SELECT ''boss_battle_conditions.unit exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_battle_condition_completed = (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'boss_battle_conditions'
      AND COLUMN_NAME = 'completed'
);
SET @sql = IF(@has_battle_condition_completed = 0, 'ALTER TABLE boss_battle_conditions ADD COLUMN completed BOOLEAN NOT NULL DEFAULT FALSE AFTER current_value', 'SELECT ''boss_battle_conditions.completed exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_battle_condition_completed_at = (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'boss_battle_conditions'
      AND COLUMN_NAME = 'completed_at'
);
SET @sql = IF(@has_battle_condition_completed_at = 0, 'ALTER TABLE boss_battle_conditions ADD COLUMN completed_at DATETIME NULL AFTER completed', 'SELECT ''boss_battle_conditions.completed_at exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_battle_condition_sort_order = (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'boss_battle_conditions'
      AND COLUMN_NAME = 'sort_order'
);
SET @sql = IF(@has_battle_condition_sort_order = 0, 'ALTER TABLE boss_battle_conditions ADD COLUMN sort_order INT NOT NULL DEFAULT 0 AFTER completed_at', 'SELECT ''boss_battle_conditions.sort_order exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_battle_condition_updated_at = (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'boss_battle_conditions'
      AND COLUMN_NAME = 'updated_at'
);
SET @sql = IF(@has_battle_condition_updated_at = 0, 'ALTER TABLE boss_battle_conditions ADD COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER created_at', 'SELECT ''boss_battle_conditions.updated_at exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

CREATE TABLE IF NOT EXISTS boss_battle_damage_logs (
    damage_log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    battle_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    damage INT NOT NULL,
    source_type VARCHAR(50) NOT NULL,
    source_id BIGINT NULL,
    description VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_damage_logs_battle
        FOREIGN KEY (battle_id)
        REFERENCES boss_battles(battle_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_damage_logs_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,
    INDEX idx_damage_logs_battle_created (battle_id, created_at),
    INDEX idx_damage_logs_battle_user (battle_id, user_id)
) ENGINE=InnoDB;
