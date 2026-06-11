USE nyamnyam;

SET @has_best_streak_days = (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'characters'
      AND COLUMN_NAME = 'best_streak_days'
);
SET @sql = IF(
    @has_best_streak_days = 0,
    'ALTER TABLE characters ADD COLUMN best_streak_days INT NOT NULL DEFAULT 0 AFTER streak_days',
    'SELECT ''characters.best_streak_days already exists'''
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

ALTER TABLE characters
    ALTER COLUMN stage SET DEFAULT 'LEVEL_1';

UPDATE characters
SET stage = CASE
    WHEN level >= 30 THEN 'LEVEL_4'
    WHEN level >= 20 THEN 'LEVEL_3'
    WHEN level >= 10 THEN 'LEVEL_2'
    ELSE 'LEVEL_1'
END;

ALTER TABLE xp_histories
    MODIFY source_id BIGINT NOT NULL;

SET @has_uq_xp_source = (
    SELECT COUNT(1)
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'xp_histories'
      AND CONSTRAINT_NAME = 'uq_xp_source'
);
SET @sql = IF(
    @has_uq_xp_source = 0,
    'ALTER TABLE xp_histories ADD CONSTRAINT uq_xp_source UNIQUE (character_id, source_type, source_id)',
    'SELECT ''xp_histories.uq_xp_source already exists'''
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
