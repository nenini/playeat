USE nyamnyam;

SET @has_battle_condition_damage = (
    SELECT COUNT(1) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'boss_battle_conditions'
      AND COLUMN_NAME = 'damage'
);
SET @sql = IF(
    @has_battle_condition_damage = 0,
    'ALTER TABLE boss_battle_conditions ADD COLUMN damage INT NOT NULL DEFAULT 0 AFTER current_value',
    'SELECT ''boss_battle_conditions.damage exists'''
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
