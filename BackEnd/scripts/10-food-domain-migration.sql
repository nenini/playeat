SET @has_foods_nutrition_basis_amount = (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'foods'
      AND COLUMN_NAME = 'nutrition_basis_amount'
);
SET @sql = IF(@has_foods_nutrition_basis_amount = 0, 'ALTER TABLE foods ADD COLUMN nutrition_basis_amount DECIMAL(8,2) NOT NULL DEFAULT 100 AFTER category', 'SELECT ''foods.nutrition_basis_amount exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_foods_nutrition_basis_unit = (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'foods'
      AND COLUMN_NAME = 'nutrition_basis_unit'
);
SET @sql = IF(@has_foods_nutrition_basis_unit = 0, 'ALTER TABLE foods ADD COLUMN nutrition_basis_unit VARCHAR(10) NOT NULL DEFAULT ''g'' AFTER nutrition_basis_amount', 'SELECT ''foods.nutrition_basis_unit exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_foods_serving_amount = (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'foods'
      AND COLUMN_NAME = 'serving_amount'
);
SET @sql = IF(@has_foods_serving_amount = 0, 'ALTER TABLE foods ADD COLUMN serving_amount DECIMAL(8,2) AFTER nutrition_basis_unit', 'SELECT ''foods.serving_amount exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_foods_serving_unit = (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'foods'
      AND COLUMN_NAME = 'serving_unit'
);
SET @sql = IF(@has_foods_serving_unit = 0, 'ALTER TABLE foods ADD COLUMN serving_unit VARCHAR(10) AFTER serving_amount', 'SELECT ''foods.serving_unit exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_foods_gram_per_piece = (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'foods'
      AND COLUMN_NAME = 'gram_per_piece'
);
SET @sql = IF(@has_foods_gram_per_piece = 0, 'ALTER TABLE foods ADD COLUMN gram_per_piece DECIMAL(8,4) AFTER serving_unit', 'SELECT ''foods.gram_per_piece exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_foods_serving_size_g = (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'foods'
      AND COLUMN_NAME = 'serving_size_g'
);
SET @sql = IF(@has_foods_serving_size_g > 0, 'UPDATE foods SET serving_amount = serving_size_g, serving_unit = ''g'' WHERE serving_size_g IS NOT NULL AND serving_amount IS NULL', 'SELECT ''foods.serving_size_g missing''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = IF(@has_foods_serving_size_g > 0, 'ALTER TABLE foods DROP COLUMN serving_size_g', 'SELECT ''foods.serving_size_g already dropped''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_diet_items_input_amount = (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'diet_items'
      AND COLUMN_NAME = 'input_amount'
);
SET @sql = IF(@has_diet_items_input_amount = 0, 'ALTER TABLE diet_items ADD COLUMN input_amount DECIMAL(8,2) AFTER food_id', 'SELECT ''diet_items.input_amount exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_diet_items_input_unit = (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'diet_items'
      AND COLUMN_NAME = 'input_unit'
);
SET @sql = IF(@has_diet_items_input_unit = 0, 'ALTER TABLE diet_items ADD COLUMN input_unit VARCHAR(10) AFTER input_amount', 'SELECT ''diet_items.input_unit exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

ALTER TABLE diet_items
    MODIFY COLUMN amount_g DECIMAL(8,2);

SET @has_diet_items_amount_ml = (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'diet_items'
      AND COLUMN_NAME = 'amount_ml'
);
SET @sql = IF(@has_diet_items_amount_ml = 0, 'ALTER TABLE diet_items ADD COLUMN amount_ml DECIMAL(8,2) AFTER amount_g', 'SELECT ''diet_items.amount_ml exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE diet_items
SET input_amount = amount_g,
    input_unit = 'g'
WHERE input_amount IS NULL;

ALTER TABLE diet_items
    MODIFY COLUMN input_amount DECIMAL(8,2) NOT NULL,
    MODIFY COLUMN input_unit VARCHAR(10) NOT NULL;
