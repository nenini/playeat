SET NAMES utf8mb4;
USE nyamnyam;

SET @schema_name = DATABASE();

SET @table_name = 'boss_common_conditions';
SET @column_name = 'condition_category';
SET @sql = IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = @table_name AND COLUMN_NAME = @column_name),
    'ALTER TABLE boss_common_conditions ADD COLUMN condition_category VARCHAR(50) NULL AFTER target_type',
    'SELECT ''boss_common_conditions.condition_category exists'''
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @column_name = 'metric_type';
SET @sql = IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = @table_name AND COLUMN_NAME = @column_name),
    'ALTER TABLE boss_common_conditions ADD COLUMN metric_type VARCHAR(50) NULL AFTER condition_category',
    'SELECT ''boss_common_conditions.metric_type exists'''
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @column_name = 'comparison_type';
SET @sql = IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = @table_name AND COLUMN_NAME = @column_name),
    'ALTER TABLE boss_common_conditions ADD COLUMN comparison_type VARCHAR(50) NULL AFTER metric_type',
    'SELECT ''boss_common_conditions.comparison_type exists'''
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @column_name = 'aggregation_type';
SET @sql = IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = @table_name AND COLUMN_NAME = @column_name),
    'ALTER TABLE boss_common_conditions ADD COLUMN aggregation_type VARCHAR(50) NULL AFTER comparison_type',
    'SELECT ''boss_common_conditions.aggregation_type exists'''
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @column_name = 'evaluation_scope';
SET @sql = IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = @table_name AND COLUMN_NAME = @column_name),
    'ALTER TABLE boss_common_conditions ADD COLUMN evaluation_scope VARCHAR(50) NULL AFTER aggregation_type',
    'SELECT ''boss_common_conditions.evaluation_scope exists'''
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @column_name = 'threshold_min_value';
SET @sql = IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = @table_name AND COLUMN_NAME = @column_name),
    'ALTER TABLE boss_common_conditions ADD COLUMN threshold_min_value DECIMAL(10,2) NULL AFTER threshold_value',
    'SELECT ''boss_common_conditions.threshold_min_value exists'''
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @column_name = 'threshold_max_value';
SET @sql = IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = @table_name AND COLUMN_NAME = @column_name),
    'ALTER TABLE boss_common_conditions ADD COLUMN threshold_max_value DECIMAL(10,2) NULL AFTER threshold_min_value',
    'SELECT ''boss_common_conditions.threshold_max_value exists'''
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @column_name = 'required_for_clear';
SET @sql = IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = @table_name AND COLUMN_NAME = @column_name),
    'ALTER TABLE boss_common_conditions ADD COLUMN required_for_clear BOOLEAN NOT NULL DEFAULT TRUE AFTER required_days',
    'SELECT ''boss_common_conditions.required_for_clear exists'''
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @column_name = 'verification_supported';
SET @sql = IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = @table_name AND COLUMN_NAME = @column_name),
    'ALTER TABLE boss_common_conditions ADD COLUMN verification_supported BOOLEAN NOT NULL DEFAULT TRUE AFTER required_for_clear',
    'SELECT ''boss_common_conditions.verification_supported exists'''
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @table_name = 'boss_battle_conditions';
SET @column_name = 'condition_category';
SET @sql = IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = @table_name AND COLUMN_NAME = @column_name),
    'ALTER TABLE boss_battle_conditions ADD COLUMN condition_category VARCHAR(50) NULL AFTER target_type',
    'SELECT ''boss_battle_conditions.condition_category exists'''
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @column_name = 'metric_type';
SET @sql = IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = @table_name AND COLUMN_NAME = @column_name),
    'ALTER TABLE boss_battle_conditions ADD COLUMN metric_type VARCHAR(50) NULL AFTER condition_category',
    'SELECT ''boss_battle_conditions.metric_type exists'''
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @column_name = 'comparison_type';
SET @sql = IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = @table_name AND COLUMN_NAME = @column_name),
    'ALTER TABLE boss_battle_conditions ADD COLUMN comparison_type VARCHAR(50) NULL AFTER metric_type',
    'SELECT ''boss_battle_conditions.comparison_type exists'''
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @column_name = 'aggregation_type';
SET @sql = IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = @table_name AND COLUMN_NAME = @column_name),
    'ALTER TABLE boss_battle_conditions ADD COLUMN aggregation_type VARCHAR(50) NULL AFTER comparison_type',
    'SELECT ''boss_battle_conditions.aggregation_type exists'''
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @column_name = 'evaluation_scope';
SET @sql = IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = @table_name AND COLUMN_NAME = @column_name),
    'ALTER TABLE boss_battle_conditions ADD COLUMN evaluation_scope VARCHAR(50) NULL AFTER aggregation_type',
    'SELECT ''boss_battle_conditions.evaluation_scope exists'''
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @column_name = 'threshold_min_value';
SET @sql = IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = @table_name AND COLUMN_NAME = @column_name),
    'ALTER TABLE boss_battle_conditions ADD COLUMN threshold_min_value DECIMAL(10,2) NULL AFTER threshold_value',
    'SELECT ''boss_battle_conditions.threshold_min_value exists'''
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @column_name = 'threshold_max_value';
SET @sql = IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = @table_name AND COLUMN_NAME = @column_name),
    'ALTER TABLE boss_battle_conditions ADD COLUMN threshold_max_value DECIMAL(10,2) NULL AFTER threshold_min_value',
    'SELECT ''boss_battle_conditions.threshold_max_value exists'''
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @column_name = 'required_for_clear';
SET @sql = IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = @table_name AND COLUMN_NAME = @column_name),
    'ALTER TABLE boss_battle_conditions ADD COLUMN required_for_clear BOOLEAN NOT NULL DEFAULT TRUE AFTER completed',
    'SELECT ''boss_battle_conditions.required_for_clear exists'''
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @column_name = 'verification_supported';
SET @sql = IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = @table_name AND COLUMN_NAME = @column_name),
    'ALTER TABLE boss_battle_conditions ADD COLUMN verification_supported BOOLEAN NOT NULL DEFAULT TRUE AFTER required_for_clear',
    'SELECT ''boss_battle_conditions.verification_supported exists'''
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

UPDATE boss_common_conditions
SET condition_category = 'NUTRITION',
    metric_type = 'SUGAR',
    comparison_type = 'LESS_THAN_OR_EQUAL',
    aggregation_type = 'DAYS_SATISFIED',
    evaluation_scope = 'GUILD_BATTLE_PERIOD',
    required_for_clear = TRUE,
    verification_supported = TRUE
WHERE target_type = 'SUGAR_UNDER_LIMIT';

UPDATE boss_battle_conditions
SET condition_category = 'NUTRITION',
    metric_type = 'SUGAR',
    comparison_type = 'LESS_THAN_OR_EQUAL',
    aggregation_type = 'DAYS_SATISFIED',
    evaluation_scope = 'GUILD_BATTLE_PERIOD',
    required_for_clear = TRUE,
    verification_supported = TRUE
WHERE target_type = 'SUGAR_UNDER_LIMIT';

UPDATE boss_common_conditions
SET required_for_clear = FALSE,
    verification_supported = FALSE
WHERE target_type IN ('PROCESSED_DRINK_ZERO', 'VEGETABLE_VARIETY');

UPDATE boss_battle_conditions
SET required_for_clear = FALSE,
    verification_supported = FALSE
WHERE target_type IN ('PROCESSED_DRINK_ZERO', 'VEGETABLE_VARIETY');

CREATE TABLE IF NOT EXISTS boss_condition_templates (
    condition_template_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(500) NOT NULL,
    target_type VARCHAR(50) NOT NULL,
    condition_category VARCHAR(50) NOT NULL,
    metric_type VARCHAR(50) NOT NULL,
    comparison_type VARCHAR(50) NOT NULL,
    aggregation_type VARCHAR(50) NOT NULL,
    evaluation_scope VARCHAR(50) NOT NULL,
    threshold_value DECIMAL(10,2) NULL,
    threshold_min_value DECIMAL(10,2) NULL,
    threshold_max_value DECIMAL(10,2) NULL,
    threshold_unit VARCHAR(30) NULL,
    target_value INT NOT NULL DEFAULT 1,
    required_days INT NULL,
    unit VARCHAR(30) NOT NULL,
    difficulty VARCHAR(20) NOT NULL DEFAULT 'EASY',
    required_for_clear BOOLEAN NOT NULL DEFAULT TRUE,
    verification_supported BOOLEAN NOT NULL DEFAULT TRUE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_boss_condition_templates_active (active),
    INDEX idx_boss_condition_templates_target_type (target_type),
    INDEX idx_boss_condition_templates_condition (condition_category, metric_type),
    INDEX idx_boss_condition_templates_difficulty (difficulty),
    INDEX idx_boss_condition_templates_sort (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET @table_name = 'boss_common_conditions';
SET @column_name = 'condition_template_id';
SET @sql = IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = @table_name AND COLUMN_NAME = @column_name),
    'ALTER TABLE boss_common_conditions ADD COLUMN condition_template_id BIGINT NULL AFTER condition_id',
    'SELECT ''boss_common_conditions.condition_template_id exists'''
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @table_name = 'boss_battle_conditions';
SET @column_name = 'condition_template_id';
SET @sql = IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @schema_name AND TABLE_NAME = @table_name AND COLUMN_NAME = @column_name),
    'ALTER TABLE boss_battle_conditions ADD COLUMN condition_template_id BIGINT NULL AFTER condition_id',
    'SELECT ''boss_battle_conditions.condition_template_id exists'''
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @has_fk_boss_common_conditions_template = (
    SELECT COUNT(1)
    FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
    WHERE TABLE_SCHEMA = @schema_name
      AND TABLE_NAME = 'boss_common_conditions'
      AND CONSTRAINT_NAME = 'fk_boss_common_conditions_template'
);
SET @sql = IF(
    @has_fk_boss_common_conditions_template = 0,
    'ALTER TABLE boss_common_conditions ADD CONSTRAINT fk_boss_common_conditions_template FOREIGN KEY (condition_template_id) REFERENCES boss_condition_templates(condition_template_id) ON DELETE SET NULL',
    'SELECT ''fk_boss_common_conditions_template exists'''
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @has_fk_battle_conditions_template = (
    SELECT COUNT(1)
    FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
    WHERE TABLE_SCHEMA = @schema_name
      AND TABLE_NAME = 'boss_battle_conditions'
      AND CONSTRAINT_NAME = 'fk_battle_conditions_template'
);
SET @sql = IF(
    @has_fk_battle_conditions_template = 0,
    'ALTER TABLE boss_battle_conditions ADD CONSTRAINT fk_battle_conditions_template FOREIGN KEY (condition_template_id) REFERENCES boss_condition_templates(condition_template_id) ON DELETE SET NULL',
    'SELECT ''fk_battle_conditions_template exists'''
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

INSERT INTO boss_condition_templates (
    title,
    description,
    target_type,
    condition_category,
    metric_type,
    comparison_type,
    aggregation_type,
    evaluation_scope,
    threshold_value,
    threshold_unit,
    target_value,
    required_days,
    unit,
    difficulty,
    required_for_clear,
    verification_supported,
    sort_order
)
SELECT
    '당류 50g 이하 유지',
    '하루 당류 섭취량을 50g 이하로 유지합니다.',
    'SUGAR_UNDER_LIMIT',
    'NUTRITION',
    'SUGAR',
    'LESS_THAN_OR_EQUAL',
    'DAYS_SATISFIED',
    'GUILD_BATTLE_PERIOD',
    50,
    'GRAM',
    3,
    3,
    'DAY',
    'EASY',
    TRUE,
    TRUE,
    1
WHERE NOT EXISTS (
    SELECT 1 FROM boss_condition_templates WHERE target_type = 'SUGAR_UNDER_LIMIT'
);

INSERT INTO boss_condition_templates (
    title,
    description,
    target_type,
    condition_category,
    metric_type,
    comparison_type,
    aggregation_type,
    evaluation_scope,
    threshold_value,
    threshold_unit,
    target_value,
    required_days,
    unit,
    difficulty,
    required_for_clear,
    verification_supported,
    sort_order
)
SELECT
    '단백질 60g 이상 섭취',
    '하루 단백질 섭취량을 60g 이상 달성합니다.',
    'PROTEIN_OVER_TARGET',
    'NUTRITION',
    'PROTEIN',
    'GREATER_THAN_OR_EQUAL',
    'DAYS_SATISFIED',
    'GUILD_BATTLE_PERIOD',
    60,
    'GRAM',
    3,
    3,
    'DAY',
    'NORMAL',
    TRUE,
    TRUE,
    2
WHERE NOT EXISTS (
    SELECT 1 FROM boss_condition_templates WHERE target_type = 'PROTEIN_OVER_TARGET'
);

INSERT INTO boss_condition_templates (
    title,
    description,
    target_type,
    condition_category,
    metric_type,
    comparison_type,
    aggregation_type,
    evaluation_scope,
    threshold_value,
    threshold_unit,
    target_value,
    required_days,
    unit,
    difficulty,
    required_for_clear,
    verification_supported,
    sort_order
)
SELECT
    '나트륨 2000mg 이하 유지',
    '하루 나트륨 섭취량을 2000mg 이하로 유지합니다.',
    'SODIUM_UNDER_LIMIT',
    'NUTRITION',
    'SODIUM',
    'LESS_THAN_OR_EQUAL',
    'DAYS_SATISFIED',
    'GUILD_BATTLE_PERIOD',
    2000,
    'MG',
    3,
    3,
    'DAY',
    'NORMAL',
    TRUE,
    TRUE,
    3
WHERE NOT EXISTS (
    SELECT 1 FROM boss_condition_templates WHERE target_type = 'SODIUM_UNDER_LIMIT'
);

SET @sugar_under_limit_template_id = (
    SELECT condition_template_id
    FROM boss_condition_templates
    WHERE target_type = 'SUGAR_UNDER_LIMIT'
    ORDER BY condition_template_id
    LIMIT 1
);

UPDATE boss_common_conditions
SET condition_template_id = @sugar_under_limit_template_id,
    condition_category = 'NUTRITION',
    metric_type = 'SUGAR',
    comparison_type = 'LESS_THAN_OR_EQUAL',
    aggregation_type = 'DAYS_SATISFIED',
    evaluation_scope = 'GUILD_BATTLE_PERIOD',
    threshold_value = COALESCE(threshold_value, 50),
    threshold_unit = 'GRAM',
    required_for_clear = TRUE,
    verification_supported = TRUE
WHERE target_type = 'SUGAR_UNDER_LIMIT';

UPDATE boss_battle_conditions
SET condition_template_id = @sugar_under_limit_template_id,
    condition_category = 'NUTRITION',
    metric_type = 'SUGAR',
    comparison_type = 'LESS_THAN_OR_EQUAL',
    aggregation_type = 'DAYS_SATISFIED',
    evaluation_scope = 'GUILD_BATTLE_PERIOD',
    threshold_value = COALESCE(threshold_value, 50),
    threshold_unit = 'GRAM',
    required_for_clear = TRUE,
    verification_supported = TRUE
WHERE target_type = 'SUGAR_UNDER_LIMIT';

UPDATE boss_common_conditions
SET required_for_clear = FALSE,
    verification_supported = FALSE
WHERE target_type IN ('PROCESSED_DRINK_ZERO', 'VEGETABLE_VARIETY');

UPDATE boss_battle_conditions
SET required_for_clear = FALSE,
    verification_supported = FALSE
WHERE target_type IN ('PROCESSED_DRINK_ZERO', 'VEGETABLE_VARIETY');

INSERT INTO boss_seasons (
    season_code,
    name,
    description,
    target_nutrient,
    start_date,
    end_date,
    active
) VALUES (
    'SUGAR-DRAGON-TEST-2026-06',
    '2026년 6월 당분 드래곤 테스트 시즌',
    'Swagger 공통 조건 검증 테스트용 시즌',
    'SUGAR',
    '2026-06-10',
    '2026-06-16',
    TRUE
)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    target_nutrient = VALUES(target_nutrient),
    start_date = VALUES(start_date),
    end_date = VALUES(end_date),
    active = VALUES(active);

SET @sugar_test_season_id = (
    SELECT season_id
    FROM boss_seasons
    WHERE season_code = 'SUGAR-DRAGON-TEST-2026-06'
);

INSERT INTO bosses (
    season_id,
    name,
    description,
    difficulty,
    max_hp,
    image_url,
    reward_exp,
    reward_coin,
    status,
    starts_at,
    ends_at
) VALUES
    (@sugar_test_season_id, '당분 드래곤_테스트', 'Swagger 공통 조건 검증 테스트용 보스입니다.', 'EASY', 500, '/images/boss/sugar-dragon.png', 800, 100, 'ACTIVE', '2026-06-10 00:00:00', '2026-06-16 23:59:59'),
    (@sugar_test_season_id, '당분 드래곤_테스트', 'Swagger 공통 조건 검증 테스트용 보스입니다.', 'NORMAL', 1000, '/images/boss/sugar-dragon.png', 1200, 150, 'ACTIVE', '2026-06-10 00:00:00', '2026-06-16 23:59:59'),
    (@sugar_test_season_id, '당분 드래곤_테스트', 'Swagger 공통 조건 검증 테스트용 보스입니다.', 'HARD', 1800, '/images/boss/sugar-dragon.png', 2400, 300, 'ACTIVE', '2026-06-10 00:00:00', '2026-06-16 23:59:59')
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    max_hp = VALUES(max_hp),
    image_url = VALUES(image_url),
    reward_exp = VALUES(reward_exp),
    reward_coin = VALUES(reward_coin),
    status = VALUES(status),
    starts_at = VALUES(starts_at),
    ends_at = VALUES(ends_at);

INSERT INTO boss_common_conditions (
    condition_template_id,
    season_id,
    boss_id,
    title,
    description,
    target_type,
    condition_category,
    metric_type,
    comparison_type,
    aggregation_type,
    evaluation_scope,
    threshold_value,
    threshold_unit,
    target_value,
    required_days,
    required_for_clear,
    verification_supported,
    unit,
    sort_order
)
SELECT
    @sugar_under_limit_template_id,
    @sugar_test_season_id,
    b.boss_id,
    '당류 50g 이하 공동 달성',
    '보스전 참여자 중 1명이 하루 당류 섭취량 50g 이하를 달성하면 완료됩니다.',
    'SUGAR_UNDER_LIMIT',
    'NUTRITION',
    'SUGAR',
    'LESS_THAN_OR_EQUAL',
    'DAYS_SATISFIED',
    'GUILD_BATTLE_PERIOD',
    50,
    'GRAM',
    1,
    1,
    TRUE,
    TRUE,
    'DAY',
    1
FROM bosses b
WHERE b.season_id = @sugar_test_season_id
  AND b.name = '당분 드래곤_테스트'
  AND NOT EXISTS (
      SELECT 1
      FROM boss_common_conditions bcc
      WHERE bcc.boss_id = b.boss_id
        AND bcc.target_type = 'SUGAR_UNDER_LIMIT'
  );
