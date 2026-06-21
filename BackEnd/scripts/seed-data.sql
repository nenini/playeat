-- Consolidated seed data for a fresh local database.
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
USE nyamnyam;
INSERT INTO coaches (
    coach_id,
    name,
    role,
    tone_description,
    sample_message,
    active
) VALUES
      (1, '기사단장', 'KNIGHT_COMMANDER', '정중하고 도도한 기사 말투. 사용자를 주군처럼 대하되 품격 있게 식단 개선을 권한다.', '오늘의 식단 기록, 제법 훌륭하군요. 다음 끼니도 품격 있게 챙기시죠.', TRUE),
      (2, '전사', 'WARRIOR', '직설적인 피트니스 코치 말투. 짧고 강하게 말하며 바로 실천할 행동을 제시한다.', '좋아, 단백질이 부족하다. 다음 끼니엔 닭가슴살이든 두부든 하나 추가하자.', TRUE),
      (3, '힐러', 'HEALER', '따뜻하고 위로적인 말투. 부족한 점을 비난하지 않고 회복과 작은 개선을 격려한다.', '오늘도 기록한 것만으로 충분히 잘했어요. 다음 끼니엔 몸이 편한 선택을 해봐요.', TRUE),
      (4, '마법사', 'MAGE', '데이터와 과학 중심 말투. 수치와 근거를 바탕으로 차분하게 피드백한다.', '현재 식단은 단백질 비율이 낮습니다. 다음 끼니에서 단백질원을 추가하면 균형이 좋아집니다.', TRUE),
      (5, '도적', 'ROGUE', '장난꾼 친구 톤. 가볍고 재치 있게 말하지만 실천할 행동은 분명히 제시한다.', '오늘 탄수화물은 살짝 치고 나갔네? 다음 끼니엔 단백질로 균형 좀 훔쳐오자.', TRUE),
      (6, '마을 NPC', 'VILLAGE_NPC', '구수하고 친근한 동네 어른 말투. 편안하게 타이르듯 식단 개선을 제안한다.', '아이고 잘 챙겨 먹었네. 다음엔 짠 음식만 살짝 줄이면 더 좋겠다.', TRUE)
    ON DUPLICATE KEY UPDATE
                         name = VALUES(name),
                         role = VALUES(role),
                         tone_description = VALUES(tone_description),
                         sample_message = VALUES(sample_message),
                         active = VALUES(active);
INSERT INTO nutrition_reference_standards (
    standard_version,
    source_name,
    gender,
    age_min,
    age_max,
    sodium_mg,
    fiber_g
) VALUES
      ('KDRI_2020', '2020 Korean Dietary Reference Intakes', 'ALL', 1, 5, 1200, 15),
      ('KDRI_2020', '2020 Korean Dietary Reference Intakes', 'ALL', 6, 11, 1500, 20),
      ('KDRI_2020', '2020 Korean Dietary Reference Intakes', 'MALE', 12, 18, 2000, 25),
      ('KDRI_2020', '2020 Korean Dietary Reference Intakes', 'FEMALE', 12, 18, 2000, 20),
      ('KDRI_2020', '2020 Korean Dietary Reference Intakes', 'MALE', 19, 64, 2000, 30),
      ('KDRI_2020', '2020 Korean Dietary Reference Intakes', 'FEMALE', 19, 64, 2000, 20),
      ('KDRI_2020', '2020 Korean Dietary Reference Intakes', 'MALE', 65, 120, 2000, 25),
      ('KDRI_2020', '2020 Korean Dietary Reference Intakes', 'FEMALE', 65, 120, 2000, 20)
    ON DUPLICATE KEY UPDATE
                         source_name = VALUES(source_name),
                         sodium_mg = VALUES(sodium_mg),
                         fiber_g = VALUES(fiber_g);
INSERT INTO peer_nutrition_statistics (
    standard_version,
    source_name,
    gender,
    age_min,
    age_max,
    avg_calories,
    avg_protein_g,
    avg_carbs_g,
    avg_fat_g,
    avg_sodium_mg,
    avg_fiber_g
) VALUES
      ('KNHANES_2022', '2022 국민건강영양조사 (질병관리청)', 'ALL',    1,   5,  1320, 47,  190, 44,  1410, 10),
      ('KNHANES_2022', '2022 국민건강영양조사 (질병관리청)', 'ALL',    6,  11,  1740, 66,  258, 57,  2350, 15),
      ('KNHANES_2022', '2022 국민건강영양조사 (질병관리청)', 'MALE',  12,  18,  2340, 90,  348, 81,  3820, 19),
      ('KNHANES_2022', '2022 국민건강영양조사 (질병관리청)', 'FEMALE',12,  18,  1760, 65,  261, 59,  2930, 15),
      ('KNHANES_2022', '2022 국민건강영양조사 (질병관리청)', 'MALE',  19,  64,  2080, 88,  308, 74,  3720, 22),
      ('KNHANES_2022', '2022 국민건강영양조사 (질병관리청)', 'FEMALE',19,  64,  1600, 65,  242, 54,  2760, 18),
      ('KNHANES_2022', '2022 국민건강영양조사 (질병관리청)', 'MALE',  65, 120,  1840, 72,  291, 52,  2750, 21),
      ('KNHANES_2022', '2022 국민건강영양조사 (질병관리청)', 'FEMALE',65, 120,  1470, 55,  235, 39,  2090, 17)
    ON DUPLICATE KEY UPDATE
                         source_name   = VALUES(source_name),
                         avg_calories  = VALUES(avg_calories),
                         avg_protein_g = VALUES(avg_protein_g),
                         avg_carbs_g   = VALUES(avg_carbs_g),
                         avg_fat_g     = VALUES(avg_fat_g),
                         avg_sodium_mg = VALUES(avg_sodium_mg),
                         avg_fiber_g   = VALUES(avg_fiber_g);
INSERT INTO items (
    name,
    description,
    item_type,
    slot_type,
    price,
    image_url,
    default_item,
    purchasable,
    active,
    sort_order
)
SELECT '나무막대기', '그냥 주운 나뭇가지', 'EQUIPMENT', 'HAND', 0, '/images/items/wood-stick.png', TRUE, FALSE, TRUE, 1
    WHERE NOT EXISTS (SELECT 1 FROM items WHERE name = '나무막대기');
INSERT INTO items (
    name,
    description,
    item_type,
    slot_type,
    price,
    image_url,
    default_item,
    purchasable,
    active,
    sort_order
)
SELECT '칼', '번쩍이는 강철 검', 'EQUIPMENT', 'HAND', 500, '/images/items/sword.png', FALSE, TRUE, TRUE, 2
    WHERE NOT EXISTS (SELECT 1 FROM items WHERE name = '칼');
INSERT INTO items (
    name,
    description,
    item_type,
    slot_type,
    price,
    image_url,
    default_item,
    purchasable,
    active,
    sort_order
)
SELECT '지팡이', '마법의 기운이 흐른다', 'EQUIPMENT', 'HAND', 900, '/images/items/staff.png', FALSE, TRUE, TRUE, 3
    WHERE NOT EXISTS (SELECT 1 FROM items WHERE name = '지팡이');
INSERT INTO items (
    name,
    description,
    item_type,
    slot_type,
    price,
    image_url,
    default_item,
    purchasable,
    active,
    sort_order
)
SELECT '왕관', '길드 최고의 명예', 'EQUIPMENT', 'HEAD', 1500, '/images/items/crown.png', FALSE, TRUE, TRUE, 4
    WHERE NOT EXISTS (SELECT 1 FROM items WHERE name = '왕관');
-- Boss season, quest templates, and sample foods follow.
-- Boss season, quest templates, and sample foods follow.

SET NAMES utf8mb4;
USE nyamnyam;

INSERT INTO boss_seasons (
    season_code,
    name,
    description,
    target_nutrient,
    start_date,
    end_date,
    active
) VALUES (
             'SUGAR-DRAGON-2026-06',
             '2026년 6월 당분 드래곤 시즌',
             '당류 섭취를 줄이는 길드 보스 시즌',
             'SUGAR',
             '2026-06-15',
             '2026-06-21',
             TRUE
         )
    ON DUPLICATE KEY UPDATE
                         name = VALUES(name),
                         description = VALUES(description),
                         target_nutrient = VALUES(target_nutrient),
                         start_date = VALUES(start_date),
                         end_date = VALUES(end_date),
                         active = VALUES(active);

SET @sugar_season_id = (
    SELECT season_id
    FROM boss_seasons
    WHERE season_code = 'SUGAR-DRAGON-2026-06'
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
      (
          @sugar_season_id,
          '당분 드래곤',
          '난이도를 선택해 길드원과 함께 전투를 시작하세요.',
          'EASY',
          500,
          '/images/boss/sugar-dragon.png',
          800,
          100,
          'ACTIVE',
          '2026-06-15 00:00:00',
          '2026-06-21 23:59:59'
      ),
      (
          @sugar_season_id,
          '당분 드래곤',
          '난이도를 선택해 길드원과 함께 전투를 시작하세요.',
          'NORMAL',
          1000,
          '/images/boss/sugar-dragon.png',
          1200,
          150,
          'ACTIVE',
          '2026-06-15 00:00:00',
          '2026-06-21 23:59:59'
      ),
      (
          @sugar_season_id,
          '당분 드래곤',
          '난이도를 선택해 길드원과 함께 전투를 시작하세요.',
          'HARD',
          1800,
          '/images/boss/sugar-dragon.png',
          2400,
          300,
          'ACTIVE',
          '2026-06-15 00:00:00',
          '2026-06-21 23:59:59'
      )
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

SET @sugar_easy_boss_id = (
    SELECT boss_id
    FROM bosses
    WHERE season_id = @sugar_season_id
      AND difficulty = 'EASY'
);

SET @sugar_normal_boss_id = (
    SELECT boss_id
    FROM bosses
    WHERE season_id = @sugar_season_id
      AND difficulty = 'NORMAL'
);

SET @sugar_hard_boss_id = (
    SELECT boss_id
    FROM bosses
    WHERE season_id = @sugar_season_id
      AND difficulty = 'HARD'
);

DELETE FROM boss_common_conditions
WHERE boss_id IN (@sugar_easy_boss_id, @sugar_normal_boss_id, @sugar_hard_boss_id);

INSERT INTO boss_common_conditions (
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
    threshold_min_value,
    threshold_max_value,
    threshold_unit,
    target_value,
    required_days,
    required_for_clear,
    verification_supported,
    unit,
    sort_order
) VALUES
      (
          @sugar_season_id,
          @sugar_easy_boss_id,
          '당류 20g 이하 식사 공동 달성',
          '길드원들이 당류 20g 이하 식사를 총 3회 기록하면 완료됩니다.',
          'SUGAR_UNDER_LIMIT',
          'NUTRITION',
          'SUGAR',
          'LESS_THAN_OR_EQUAL',
          'TOTAL_COUNT',
          'GUILD_BATTLE_PERIOD',
          20,
          NULL,
          NULL,
          'GRAM',
          3,
          NULL,
          TRUE,
          TRUE,
          'COUNT',
          1
      ),
      (
          @sugar_season_id,
          @sugar_normal_boss_id,
          '당류 20g 이하 식사 공동 달성',
          '길드원들이 당류 20g 이하 식사를 총 5회 기록하면 완료됩니다.',
          'SUGAR_UNDER_LIMIT',
          'NUTRITION',
          'SUGAR',
          'LESS_THAN_OR_EQUAL',
          'TOTAL_COUNT',
          'GUILD_BATTLE_PERIOD',
          20,
          NULL,
          NULL,
          'GRAM',
          5,
          NULL,
          TRUE,
          TRUE,
          'COUNT',
          1
      ),
      (
          @sugar_season_id,
          @sugar_normal_boss_id,
          '단백질 25g 이상 식사 공동 달성',
          '길드원들이 단백질 25g 이상 식사를 총 3회 기록하면 완료됩니다.',
          'PROTEIN_OVER_TARGET',
          'NUTRITION',
          'PROTEIN',
          'GREATER_THAN_OR_EQUAL',
          'TOTAL_COUNT',
          'GUILD_BATTLE_PERIOD',
          25,
          NULL,
          NULL,
          'GRAM',
          3,
          NULL,
          TRUE,
          TRUE,
          'COUNT',
          2
      ),
      (
          @sugar_season_id,
          @sugar_hard_boss_id,
          '당류 20g 이하 식사 공동 달성',
          '길드원들이 당류 20g 이하 식사를 총 7회 기록하면 완료됩니다.',
          'SUGAR_UNDER_LIMIT',
          'NUTRITION',
          'SUGAR',
          'LESS_THAN_OR_EQUAL',
          'TOTAL_COUNT',
          'GUILD_BATTLE_PERIOD',
          20,
          NULL,
          NULL,
          'GRAM',
          7,
          NULL,
          TRUE,
          TRUE,
          'COUNT',
          1
      ),
      (
          @sugar_season_id,
          @sugar_hard_boss_id,
          '단백질 25g 이상 식사 공동 달성',
          '길드원들이 단백질 25g 이상 식사를 총 5회 기록하면 완료됩니다.',
          'PROTEIN_OVER_TARGET',
          'NUTRITION',
          'PROTEIN',
          'GREATER_THAN_OR_EQUAL',
          'TOTAL_COUNT',
          'GUILD_BATTLE_PERIOD',
          25,
          NULL,
          NULL,
          'GRAM',
          5,
          NULL,
          TRUE,
          TRUE,
          'COUNT',
          2
      ),
      (
          @sugar_season_id,
          @sugar_hard_boss_id,
          '나트륨 800mg 이하 식사 공동 달성',
          '길드원들이 나트륨 800mg 이하 식사를 총 5회 기록하면 완료됩니다.',
          'SODIUM_UNDER_LIMIT',
          'NUTRITION',
          'SODIUM',
          'LESS_THAN_OR_EQUAL',
          'TOTAL_COUNT',
          'GUILD_BATTLE_PERIOD',
          800,
          NULL,
          NULL,
          'MG',
          5,
          NULL,
          TRUE,
          TRUE,
          'COUNT',
          3
      );

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
SELECT '오늘 하루 총 당류 50g 이하 달성하기', '오늘 기록한 전체 식단의 당류 합계가 50g 이하이면 완료됩니다.', 'REDUCE_SUGAR', 'NUTRITION', 'SUGAR', 'LESS_THAN_OR_EQUAL',
       'DAILY_VALUE', 'USER_DAILY', 50, 'GRAM', 1, 'DAY', 120, 40, 15, 'NORMAL', 2
    WHERE NOT EXISTS (SELECT 1 FROM quest_templates WHERE quest_type = 'REDUCE_SUGAR' AND metric_type = 'SUGAR');

INSERT INTO quest_templates (
    title, description, quest_type, condition_category, metric_type, comparison_type,
    aggregation_type, evaluation_scope, threshold_value, threshold_unit, target_value,
    unit, damage, reward_exp, reward_coin, difficulty, sort_order
)
SELECT '오늘 하루 총 단백질 60g 이상 달성하기', '오늘 기록한 전체 식단의 단백질 합계가 60g 이상이면 완료됩니다.', 'ACHIEVE_PROTEIN_GOAL', 'NUTRITION', 'PROTEIN', 'GREATER_THAN_OR_EQUAL',
       'DAILY_VALUE', 'USER_DAILY', 60, 'GRAM', 1, 'DAY', 120, 40, 15, 'NORMAL', 3
    WHERE NOT EXISTS (SELECT 1 FROM quest_templates WHERE quest_type = 'ACHIEVE_PROTEIN_GOAL' AND metric_type = 'PROTEIN');

INSERT INTO quest_templates (
    title, description, quest_type, condition_category, metric_type, comparison_type,
    aggregation_type, evaluation_scope, threshold_value, threshold_unit, target_value,
    unit, damage, reward_exp, reward_coin, difficulty, sort_order
)
SELECT '오늘 하루 총 나트륨 2000mg 이하 달성하기', '오늘 기록한 전체 식단의 나트륨 합계가 2000mg 이하이면 완료됩니다.', 'REDUCE_SODIUM', 'NUTRITION', 'SODIUM', 'LESS_THAN_OR_EQUAL',
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
    '당류 20g 이하 식사 공동 달성',
    '길드원들이 당류 20g 이하 식사를 누적 기록하면 완료됩니다.',
    'SUGAR_UNDER_LIMIT',
    'NUTRITION',
    'SUGAR',
    'LESS_THAN_OR_EQUAL',
    'TOTAL_COUNT',
    'GUILD_BATTLE_PERIOD',
    20,
    'GRAM',
    3,
    NULL,
    'COUNT',
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
    '단백질 25g 이상 식사 공동 달성',
    '길드원들이 단백질 25g 이상 식사를 누적 기록하면 완료됩니다.',
    'PROTEIN_OVER_TARGET',
    'NUTRITION',
    'PROTEIN',
    'GREATER_THAN_OR_EQUAL',
    'TOTAL_COUNT',
    'GUILD_BATTLE_PERIOD',
    25,
    'GRAM',
    3,
    NULL,
    'COUNT',
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
    '나트륨 800mg 이하 식사 공동 달성',
    '길드원들이 나트륨 800mg 이하 식사를 누적 기록하면 완료됩니다.',
    'SODIUM_UNDER_LIMIT',
    'NUTRITION',
    'SODIUM',
    'LESS_THAN_OR_EQUAL',
    'TOTAL_COUNT',
    'GUILD_BATTLE_PERIOD',
    800,
    'MG',
    5,
    NULL,
    'COUNT',
    'HARD',
    TRUE,
    TRUE,
    3
    WHERE NOT EXISTS (
    SELECT 1 FROM boss_condition_templates WHERE target_type = 'SODIUM_UNDER_LIMIT'
);

UPDATE boss_condition_templates
SET title = '당류 20g 이하 식사 공동 달성',
    description = '길드원들이 당류 20g 이하 식사를 누적 기록하면 완료됩니다.',
    condition_category = 'NUTRITION',
    metric_type = 'SUGAR',
    comparison_type = 'LESS_THAN_OR_EQUAL',
    aggregation_type = 'TOTAL_COUNT',
    evaluation_scope = 'GUILD_BATTLE_PERIOD',
    threshold_value = 20,
    threshold_min_value = NULL,
    threshold_max_value = NULL,
    threshold_unit = 'GRAM',
    target_value = 3,
    required_days = NULL,
    unit = 'COUNT',
    difficulty = 'EASY',
    required_for_clear = TRUE,
    verification_supported = TRUE,
    active = TRUE,
    sort_order = 1
WHERE target_type = 'SUGAR_UNDER_LIMIT';

UPDATE boss_condition_templates
SET title = '단백질 25g 이상 식사 공동 달성',
    description = '길드원들이 단백질 25g 이상 식사를 누적 기록하면 완료됩니다.',
    condition_category = 'NUTRITION',
    metric_type = 'PROTEIN',
    comparison_type = 'GREATER_THAN_OR_EQUAL',
    aggregation_type = 'TOTAL_COUNT',
    evaluation_scope = 'GUILD_BATTLE_PERIOD',
    threshold_value = 25,
    threshold_min_value = NULL,
    threshold_max_value = NULL,
    threshold_unit = 'GRAM',
    target_value = 3,
    required_days = NULL,
    unit = 'COUNT',
    difficulty = 'NORMAL',
    required_for_clear = TRUE,
    verification_supported = TRUE,
    active = TRUE,
    sort_order = 2
WHERE target_type = 'PROTEIN_OVER_TARGET';

UPDATE boss_condition_templates
SET title = '나트륨 800mg 이하 식사 공동 달성',
    description = '길드원들이 나트륨 800mg 이하 식사를 누적 기록하면 완료됩니다.',
    condition_category = 'NUTRITION',
    metric_type = 'SODIUM',
    comparison_type = 'LESS_THAN_OR_EQUAL',
    aggregation_type = 'TOTAL_COUNT',
    evaluation_scope = 'GUILD_BATTLE_PERIOD',
    threshold_value = 800,
    threshold_min_value = NULL,
    threshold_max_value = NULL,
    threshold_unit = 'MG',
    target_value = 5,
    required_days = NULL,
    unit = 'COUNT',
    difficulty = 'HARD',
    required_for_clear = TRUE,
    verification_supported = TRUE,
    active = TRUE,
    sort_order = 3
WHERE target_type = 'SODIUM_UNDER_LIMIT';

SET @sugar_under_limit_template_id = (
    SELECT condition_template_id
    FROM boss_condition_templates
    WHERE target_type = 'SUGAR_UNDER_LIMIT'
    ORDER BY condition_template_id
    LIMIT 1
);

SET @protein_over_target_template_id = (
    SELECT condition_template_id
    FROM boss_condition_templates
    WHERE target_type = 'PROTEIN_OVER_TARGET'
    ORDER BY condition_template_id
    LIMIT 1
);

SET @sodium_under_limit_template_id = (
    SELECT condition_template_id
    FROM boss_condition_templates
    WHERE target_type = 'SODIUM_UNDER_LIMIT'
    ORDER BY condition_template_id
    LIMIT 1
);

UPDATE boss_common_conditions
SET condition_template_id = @sugar_under_limit_template_id,
    condition_category = 'NUTRITION',
    metric_type = 'SUGAR',
    comparison_type = 'LESS_THAN_OR_EQUAL',
    aggregation_type = 'TOTAL_COUNT',
    evaluation_scope = 'GUILD_BATTLE_PERIOD',
    threshold_value = 20,
    threshold_min_value = NULL,
    threshold_max_value = NULL,
    threshold_unit = 'GRAM',
    required_days = NULL,
    required_for_clear = TRUE,
    verification_supported = TRUE,
    unit = 'COUNT'
WHERE target_type = 'SUGAR_UNDER_LIMIT';

UPDATE boss_common_conditions
SET condition_template_id = @protein_over_target_template_id,
    condition_category = 'NUTRITION',
    metric_type = 'PROTEIN',
    comparison_type = 'GREATER_THAN_OR_EQUAL',
    aggregation_type = 'TOTAL_COUNT',
    evaluation_scope = 'GUILD_BATTLE_PERIOD',
    threshold_value = 25,
    threshold_min_value = NULL,
    threshold_max_value = NULL,
    threshold_unit = 'GRAM',
    required_days = NULL,
    required_for_clear = TRUE,
    verification_supported = TRUE,
    unit = 'COUNT'
WHERE target_type = 'PROTEIN_OVER_TARGET';

UPDATE boss_common_conditions
SET condition_template_id = @sodium_under_limit_template_id,
    condition_category = 'NUTRITION',
    metric_type = 'SODIUM',
    comparison_type = 'LESS_THAN_OR_EQUAL',
    aggregation_type = 'TOTAL_COUNT',
    evaluation_scope = 'GUILD_BATTLE_PERIOD',
    threshold_value = 800,
    threshold_min_value = NULL,
    threshold_max_value = NULL,
    threshold_unit = 'MG',
    required_days = NULL,
    required_for_clear = TRUE,
    verification_supported = TRUE,
    unit = 'COUNT'
WHERE target_type = 'SODIUM_UNDER_LIMIT';

UPDATE boss_battle_conditions
SET condition_template_id = @sugar_under_limit_template_id,
    title = '당류 20g 이하 식사 공동 달성',
    description = '길드원들이 당류 20g 이하 식사를 누적 기록하면 완료됩니다.',
    condition_category = 'NUTRITION',
    metric_type = 'SUGAR',
    comparison_type = 'LESS_THAN_OR_EQUAL',
    aggregation_type = 'TOTAL_COUNT',
    evaluation_scope = 'GUILD_BATTLE_PERIOD',
    threshold_value = 20,
    threshold_min_value = NULL,
    threshold_max_value = NULL,
    threshold_unit = 'GRAM',
    required_days = NULL,
    required_for_clear = TRUE,
    verification_supported = TRUE,
    unit = 'COUNT'
WHERE target_type = 'SUGAR_UNDER_LIMIT';

UPDATE boss_battle_conditions
SET condition_template_id = @protein_over_target_template_id,
    title = '단백질 25g 이상 식사 공동 달성',
    description = '길드원들이 단백질 25g 이상 식사를 누적 기록하면 완료됩니다.',
    condition_category = 'NUTRITION',
    metric_type = 'PROTEIN',
    comparison_type = 'GREATER_THAN_OR_EQUAL',
    aggregation_type = 'TOTAL_COUNT',
    evaluation_scope = 'GUILD_BATTLE_PERIOD',
    threshold_value = 25,
    threshold_min_value = NULL,
    threshold_max_value = NULL,
    threshold_unit = 'GRAM',
    required_days = NULL,
    required_for_clear = TRUE,
    verification_supported = TRUE,
    unit = 'COUNT'
WHERE target_type = 'PROTEIN_OVER_TARGET';

UPDATE boss_battle_conditions
SET condition_template_id = @sodium_under_limit_template_id,
    title = '나트륨 800mg 이하 식사 공동 달성',
    description = '길드원들이 나트륨 800mg 이하 식사를 누적 기록하면 완료됩니다.',
    condition_category = 'NUTRITION',
    metric_type = 'SODIUM',
    comparison_type = 'LESS_THAN_OR_EQUAL',
    aggregation_type = 'TOTAL_COUNT',
    evaluation_scope = 'GUILD_BATTLE_PERIOD',
    threshold_value = 800,
    threshold_min_value = NULL,
    threshold_max_value = NULL,
    threshold_unit = 'MG',
    required_days = NULL,
    required_for_clear = TRUE,
    verification_supported = TRUE,
    unit = 'COUNT'
WHERE target_type = 'SODIUM_UNDER_LIMIT';

DELETE FROM boss_common_conditions
WHERE target_type IN ('PROCESSED_DRINK_ZERO', 'VEGETABLE_VARIETY');

DELETE FROM boss_battle_conditions
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
             '2026-06-15',
             '2026-06-21',
             FALSE
         )
    ON DUPLICATE KEY UPDATE
                         name = VALUES(name),
                         description = VALUES(description),
                         target_nutrient = VALUES(target_nutrient),
                         start_date = VALUES(start_date),
                         end_date = VALUES(end_date),
                         active = FALSE;

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
      (@sugar_test_season_id, '당분 드래곤_테스트', 'Swagger 공통 조건 검증 테스트용 보스입니다.', 'EASY', 500, '/images/boss/sugar-dragon.png', 800, 100, 'ACTIVE', '2026-06-15 00:00:00', '2026-06-21 23:59:59'),
      (@sugar_test_season_id, '당분 드래곤_테스트', 'Swagger 공통 조건 검증 테스트용 보스입니다.', 'NORMAL', 1000, '/images/boss/sugar-dragon.png', 1200, 150, 'ACTIVE', '2026-06-15 00:00:00', '2026-06-21 23:59:59'),
      (@sugar_test_season_id, '당분 드래곤_테스트', 'Swagger 공통 조건 검증 테스트용 보스입니다.', 'HARD', 1800, '/images/boss/sugar-dragon.png', 2400, 300, 'ACTIVE', '2026-06-15 00:00:00', '2026-06-21 23:59:59')
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

DELETE FROM boss_common_conditions
WHERE season_id = @sugar_test_season_id;

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
    threshold_min_value,
    threshold_max_value,
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
    '당류 20g 이하 식사 공동 달성',
    '길드원들이 당류 20g 이하 식사를 총 1회 기록하면 완료됩니다.',
    'SUGAR_UNDER_LIMIT',
    'NUTRITION',
    'SUGAR',
    'LESS_THAN_OR_EQUAL',
    'TOTAL_COUNT',
    'GUILD_BATTLE_PERIOD',
    20,
    NULL,
    NULL,
    'GRAM',
    1,
    NULL,
    TRUE,
    TRUE,
    'COUNT',
    1
FROM bosses b
WHERE b.season_id = @sugar_test_season_id
  AND b.name = '당분 드래곤_테스트';

-- Development sample food seed. Full seed is stored at BackEnd/scripts/full/10-foods-seed-full.sql.
SET NAMES utf8mb4;
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-004160000-0001', '국밥_돼지머리', NULL, '밥류', 100, 'g', 900, 'g', NULL, 137, 6.70, 15.94, 5.16, 0.16, 181, 0.7, 0.24, 24, 47, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-004310000-0001', '국밥_순대국밥', NULL, '밥류', 100, 'g', 900, 'g', NULL, 75, 3.17, 10.38, 2.28, 0.17, 126, 1.3, 0.67, 31, 34, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-004500000-0001', '국밥_콩나물', NULL, '밥류', 100, 'g', 780, 'g', NULL, 52, 1.45, 10.93, 0.24, 0.00, 172, 1.2, 0.18, 16, 42, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-006000000-0001', '기장밥', NULL, '밥류', 100, 'g', 200, 'g', NULL, 166, 3.44, 36.77, 0.57, 0.00, 1, 1.5, 0.33, 28, 25, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-007000000-0001', '김밥', NULL, '밥류', 100, 'g', 230, 'g', NULL, 140, 4.84, 19.98, 4.55, 0.00, 307, 1.4, 0.30, 55, 118, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-007070000-0001', '김밥_김치', NULL, '밥류', 100, 'g', 270, 'g', NULL, 130, 4.30, 19.17, 4.03, 0.29, 349, 1.8, 0.31, 64, 126, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-007090000-0001', '김밥_날치알', NULL, '밥류', 100, 'g', 260, 'g', NULL, 177, 6.10, 28.66, 4.26, 1.43, 299, 2.1, 0.62, 82, 118, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-007120000-0001', '김밥_돈가스', NULL, '밥류', 100, 'g', 320, 'g', NULL, 202, 5.77, 31.64, 5.81, 1.33, 241, 2.1, 0.49, 74, 134, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-007280000-0001', '김밥_소고기', NULL, '밥류', 100, 'g', 250, 'g', NULL, 179, 6.46, 25.78, 5.56, 0.03, 267, 1.5, 0.87, 64, 96, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-007450000-0001', '김밥_참치', NULL, '밥류', 100, 'g', 250, 'g', NULL, 174, 7.00, 20.26, 7.22, 0.71, 335, 1.8, 0.72, 73, 123, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-007480000-0001', '김밥_채소', NULL, '밥류', 100, 'g', 280, 'g', NULL, 158, 4.60, 26.65, 3.65, 0.14, 309, 3.4, 0.44, 69, 164, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-007490000-0001', '김밥_치즈', NULL, '밥류', 100, 'g', 270, 'g', NULL, 177, 6.24, 22.10, 7.03, 0.21, 169, 2.6, 0.21, 15, 41, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-007520000-0001', '김밥_풋고추', NULL, '밥류', 100, 'g', 290, 'g', NULL, 169, 4.88, 27.52, 4.41, 0.09, 327, 3.3, 0.49, 70, 148, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-010080000-0001', '덮밥_낙지', NULL, '밥류', 100, 'g', 350, 'g', NULL, 150, 5.88, 24.12, 3.34, 2.16, 212, 2.3, 0.36, 47, 95, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-010110000-0001', '덮밥_닭고기', NULL, '밥류', 100, 'g', 410, 'g', NULL, 125, 11.40, 14.82, 2.18, 1.07, 153, 1.1, 0.53, 108, 137, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-010150000-0001', '덮밥_돼지고기(제육)', NULL, '밥류', 100, 'g', 470, 'g', NULL, 202, 9.43, 16.86, 10.77, 1.98, 174, 1.4, 0.49, 101, 196, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-010240000-0001', '덮밥_불고기', NULL, '밥류', 100, 'g', 400, 'g', NULL, 182, 6.60, 26.96, 5.31, 0.07, 253, 2.8, 0.82, 73, 138, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-010390000-0001', '덮밥_오징어', NULL, '밥류', 100, 'g', 360, 'g', NULL, 135, 7.18, 21.94, 2.01, 0.66, 162, 2.6, 0.22, 78, 135, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-016000000-0001', '보리밥', NULL, '밥류', 100, 'g', 200, 'g', NULL, 161, 2.90, 36.77, 0.24, 2.57, 4, 2.4, 0.20, 34, 28, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-017000000-0001', '볶음밥', NULL, '밥류', 100, 'g', 350, 'g', NULL, 183, 5.56, 33.97, 2.76, 0.00, 212, 1.7, 0.52, 61, 99, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-017030000-0001', '볶음밥_계란', NULL, '밥류', 100, 'g', 260, 'g', NULL, 225, 6.62, 24.23, 11.28, 0.17, 318, 1.8, 0.44, 94, 159, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-017070000-0001', '볶음밥_김치', NULL, '밥류', 100, 'g', 330, 'g', NULL, 167, 5.32, 15.74, 9.18, 13.89, 470, 2.8, 0.70, 72, 168, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-017260000-0001', '볶음밥_새우', NULL, '밥류', 100, 'g', 370, 'g', NULL, 172, 6.31, 22.85, 6.14, 0.71, 146, 1.6, 0.35, 62, 65, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-017280000-0001', '볶음밥_소고기', NULL, '밥류', 100, 'g', 260, 'g', NULL, 176, 7.59, 21.76, 6.48, 0.08, 215, 2.1, 0.62, 86, 167, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-017450000-0001', '볶음밥_참치', NULL, '밥류', 100, 'g', 230, 'g', NULL, 180, 4.62, 32.90, 3.27, 0.12, 631, 1.6, 0.11, 46, 66, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-017480000-0001', '볶음밥_채소', NULL, '밥류', 100, 'g', 260, 'g', NULL, 182, 4.72, 28.74, 5.39, 0.29, 408, 2.3, 0.42, 61, 156, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-017510000-0001', '볶음밥_표고버섯', NULL, '밥류', 100, 'g', 300, 'g', NULL, 184, 3.36, 31.79, 4.81, 0.00, 299, 2.6, 0.30, 39, 167, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-018000000-0001', '비빔밥', NULL, '밥류', 100, 'g', 450, 'g', NULL, 142, 6.86, 18.84, 4.32, 0.05, 232, 2.0, 1.04, 79, 180, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-018130000-0001', '비빔밥_돌솥', NULL, '밥류', 100, 'g', 380, 'g', NULL, 147, 5.39, 24.68, 2.95, 1.24, 337, 2.4, 0.59, 72, 114, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-018430000-0001', '비빔밥_육회', NULL, '밥류', 100, 'g', 300, 'g', NULL, 147, 6.39, 22.92, 3.36, 4.45, 446, 2.6, 0.89, 95, 178, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-019460000-0001', '삼각김밥_참치마요네즈', NULL, '밥류', 100, 'g', 200, 'g', NULL, 199, 5.93, 25.45, 8.21, 0.00, 230, 2.8, 0.38, 58, 76, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-021000000-0001', '수수밥', NULL, '밥류', 100, 'g', 200, 'g', NULL, 161, 3.16, 35.02, 0.92, 0.08, 1, 1.1, 0.17, 25, 26, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-023000000-0001', '알밥', NULL, '밥류', 100, 'g', 330, 'g', NULL, 128, 4.73, 15.75, 5.11, 2.30, 430, 3.5, 0.44, 61, 128, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-026000000-0001', '영양돌솥밥', NULL, '밥류', 100, 'g', 350, 'g', NULL, 184, 3.43, 36.06, 2.87, 0.63, 6, 2.1, 0.48, 32, 36, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-028000000-0001', '오므라이스', NULL, '밥류', 100, 'g', 400, 'g', NULL, 173, 6.50, 20.54, 7.23, 3.19, 433, 2.4, 0.84, 93, 163, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-031000000-0001', '자장밥', NULL, '밥류', 100, 'g', 470, 'g', NULL, 122, 4.27, 15.57, 4.72, 0.03, 236, 2.1, 0.41, 49, 137, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-032000000-0001', '잡곡밥', NULL, '밥류', 100, 'g', 200, 'g', NULL, 146, 5.30, 29.33, 0.87, 0.32, 3, 6.1, 1.33, 111, 157, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-033000000-0001', '잡채밥', NULL, '밥류', 100, 'g', 550, 'g', NULL, 150, 4.02, 25.79, 3.42, 0.05, 227, 3.0, 0.78, 47, 148, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-035280000-0001', '주먹밥_소고기', NULL, '밥류', 100, 'g', 200, 'g', NULL, 178, 4.76, 34.73, 2.18, 0.23, 357, 1.5, 0.29, 46, 69, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-037000000-0001', '짬뽕밥', NULL, '밥류', 100, 'g', 900, 'g', NULL, 72, 3.20, 11.65, 1.40, 1.34, 234, 1.4, 0.67, 53, 138, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-038000000-0001', '차조밥', NULL, '밥류', 100, 'g', 200, 'g', NULL, 157, 2.84, 34.86, 0.65, 0.04, 1, 1.8, 0.32, 30, 27, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-039000000-0001', '찰밥', NULL, '밥류', 100, 'g', 280, 'g', NULL, 153, 4.39, 33.56, 0.18, 0.49, 3, 2.9, 0.22, 56, 168, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-042200000-0001', '초밥_모듬', NULL, '밥류', 100, 'g', 380, 'g', NULL, 160, 9.66, 26.72, 1.59, 1.40, 142, 2.0, 0.07, 106, 161, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-042410000-0001', '초밥_유부초밥', NULL, '밥류', 100, 'g', 200, 'g', NULL, 194, 5.78, 29.82, 5.77, 1.99, 386, 2.6, 0.59, 67, 57, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-044000000-0001', '카레라이스', NULL, '밥류', 100, 'g', 480, 'g', NULL, 108, 3.11, 20.74, 1.40, 0.16, 210, 0.5, 0.30, 35, 91, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-047000000-0001', '콩나물밥', NULL, '밥류', 100, 'g', 320, 'g', NULL, 123, 4.68, 22.39, 1.58, 0.04, 272, 1.9, 0.52, 125, 241, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-048020000-0001', '콩밥_검정콩', NULL, '밥류', 100, 'g', 200, 'g', NULL, 165, 4.53, 34.30, 1.12, 0.00, 3, 1.1, 0.38, 52, 86, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-048400000-0001', '콩밥_완두콩', NULL, '밥류', 100, 'g', 200, 'g', NULL, 138, 2.95, 31.25, 0.16, 0.00, 295, 1.9, 0.34, 32, 70, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-049000000-0001', '하이라이스', NULL, '밥류', 100, 'g', 360, 'g', NULL, 112, 4.02, 21.34, 1.13, 0.00, 246, 0.9, 0.07, 36, 77, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-050000000-0001', '현미밥', NULL, '밥류', 100, 'g', 230, 'g', NULL, 172, 3.10, 38.90, 0.47, 0.00, 2, 2.0, 0.21, 57, 41, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D101-051200000-0001', '회덮밥_모듬', NULL, '밥류', 100, 'g', 410, 'g', NULL, 145, 9.10, 17.76, 4.15, 3.07, 155, 1.2, 0.23, 108, 217, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-053000000-0001', '가래떡', NULL, '빵 및 과자류', 100, 'g', 100, 'g', NULL, 195, 3.92, 43.73, 0.51, 0.00, 241, 0.6, 0.25, 43, 23, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-055040000-0001', '경단_깨', NULL, '빵 및 과자류', 100, 'g', 100, 'g', NULL, 240, 6.28, 38.58, 6.72, 0.61, 203, 4.6, 0.84, 104, 75, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-057000000-0001', '꿀떡', NULL, '빵 및 과자류', 100, 'g', 100, 'g', NULL, 228, 3.74, 49.48, 1.73, 0.29, 275, 0.8, 0.36, 52, 39, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-058000000-0001', '모듬찰떡', NULL, '빵 및 과자류', 100, 'g', 100, 'g', NULL, 218, 6.78, 42.27, 2.44, 2.98, 188, 3.8, 0.75, 91, 191, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-061000000-0001', '백설기', NULL, '빵 및 과자류', 100, 'g', 100, 'g', NULL, 230, 3.71, 50.40, 1.45, 0.11, 211, 2.0, 0.08, 36, 31, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-063040000-0001', '송편_깨', NULL, '빵 및 과자류', 100, 'g', 100, 'g', NULL, 219, 4.65, 42.83, 3.23, 1.98, 260, 4.4, 0.46, 65, 50, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-067000000-0001', '시루떡', NULL, '빵 및 과자류', 100, 'g', 100, 'g', NULL, 224, 3.77, 48.13, 1.85, 0.04, 195, 4.0, 0.66, 65, 161, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-069000000-0001', '약식', NULL, '빵 및 과자류', 100, 'g', 100, 'g', NULL, 220, 4.21, 45.24, 2.45, 13.24, 221, 2.6, 0.33, 43, 85, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-070000000-0001', '인절미', NULL, '빵 및 과자류', 100, 'g', 100, 'g', NULL, 242, 5.64, 50.07, 2.16, 6.35, 297, 2.9, 0.40, 61, 128, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-071260000-0001', '절편_쑥', NULL, '빵 및 과자류', 100, 'g', 100, 'g', NULL, 192, 7.72, 34.87, 2.42, 0.25, 192, 2.0, 0.43, 34, 39, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-072000000-0001', '증편', NULL, '빵 및 과자류', 100, 'g', 100, 'g', NULL, 200, 3.17, 44.15, 1.20, 3.19, 284, 1.0, 0.10, 38, 22, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-074390000-0001', '찹쌀떡_팥', NULL, '빵 및 과자류', 100, 'g', 100, 'g', NULL, 295, 5.06, 62.93, 2.53, 25.95, 140, 1.8, 0.41, 34, 34, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-082120000-0001', '도넛_링도넛', NULL, '빵 및 과자류', 100, 'g', 100, 'g', NULL, 515, 7.85, 46.16, 33.17, 11.60, 537, 1.2, 1.04, 364, 176, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-082290000-0001', '도넛_찹쌀', NULL, '빵 및 과자류', 100, 'g', 70, 'g', NULL, 288, 6.00, 55.56, 4.64, 11.04, 312, 3.9, 0.76, 97, 49, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-085000000-0001', '마늘빵', NULL, '빵 및 과자류', 100, 'g', 50, 'g', NULL, 438, 12.06, 57.94, 17.58, 7.36, 519, 4.3, 0.58, 119, 178, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-087000000-0001', '머핀', NULL, '빵 및 과자류', 100, 'g', 130, 'g', NULL, 384, 5.73, 59.15, 13.84, 39.65, 411, 1.1, 0.69, 137, 99, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-088000000-0001', '모닝빵', NULL, '빵 및 과자류', 100, 'g', 70, 'g', NULL, 314, 9.50, 52.17, 7.43, 7.97, 489, 2.4, 0.56, 98, 115, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-089000000-0001', '모카빵', NULL, '빵 및 과자류', 100, 'g', 150, 'g', NULL, 339, 8.55, 57.96, 8.05, 12.53, 354, 3.6, 0.97, 133, 173, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-095000000-0001', '베이글', NULL, '빵 및 과자류', 100, 'g', 120, 'g', NULL, 306, 10.00, 53.48, 5.83, 4.58, 695, 2.7, 0.76, 98, 109, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-096060000-0001', '샌드위치_닭가슴살', NULL, '빵 및 과자류', 100, 'g', 230, 'g', NULL, 240, 12.18, 20.96, 11.92, 3.09, 438, 2.4, 0.17, 129, 200, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-096150000-0001', '샌드위치_모듬', NULL, '빵 및 과자류', 100, 'g', 220, 'g', NULL, 193, 9.50, 8.44, 13.44, 0.60, 358, 1.9, 0.75, 187, 152, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-096280000-0001', '샌드위치_참치', NULL, '빵 및 과자류', 100, 'g', 250, 'g', NULL, 202, 10.54, 13.78, 11.65, 1.38, 209, 1.7, 0.64, 91, 152, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-096300000-0001', '샌드위치_채소', NULL, '빵 및 과자류', 100, 'g', 180, 'g', NULL, 244, 4.08, 23.82, 14.75, 14.26, 426, 2.5, 0.07, 47, 162, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-096431100-0001', '샌드위치_햄_달걀', NULL, '빵 및 과자류', 100, 'g', 200, 'g', NULL, 220, 12.44, 15.64, 11.92, 1.90, 699, 1.7, 0.55, 258, 189, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-097000000-0001', '소보로빵', NULL, '빵 및 과자류', 100, 'g', 70, 'g', NULL, 392, 8.27, 55.63, 15.13, 16.69, 396, 2.9, 0.74, 107, 120, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-100000000-0001', '식빵', NULL, '빵 및 과자류', 100, 'g', 100, 'g', NULL, 264, 9.34, 50.91, 2.60, 1.56, 468, 3.5, 0.76, 117, 129, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-105000000-0001', '초코소라빵', NULL, '빵 및 과자류', 100, 'g', 70, 'g', NULL, 319, 3.29, 51.76, 10.93, 9.29, 215, 2.1, 1.23, 101, 182, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-106000000-0001', '츄러스', NULL, '빵 및 과자류', 100, 'g', 70, 'g', NULL, 361, 6.60, 58.17, 11.29, 22.73, 363, 3.0, 0.59, 178, 64, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-109000000-0001', '카스텔라', NULL, '빵 및 과자류', 100, 'g', 70, 'g', NULL, 335, 10.03, 47.99, 11.47, 33.77, 63, 1.3, 1.46, 187, 96, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-110110000-0001', '케이크_롤케이크', NULL, '빵 및 과자류', 100, 'g', 100, 'g', NULL, 312, 7.36, 44.01, 11.83, 24.64, 143, 1.4, 1.13, 140, 98, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-110210000-0001', '케이크_생크림케이크', NULL, '빵 및 과자류', 100, 'g', 100, 'g', NULL, 237, 3.45, 27.62, 12.54, 14.49, 151, 1.1, 0.58, 131, 106, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-111000000-0001', '크로와상', NULL, '빵 및 과자류', 100, 'g', 150, 'g', NULL, 446, 7.52, 43.31, 26.98, 6.10, 457, 2.3, 0.80, 133, 111, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-116190000-0001', '파이/만주_사과파이', NULL, '빵 및 과자류', 100, 'g', 100, 'g', NULL, 185, 2.54, 34.83, 3.91, 7.11, 204, 2.3, 0.43, 169, 94, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-118000000-0001', '페이스트리', NULL, '빵 및 과자류', 100, 'g', 70, 'g', NULL, 466, 8.31, 41.50, 29.66, 6.43, 373, 2.1, 0.46, 88, 102, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-120350000-0001', '피자_콤비네이션피자', NULL, '빵 및 과자류', 100, 'g', 200, 'g', NULL, 274, 10.68, 25.54, 14.36, 4.85, 441, 2.6, 0.63, 180, 214, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-121000000-0001', '피자빵', NULL, '빵 및 과자류', 100, 'g', 200, 'g', NULL, 298, 8.85, 31.76, 15.05, 8.11, 637, 2.2, 0.87, 163, 164, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-123000000-0001', '햄버거', NULL, '빵 및 과자류', 100, 'g', 200, 'g', NULL, 264, 12.85, 21.67, 13.98, 3.13, 450, 2.8, 1.36, 116, 203, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-123170000-0001', '햄버거_불고기버거', NULL, '빵 및 과자류', 100, 'g', 200, 'g', NULL, 250, 13.88, 22.68, 11.52, 0.13, 338, 3.6, 1.24, 117, 206, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D102-125030000-0001', '호떡_견과류', NULL, '빵 및 과자류', 100, 'g', 150, 'g', NULL, 314, 5.41, 57.12, 7.07, 0.73, 211, 2.1, 0.38, 15, 33, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D103-141000000-0001', '간자장', NULL, '면 및 만두류', 100, 'g', 500, 'g', NULL, 125, 5.12, 15.13, 4.89, 0.86, 455, 1.5, 0.54, 53, 125, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D103-142160000-0001', '국수_막국수', NULL, '면 및 만두류', 100, 'g', 600, 'g', NULL, 133, 5.80, 23.90, 1.61, 4.88, 405, 1.7, 1.02, 52, 135, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D103-142270000-0001', '국수_비빔국수', NULL, '면 및 만두류', 100, 'g', 500, 'g', NULL, 102, 3.01, 20.47, 0.95, 4.11, 239, 1.4, 0.53, 34, 112, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D103-142410000-0001', '국수_잔치국수', NULL, '면 및 만두류', 100, 'g', 700, 'g', NULL, 44, 1.93, 8.05, 0.49, 0.04, 216, 0.8, 1.44, 13, 38, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D103-142420000-0001', '국수_쟁반막국수', NULL, '면 및 만두류', 100, 'g', 450, 'g', NULL, 119, 6.68, 12.81, 4.56, 8.12, 513, 4.5, 1.00, 100, 245, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D103-144180000-0001', '냉면_물냉면', NULL, '면 및 만두류', 100, 'g', 700, 'g', NULL, 55, 1.96, 10.31, 0.64, 4.52, 161, 0.3, 0.28, 25, 49, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D103-144280000-0001', '냉면_비빔냉면', NULL, '면 및 만두류', 100, 'g', 500, 'g', NULL, 100, 5.67, 13.35, 2.64, 4.02, 177, 1.5, 0.49, 62, 167, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D103-144515500-0001', '냉면_회냉면_홍어', NULL, '면 및 만두류', 100, 'g', 500, 'g', NULL, 148, 5.81, 22.21, 4.02, 2.84, 99, 2.6, 1.01, 73, 161, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D103-145000000-0001', '떡국', NULL, '면 및 만두류', 100, 'g', 700, 'g', NULL, 84, 3.43, 16.37, 0.53, 0.08, 176, 0.8, 0.37, 34, 51, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
INSERT INTO foods (external_food_code, name, brand, category, nutrition_basis_amount, nutrition_basis_unit, serving_amount, serving_unit, gram_per_piece, calories, protein_g, carbs_g, fat_g, sugar_g, sodium_mg, fiber_g, iron_mg, phosphorus_mg, potassium_mg, vitamin_a_ug_rae, beta_carotene_ug, retinol_ug, source)
VALUES ('D103-146000000-0001', '떡만두국', NULL, '면 및 만두류', 100, 'g', 600, 'g', NULL, 111, 3.53, 15.47, 3.91, 0.01, 206, 1.8, 0.56, 37, 91, 0, 0, 0, '식품의약품안전처')
    ON DUPLICATE KEY UPDATE name = VALUES(name), brand = VALUES(brand), category = VALUES(category), nutrition_basis_amount = VALUES(nutrition_basis_amount), nutrition_basis_unit = VALUES(nutrition_basis_unit), serving_amount = VALUES(serving_amount), serving_unit = VALUES(serving_unit), gram_per_piece = VALUES(gram_per_piece), calories = VALUES(calories), protein_g = VALUES(protein_g), carbs_g = VALUES(carbs_g), fat_g = VALUES(fat_g), sugar_g = VALUES(sugar_g), sodium_mg = VALUES(sodium_mg), fiber_g = VALUES(fiber_g), iron_mg = VALUES(iron_mg), phosphorus_mg = VALUES(phosphorus_mg), potassium_mg = VALUES(potassium_mg), vitamin_a_ug_rae = VALUES(vitamin_a_ug_rae), beta_carotene_ug = VALUES(beta_carotene_ug), retinol_ug = VALUES(retinol_ug), source = VALUES(source);
