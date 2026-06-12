ALTER TABLE foods
    ADD COLUMN nutrition_basis_amount DECIMAL(8,2) NOT NULL DEFAULT 100 AFTER category,
    ADD COLUMN nutrition_basis_unit VARCHAR(10) NOT NULL DEFAULT 'g' AFTER nutrition_basis_amount,
    ADD COLUMN serving_amount DECIMAL(8,2) AFTER nutrition_basis_unit,
    ADD COLUMN serving_unit VARCHAR(10) AFTER serving_amount,
    ADD COLUMN gram_per_piece DECIMAL(8,4) AFTER serving_unit;

UPDATE foods
SET serving_amount = serving_size_g,
    serving_unit = 'g'
WHERE serving_size_g IS NOT NULL;

ALTER TABLE foods
    DROP COLUMN serving_size_g;

ALTER TABLE diet_items
    ADD COLUMN input_amount DECIMAL(8,2) AFTER food_id,
    ADD COLUMN input_unit VARCHAR(10) AFTER input_amount,
    MODIFY COLUMN amount_g DECIMAL(8,2),
    ADD COLUMN amount_ml DECIMAL(8,2) AFTER amount_g;

UPDATE diet_items
SET input_amount = amount_g,
    input_unit = 'g'
WHERE input_amount IS NULL;

ALTER TABLE diet_items
    MODIFY COLUMN input_amount DECIMAL(8,2) NOT NULL,
    MODIFY COLUMN input_unit VARCHAR(10) NOT NULL;
