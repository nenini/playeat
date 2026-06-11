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
        50,
        '/images/boss/sugar-dragon.png',
        800,
        100,
        'ACTIVE',
        '2026-06-10 00:00:00',
        '2026-06-16 23:59:59'
    ),
    (
        @sugar_season_id,
        '당분 드래곤',
        '난이도를 선택해 길드원과 함께 전투를 시작하세요.',
        'NORMAL',
        100,
        '/images/boss/sugar-dragon.png',
        1200,
        150,
        'ACTIVE',
        '2026-06-10 00:00:00',
        '2026-06-16 23:59:59'
    ),
    (
        @sugar_season_id,
        '당분 드래곤',
        '난이도를 선택해 길드원과 함께 전투를 시작하세요.',
        'HARD',
        200,
        '/images/boss/sugar-dragon.png',
        2400,
        300,
        'ACTIVE',
        '2026-06-10 00:00:00',
        '2026-06-16 23:59:59'
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

INSERT INTO boss_common_conditions (
    season_id,
    boss_id,
    title,
    description,
    target_type,
    threshold_value,
    threshold_unit,
    target_value,
    required_days,
    unit,
    sort_order
)
SELECT
    @sugar_season_id,
    @sugar_easy_boss_id,
    '당류 50g 이하 유지',
    '하루 당류 섭취량을 50g 이하로 유지합니다.',
    'SUGAR_UNDER_LIMIT',
    50,
    'g',
    3,
    3,
    '일',
    1
WHERE NOT EXISTS (
    SELECT 1 FROM boss_common_conditions
    WHERE boss_id = @sugar_easy_boss_id
      AND target_type = 'SUGAR_UNDER_LIMIT'
      AND sort_order = 1
);

INSERT INTO boss_common_conditions (
    season_id,
    boss_id,
    title,
    description,
    target_type,
    threshold_value,
    threshold_unit,
    target_value,
    required_days,
    unit,
    sort_order
)
SELECT
    @sugar_season_id,
    @sugar_normal_boss_id,
    '당류 50g 이하 유지',
    '하루 당류 섭취량을 50g 이하로 유지합니다.',
    'SUGAR_UNDER_LIMIT',
    50,
    'g',
    4,
    4,
    '일',
    1
WHERE NOT EXISTS (
    SELECT 1 FROM boss_common_conditions
    WHERE boss_id = @sugar_normal_boss_id
      AND target_type = 'SUGAR_UNDER_LIMIT'
      AND sort_order = 1
);

INSERT INTO boss_common_conditions (
    season_id,
    boss_id,
    title,
    description,
    target_type,
    threshold_value,
    threshold_unit,
    target_value,
    required_days,
    unit,
    sort_order
)
SELECT
    @sugar_season_id,
    @sugar_normal_boss_id,
    '가공음료 0회',
    '가공음료를 마시지 않은 날을 유지합니다.',
    'PROCESSED_DRINK_ZERO',
    0,
    '회',
    4,
    4,
    '일',
    2
WHERE NOT EXISTS (
    SELECT 1 FROM boss_common_conditions
    WHERE boss_id = @sugar_normal_boss_id
      AND target_type = 'PROCESSED_DRINK_ZERO'
      AND sort_order = 2
);

INSERT INTO boss_common_conditions (
    season_id,
    boss_id,
    title,
    description,
    target_type,
    threshold_value,
    threshold_unit,
    target_value,
    required_days,
    unit,
    sort_order
)
SELECT
    @sugar_season_id,
    @sugar_hard_boss_id,
    '당류 50g 이하 유지',
    '하루 당류 섭취량을 50g 이하로 유지합니다.',
    'SUGAR_UNDER_LIMIT',
    50,
    'g',
    4,
    4,
    '일',
    1
WHERE NOT EXISTS (
    SELECT 1 FROM boss_common_conditions
    WHERE boss_id = @sugar_hard_boss_id
      AND target_type = 'SUGAR_UNDER_LIMIT'
      AND sort_order = 1
);

INSERT INTO boss_common_conditions (
    season_id,
    boss_id,
    title,
    description,
    target_type,
    threshold_value,
    threshold_unit,
    target_value,
    required_days,
    unit,
    sort_order
)
SELECT
    @sugar_season_id,
    @sugar_hard_boss_id,
    '가공음료 0회',
    '가공음료를 마시지 않은 날을 유지합니다.',
    'PROCESSED_DRINK_ZERO',
    0,
    '회',
    4,
    4,
    '일',
    2
WHERE NOT EXISTS (
    SELECT 1 FROM boss_common_conditions
    WHERE boss_id = @sugar_hard_boss_id
      AND target_type = 'PROCESSED_DRINK_ZERO'
      AND sort_order = 2
);

INSERT INTO boss_common_conditions (
    season_id,
    boss_id,
    title,
    description,
    target_type,
    threshold_value,
    threshold_unit,
    target_value,
    required_days,
    unit,
    sort_order
)
SELECT
    @sugar_season_id,
    @sugar_hard_boss_id,
    '채소 하루 2종 이상',
    '하루에 채소를 2종 이상 기록한 날을 유지합니다.',
    'VEGETABLE_VARIETY',
    2,
    '종',
    5,
    5,
    '일',
    3
WHERE NOT EXISTS (
    SELECT 1 FROM boss_common_conditions
    WHERE boss_id = @sugar_hard_boss_id
      AND target_type = 'VEGETABLE_VARIETY'
      AND sort_order = 3
);
