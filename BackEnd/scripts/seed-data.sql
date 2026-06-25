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
    effect_value,
    default_item,
    purchasable,
    active,
    sort_order
)
SELECT '나무막대기', '그냥 주운 나뭇가지', 'EQUIPMENT', 'HAND', 0, '/images/items/wood-stick.png', NULL, TRUE, FALSE, TRUE, 1
    WHERE NOT EXISTS (SELECT 1 FROM items WHERE name = '나무막대기');
INSERT INTO items (
    name,
    description,
    item_type,
    slot_type,
    price,
    image_url,
    effect_value,
    default_item,
    purchasable,
    active,
    sort_order
)
SELECT '칼', '번쩍이는 강철 검', 'EQUIPMENT', 'HAND', 500, '/images/items/sword.png', NULL, FALSE, TRUE, TRUE, 2
    WHERE NOT EXISTS (SELECT 1 FROM items WHERE name = '칼');
INSERT INTO items (
    name,
    description,
    item_type,
    slot_type,
    price,
    image_url,
    effect_value,
    default_item,
    purchasable,
    active,
    sort_order
)
SELECT '지팡이', '마법의 기운이 흐른다', 'EQUIPMENT', 'HAND', 900, '/images/items/staff.png', NULL, FALSE, TRUE, TRUE, 3
    WHERE NOT EXISTS (SELECT 1 FROM items WHERE name = '지팡이');
INSERT INTO items (
    name,
    description,
    item_type,
    slot_type,
    price,
    image_url,
    effect_value,
    default_item,
    purchasable,
    active,
    sort_order
)
SELECT '왕관', '길드 최고의 명예', 'EQUIPMENT', 'HEAD', 1500, '/images/items/crown.png', NULL, FALSE, TRUE, TRUE, 4
    WHERE NOT EXISTS (SELECT 1 FROM items WHERE name = '왕관');
INSERT INTO items (
    name,
    description,
    item_type,
    slot_type,
    price,
    image_url,
    effect_value,
    default_item,
    purchasable,
    active,
    sort_order
)
SELECT '냠냠이 캐릭터', '기본으로 함께하는 든든한 냠냠이 모험가', 'CHARACTER', 'CHARACTER', 0, 'NYAMNYAM', 'NYAMNYAM', TRUE, FALSE, TRUE, 19
    WHERE NOT EXISTS (SELECT 1 FROM items WHERE name = '냠냠이 캐릭터');
INSERT INTO items (
    name,
    description,
    item_type,
    slot_type,
    price,
    image_url,
    effect_value,
    default_item,
    purchasable,
    active,
    sort_order
)
SELECT '펭귄 캐릭터', '차분한 펭귄 모험가', 'CHARACTER', 'CHARACTER', 5000, 'PENGUIN', 'PENGUIN', FALSE, TRUE, TRUE, 20
    WHERE NOT EXISTS (SELECT 1 FROM items WHERE name = '펭귄 캐릭터');
INSERT INTO items (
    name,
    description,
    item_type,
    slot_type,
    price,
    image_url,
    effect_value,
    default_item,
    purchasable,
    active,
    sort_order
)
SELECT '강아지 캐릭터', '활발한 강아지 모험가', 'CHARACTER', 'CHARACTER', 5000, 'DOG', 'DOG', FALSE, TRUE, TRUE, 21
    WHERE NOT EXISTS (SELECT 1 FROM items WHERE name = '강아지 캐릭터');
INSERT INTO items (
    name,
    description,
    item_type,
    slot_type,
    price,
    image_url,
    effect_value,
    default_item,
    purchasable,
    active,
    sort_order
)
SELECT '푸른 숲 배경', '냠냠이가 모험을 시작하는 싱그러운 숲 배경', 'BACKGROUND', 'BACKGROUND', 3000, 'BACKGROUND_1', 'BACKGROUND_1', FALSE, TRUE, TRUE, 30
    WHERE NOT EXISTS (SELECT 1 FROM items WHERE name = '푸른 숲 배경');
INSERT INTO items (
    name,
    description,
    item_type,
    slot_type,
    price,
    image_url,
    effect_value,
    default_item,
    purchasable,
    active,
    sort_order
)
SELECT '달콤한 월드 배경', '보스전의 달콤한 분위기를 담은 배경', 'BACKGROUND', 'BACKGROUND', 4000, 'BACKGROUND_2', 'BACKGROUND_2', FALSE, TRUE, TRUE, 31
    WHERE NOT EXISTS (SELECT 1 FROM items WHERE name = '달콤한 월드 배경');
INSERT INTO items (
    name,
    description,
    item_type,
    slot_type,
    price,
    image_url,
    effect_value,
    default_item,
    purchasable,
    active,
    sort_order
)
SELECT '모험 캠프 배경', '길드 모험을 준비하는 따뜻한 캠프 배경', 'BACKGROUND', 'BACKGROUND', 5000, 'BACKGROUND_3', 'BACKGROUND_3', FALSE, TRUE, TRUE, 32
    WHERE NOT EXISTS (SELECT 1 FROM items WHERE name = '모험 캠프 배경');

-- Boss seasons, bosses, common conditions, and quest templates.

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
             '2026-06-22',
             '2026-06-28',
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
          '당류를 줄이는 식습관을 방해하는 달콤한 드래곤',
          'EASY',
          500,
          'DRAGON',
          800,
          100,
          'ACTIVE',
          '2026-06-22 00:00:00',
          '2026-06-28 23:59:59'
      ),
      (
          @sugar_season_id,
          '염분 골렘',
          '짠맛으로 식단 균형을 무너뜨리는 염분 골렘',
          'NORMAL',
          1000,
          'GOLEM',
          1200,
          150,
          'ACTIVE',
          '2026-06-22 00:00:00',
          '2026-06-28 23:59:59'
      ),
      (
          @sugar_season_id,
          '단백질 해골기사',
          '단백질 목표 달성을 시험하는 해골기사',
          'HARD',
          1800,
          'KNIGHT',
          2400,
          300,
          'ACTIVE',
          '2026-06-22 00:00:00',
          '2026-06-28 23:59:59'
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
      (@sugar_season_id, @sugar_easy_boss_id, '당류 20g 이하 식사 공동 달성', '길드원들이 당류 20g 이하 식사를 총 3회 기록하면 완료됩니다.', 'SUGAR_UNDER_LIMIT', 'NUTRITION', 'SUGAR', 'LESS_THAN_OR_EQUAL', 'TOTAL_COUNT', 'GUILD_BATTLE_PERIOD', 20, NULL, NULL, 'GRAM', 3, NULL, TRUE, TRUE, 'COUNT', 1),
      (@sugar_season_id, @sugar_normal_boss_id, '나트륨 800mg 이하 식사 공동 달성', '길드원들이 나트륨 800mg 이하 식사를 총 5회 기록하면 완료됩니다.', 'SODIUM_UNDER_LIMIT', 'NUTRITION', 'SODIUM', 'LESS_THAN_OR_EQUAL', 'TOTAL_COUNT', 'GUILD_BATTLE_PERIOD', 800, NULL, NULL, 'MG', 5, NULL, TRUE, TRUE, 'COUNT', 1),
      (@sugar_season_id, @sugar_hard_boss_id, '단백질 25g 이상 식사 공동 달성', '길드원들이 단백질 25g 이상 식사를 총 7회 기록하면 완료됩니다.', 'PROTEIN_OVER_TARGET', 'NUTRITION', 'PROTEIN', 'GREATER_THAN_OR_EQUAL', 'TOTAL_COUNT', 'GUILD_BATTLE_PERIOD', 25, NULL, NULL, 'GRAM', 7, NULL, TRUE, TRUE, 'COUNT', 1);

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
             '2026-06-22',
             '2026-06-28',
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
      (@sugar_test_season_id, '당분 드래곤_테스트', 'Swagger 공통 조건 검증 테스트용 보스입니다.', 'EASY', 500, '/images/boss/sugar-dragon.png', 800, 100, 'ACTIVE', '2026-06-22 00:00:00', '2026-06-28 23:59:59'),
      (@sugar_test_season_id, '당분 드래곤_테스트', 'Swagger 공통 조건 검증 테스트용 보스입니다.', 'NORMAL', 1000, '/images/boss/sugar-dragon.png', 1200, 150, 'ACTIVE', '2026-06-22 00:00:00', '2026-06-28 23:59:59'),
      (@sugar_test_season_id, '당분 드래곤_테스트', 'Swagger 공통 조건 검증 테스트용 보스입니다.', 'HARD', 1800, '/images/boss/sugar-dragon.png', 2400, 300, 'ACTIVE', '2026-06-22 00:00:00', '2026-06-28 23:59:59')
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
