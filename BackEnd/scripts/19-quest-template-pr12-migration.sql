SET NAMES utf8mb4;
USE nyamnyam;

CREATE TABLE IF NOT EXISTS quest_templates (
    template_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(500) NOT NULL,
    quest_type VARCHAR(50) NOT NULL,
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
    unit VARCHAR(30) NOT NULL,
    damage INT NOT NULL DEFAULT 100,
    reward_exp INT NOT NULL DEFAULT 30,
    reward_coin INT NOT NULL DEFAULT 10,
    difficulty VARCHAR(20) NOT NULL DEFAULT 'EASY',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_quest_templates_active (active),
    INDEX idx_quest_templates_condition (condition_category, metric_type),
    INDEX idx_quest_templates_difficulty (difficulty),
    INDEX idx_quest_templates_sort (sort_order)
) ENGINE=InnoDB;

SET @has_quest_template_id = (SELECT COUNT(1) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'quests' AND COLUMN_NAME = 'quest_template_id');
SET @sql = IF(@has_quest_template_id = 0, 'ALTER TABLE quests ADD COLUMN quest_template_id BIGINT NULL AFTER user_id', 'SELECT ''quests.quest_template_id exists''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @has_condition_category = (SELECT COUNT(1) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'quests' AND COLUMN_NAME = 'condition_category');
SET @sql = IF(@has_condition_category = 0, 'ALTER TABLE quests ADD COLUMN condition_category VARCHAR(50) NULL AFTER quest_type', 'SELECT ''quests.condition_category exists''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @has_metric_type = (SELECT COUNT(1) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'quests' AND COLUMN_NAME = 'metric_type');
SET @sql = IF(@has_metric_type = 0, 'ALTER TABLE quests ADD COLUMN metric_type VARCHAR(50) NULL AFTER condition_category', 'SELECT ''quests.metric_type exists''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @has_comparison_type = (SELECT COUNT(1) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'quests' AND COLUMN_NAME = 'comparison_type');
SET @sql = IF(@has_comparison_type = 0, 'ALTER TABLE quests ADD COLUMN comparison_type VARCHAR(50) NULL AFTER metric_type', 'SELECT ''quests.comparison_type exists''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @has_aggregation_type = (SELECT COUNT(1) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'quests' AND COLUMN_NAME = 'aggregation_type');
SET @sql = IF(@has_aggregation_type = 0, 'ALTER TABLE quests ADD COLUMN aggregation_type VARCHAR(50) NULL AFTER comparison_type', 'SELECT ''quests.aggregation_type exists''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @has_evaluation_scope = (SELECT COUNT(1) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'quests' AND COLUMN_NAME = 'evaluation_scope');
SET @sql = IF(@has_evaluation_scope = 0, 'ALTER TABLE quests ADD COLUMN evaluation_scope VARCHAR(50) NULL AFTER aggregation_type', 'SELECT ''quests.evaluation_scope exists''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @has_threshold_value = (SELECT COUNT(1) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'quests' AND COLUMN_NAME = 'threshold_value');
SET @sql = IF(@has_threshold_value = 0, 'ALTER TABLE quests ADD COLUMN threshold_value DECIMAL(10,2) NULL AFTER evaluation_scope', 'SELECT ''quests.threshold_value exists''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @has_threshold_min_value = (SELECT COUNT(1) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'quests' AND COLUMN_NAME = 'threshold_min_value');
SET @sql = IF(@has_threshold_min_value = 0, 'ALTER TABLE quests ADD COLUMN threshold_min_value DECIMAL(10,2) NULL AFTER threshold_value', 'SELECT ''quests.threshold_min_value exists''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @has_threshold_max_value = (SELECT COUNT(1) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'quests' AND COLUMN_NAME = 'threshold_max_value');
SET @sql = IF(@has_threshold_max_value = 0, 'ALTER TABLE quests ADD COLUMN threshold_max_value DECIMAL(10,2) NULL AFTER threshold_min_value', 'SELECT ''quests.threshold_max_value exists''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @has_threshold_unit = (SELECT COUNT(1) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'quests' AND COLUMN_NAME = 'threshold_unit');
SET @sql = IF(@has_threshold_unit = 0, 'ALTER TABLE quests ADD COLUMN threshold_unit VARCHAR(30) NULL AFTER threshold_max_value', 'SELECT ''quests.threshold_unit exists''');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @has_quest_template_fk = (
    SELECT COUNT(1)
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'quests'
      AND CONSTRAINT_NAME = 'fk_quests_template'
);
SET @sql = IF(
    @has_quest_template_fk = 0,
    'ALTER TABLE quests ADD CONSTRAINT fk_quests_template FOREIGN KEY (quest_template_id) REFERENCES quest_templates(template_id) ON DELETE SET NULL',
    'SELECT ''fk_quests_template exists'''
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

INSERT INTO quest_templates (
    title, description, quest_type, condition_category, metric_type, comparison_type,
    aggregation_type, evaluation_scope, threshold_value, threshold_unit, target_value,
    unit, damage, reward_exp, reward_coin, difficulty, sort_order
)
SELECT '오늘 식단 1회 이상 기록하기', '오늘 하루 식단을 1회 이상 기록하세요.', 'RECORD_DIET', 'DIET_RECORD', 'DIET_RECORD_COUNT', 'GREATER_THAN_OR_EQUAL',
       'DAILY_COUNT', 'USER_DAILY', 1, 'COUNT', 1, 'DAY', 100, 30, 10, 'EASY', 1
WHERE NOT EXISTS (SELECT 1 FROM quest_templates WHERE quest_type = 'RECORD_DIET' AND metric_type = 'DIET_RECORD_COUNT');

INSERT INTO quest_templates (
    title, description, quest_type, condition_category, metric_type, comparison_type,
    aggregation_type, evaluation_scope, threshold_value, threshold_unit, target_value,
    unit, damage, reward_exp, reward_coin, difficulty, sort_order
)
SELECT '오늘 당류 50g 이하 유지하기', '오늘 당류 섭취량을 50g 이하로 유지하세요.', 'REDUCE_SUGAR', 'NUTRITION', 'SUGAR', 'LESS_THAN_OR_EQUAL',
       'DAILY_VALUE', 'USER_DAILY', 50, 'GRAM', 1, 'DAY', 120, 40, 15, 'NORMAL', 2
WHERE NOT EXISTS (SELECT 1 FROM quest_templates WHERE quest_type = 'REDUCE_SUGAR' AND metric_type = 'SUGAR');

INSERT INTO quest_templates (
    title, description, quest_type, condition_category, metric_type, comparison_type,
    aggregation_type, evaluation_scope, threshold_value, threshold_unit, target_value,
    unit, damage, reward_exp, reward_coin, difficulty, sort_order
)
SELECT '오늘 단백질 60g 이상 섭취하기', '오늘 단백질을 60g 이상 섭취하세요.', 'ACHIEVE_PROTEIN_GOAL', 'NUTRITION', 'PROTEIN', 'GREATER_THAN_OR_EQUAL',
       'DAILY_VALUE', 'USER_DAILY', 60, 'GRAM', 1, 'DAY', 120, 40, 15, 'NORMAL', 3
WHERE NOT EXISTS (SELECT 1 FROM quest_templates WHERE quest_type = 'ACHIEVE_PROTEIN_GOAL' AND metric_type = 'PROTEIN');

INSERT INTO quest_templates (
    title, description, quest_type, condition_category, metric_type, comparison_type,
    aggregation_type, evaluation_scope, threshold_value, threshold_unit, target_value,
    unit, damage, reward_exp, reward_coin, difficulty, sort_order
)
SELECT '오늘 나트륨 2000mg 이하 유지하기', '오늘 나트륨 섭취량을 2000mg 이하로 유지하세요.', 'REDUCE_SODIUM', 'NUTRITION', 'SODIUM', 'LESS_THAN_OR_EQUAL',
       'DAILY_VALUE', 'USER_DAILY', 2000, 'MG', 1, 'DAY', 120, 40, 15, 'NORMAL', 4
WHERE NOT EXISTS (SELECT 1 FROM quest_templates WHERE quest_type = 'REDUCE_SODIUM' AND metric_type = 'SODIUM');

INSERT INTO quest_templates (
    title, description, quest_type, condition_category, metric_type, comparison_type,
    aggregation_type, evaluation_scope, threshold_value, threshold_unit, target_value,
    unit, damage, reward_exp, reward_coin, difficulty, sort_order
)
SELECT '오늘 아침 식사 기록하기', '오늘 아침 식사를 기록하세요.', 'RECORD_BREAKFAST', 'MEAL_PATTERN', 'BREAKFAST', 'EXISTS',
       'DAILY_COUNT', 'USER_DAILY', 1, 'COUNT', 1, 'DAY', 100, 30, 10, 'EASY', 5
WHERE NOT EXISTS (SELECT 1 FROM quest_templates WHERE quest_type = 'RECORD_BREAKFAST' AND metric_type = 'BREAKFAST');
