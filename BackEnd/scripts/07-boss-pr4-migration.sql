USE nyamnyam;

CREATE TABLE IF NOT EXISTS boss_seasons (
    season_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    season_code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(1000),
    target_nutrient VARCHAR(50),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_boss_seasons_code UNIQUE (season_code),
    INDEX idx_boss_seasons_active_dates (active, start_date, end_date)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS bosses (
    boss_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    season_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    difficulty VARCHAR(20) NOT NULL,
    max_hp INT NOT NULL DEFAULT 0,
    image_url VARCHAR(500),
    reward_exp INT NOT NULL DEFAULT 0,
    reward_coin INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    starts_at DATETIME NULL,
    ends_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_bosses_season
        FOREIGN KEY (season_id)
        REFERENCES boss_seasons(season_id)
        ON DELETE CASCADE,
    CONSTRAINT uk_bosses_season_difficulty UNIQUE (season_id, difficulty),
    INDEX idx_bosses_season (season_id),
    INDEX idx_bosses_difficulty (difficulty),
    INDEX idx_bosses_status_period (status, starts_at, ends_at)
) ENGINE=InnoDB;

SET @has_bosses_max_hp = (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'bosses'
      AND COLUMN_NAME = 'max_hp'
);
SET @sql = IF(@has_bosses_max_hp = 0, 'ALTER TABLE bosses ADD COLUMN max_hp INT NOT NULL DEFAULT 0 AFTER difficulty', 'SELECT ''bosses.max_hp exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_bosses_base_hp = (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'bosses'
      AND COLUMN_NAME = 'base_hp'
);
SET @sql = IF(@has_bosses_base_hp > 0, 'UPDATE bosses SET max_hp = base_hp WHERE max_hp = 0', 'SELECT ''bosses.base_hp missing''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_bosses_reward_exp = (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'bosses'
      AND COLUMN_NAME = 'reward_exp'
);
SET @sql = IF(@has_bosses_reward_exp = 0, 'ALTER TABLE bosses ADD COLUMN reward_exp INT NOT NULL DEFAULT 0 AFTER image_url', 'SELECT ''bosses.reward_exp exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_bosses_reward_xp = (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'bosses'
      AND COLUMN_NAME = 'reward_xp'
);
SET @sql = IF(@has_bosses_reward_xp > 0, 'UPDATE bosses SET reward_exp = reward_xp WHERE reward_exp = 0', 'SELECT ''bosses.reward_xp missing''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_bosses_reward_coin = (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'bosses'
      AND COLUMN_NAME = 'reward_coin'
);
SET @sql = IF(@has_bosses_reward_coin = 0, 'ALTER TABLE bosses ADD COLUMN reward_coin INT NOT NULL DEFAULT 0 AFTER reward_exp', 'SELECT ''bosses.reward_coin exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_bosses_reward_guild_point = (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'bosses'
      AND COLUMN_NAME = 'reward_guild_point'
);
SET @sql = IF(@has_bosses_reward_guild_point > 0, 'UPDATE bosses SET reward_coin = reward_guild_point WHERE reward_coin = 0', 'SELECT ''bosses.reward_guild_point missing''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_bosses_status = (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'bosses'
      AND COLUMN_NAME = 'status'
);
SET @sql = IF(@has_bosses_status = 0, 'ALTER TABLE bosses ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT ''ACTIVE'' AFTER reward_coin', 'SELECT ''bosses.status exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_bosses_active = (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'bosses'
      AND COLUMN_NAME = 'active'
);
SET @sql = IF(@has_bosses_active > 0, 'UPDATE bosses SET status = CASE WHEN active = TRUE THEN ''ACTIVE'' ELSE ''INACTIVE'' END', 'SELECT ''bosses.active missing''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_bosses_starts_at = (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'bosses'
      AND COLUMN_NAME = 'starts_at'
);
SET @sql = IF(@has_bosses_starts_at = 0, 'ALTER TABLE bosses ADD COLUMN starts_at DATETIME NULL AFTER status', 'SELECT ''bosses.starts_at exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_bosses_ends_at = (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'bosses'
      AND COLUMN_NAME = 'ends_at'
);
SET @sql = IF(@has_bosses_ends_at = 0, 'ALTER TABLE bosses ADD COLUMN ends_at DATETIME NULL AFTER starts_at', 'SELECT ''bosses.ends_at exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_bosses_updated_at = (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'bosses'
      AND COLUMN_NAME = 'updated_at'
);
SET @sql = IF(@has_bosses_updated_at = 0, 'ALTER TABLE bosses ADD COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER created_at', 'SELECT ''bosses.updated_at exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

CREATE TABLE IF NOT EXISTS boss_common_conditions (
    condition_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    season_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    target_type VARCHAR(50) NOT NULL,
    target_value INT NOT NULL,
    unit VARCHAR(50),
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_boss_common_conditions_season
        FOREIGN KEY (season_id)
        REFERENCES boss_seasons(season_id)
        ON DELETE CASCADE,
    INDEX idx_boss_common_conditions_season (season_id),
    INDEX idx_boss_common_conditions_target_type (target_type)
) ENGINE=InnoDB;

SET @has_condition_id = (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'boss_common_conditions'
      AND COLUMN_NAME = 'condition_id'
);
SET @has_boss_condition_id = (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'boss_common_conditions'
      AND COLUMN_NAME = 'boss_condition_id'
);
SET @sql = IF(@has_condition_id = 0 AND @has_boss_condition_id > 0, 'ALTER TABLE boss_common_conditions RENAME COLUMN boss_condition_id TO condition_id', 'SELECT ''boss_common_conditions.condition_id exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_target_type = (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'boss_common_conditions'
      AND COLUMN_NAME = 'target_type'
);
SET @has_condition_type = (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'boss_common_conditions'
      AND COLUMN_NAME = 'condition_type'
);
SET @sql = IF(@has_target_type = 0 AND @has_condition_type > 0, 'ALTER TABLE boss_common_conditions RENAME COLUMN condition_type TO target_type', 'SELECT ''boss_common_conditions.target_type exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_condition_unit = (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'boss_common_conditions'
      AND COLUMN_NAME = 'unit'
);
SET @sql = IF(@has_condition_unit = 0, 'ALTER TABLE boss_common_conditions ADD COLUMN unit VARCHAR(50) NULL AFTER target_value', 'SELECT ''boss_common_conditions.unit exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_sort_order = (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'boss_common_conditions'
      AND COLUMN_NAME = 'sort_order'
);
SET @has_display_order = (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'boss_common_conditions'
      AND COLUMN_NAME = 'display_order'
);
SET @sql = IF(@has_sort_order = 0 AND @has_display_order > 0, 'ALTER TABLE boss_common_conditions RENAME COLUMN display_order TO sort_order', 'SELECT ''boss_common_conditions.sort_order exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
