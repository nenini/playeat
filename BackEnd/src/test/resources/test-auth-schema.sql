DROP TABLE IF EXISTS reward_claims;
DROP TABLE IF EXISTS quest_verifications;
DROP TABLE IF EXISTS quests;
DROP TABLE IF EXISTS quest_templates;
DROP TABLE IF EXISTS guild_score_logs;
DROP TABLE IF EXISTS boss_battle_condition_progress;
DROP TABLE IF EXISTS boss_battle_damage_logs;
DROP TABLE IF EXISTS boss_battle_conditions;
DROP TABLE IF EXISTS boss_battle_participants;
DROP TABLE IF EXISTS boss_battles;
DROP TABLE IF EXISTS boss_common_conditions;
DROP TABLE IF EXISTS boss_condition_templates;
DROP TABLE IF EXISTS bosses;
DROP TABLE IF EXISTS boss_seasons;
DROP TABLE IF EXISTS guild_join_requests;
DROP TABLE IF EXISTS guild_chats;
DROP TABLE IF EXISTS guild_notices;
DROP TABLE IF EXISTS guild_members;
DROP TABLE IF EXISTS guilds;
DROP TABLE IF EXISTS xp_histories;
DROP TABLE IF EXISTS characters;
DROP TABLE IF EXISTS daily_nutrition_summaries;
DROP TABLE IF EXISTS diet_items;
DROP TABLE IF EXISTS diets;
DROP TABLE IF EXISTS food_favorites;
DROP TABLE IF EXISTS foods;
DROP TABLE IF EXISTS nutrition_reference_standards;
DROP TABLE IF EXISTS health_profiles;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    nickname VARCHAR(50) NOT NULL,
    profile_image_url VARCHAR(500),
    selected_coach_id BIGINT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    onboarding_completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deactivated_at DATETIME,
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE health_profiles (
    health_profile_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    height_cm DECIMAL(5,2),
    weight_kg DECIMAL(5,2),
    target_weight_kg DECIMAL(5,2),
    birth_date DATE,
    gender VARCHAR(20),
    health_goal VARCHAR(30),
    activity_level VARCHAR(30),
    diet_styles_json JSON,
    restricted_foods_json JSON,
    allergies_json JSON,
    target_calories DECIMAL(8,2),
    target_protein_g DECIMAL(8,2),
    target_carbs_g DECIMAL(8,2),
    target_fat_g DECIMAL(8,2),
    target_sodium_mg DECIMAL(8,2),
    target_fiber_g DECIMAL(8,2),
    nutrition_standard_version VARCHAR(30),
    nutrition_target_calculated_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_health_profiles_user UNIQUE (user_id)
);

CREATE TABLE nutrition_reference_standards (
    standard_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    standard_version VARCHAR(30) NOT NULL,
    source_name VARCHAR(100) NOT NULL,
    gender VARCHAR(20) NOT NULL,
    age_min INT NOT NULL,
    age_max INT NOT NULL,
    sodium_mg DECIMAL(8,2) NOT NULL,
    fiber_g DECIMAL(8,2) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO nutrition_reference_standards (
    standard_version,
    source_name,
    gender,
    age_min,
    age_max,
    sodium_mg,
    fiber_g
)
VALUES ('KDRI_2020', '2020 Korean Dietary Reference Intakes', 'ALL', 1, 120, 2000, 25);

CREATE TABLE foods (
    food_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    external_food_code VARCHAR(100),
    name VARCHAR(200) NOT NULL,
    brand VARCHAR(100),
    category VARCHAR(100),
    nutrition_basis_amount DECIMAL(8,2) NOT NULL DEFAULT 100,
    nutrition_basis_unit VARCHAR(10) NOT NULL DEFAULT 'g',
    serving_amount DECIMAL(8,2),
    serving_unit VARCHAR(10),
    gram_per_piece DECIMAL(8,4),
    calories DECIMAL(10,2) DEFAULT 0,
    protein_g DECIMAL(10,2) DEFAULT 0,
    carbs_g DECIMAL(10,2) DEFAULT 0,
    fat_g DECIMAL(10,2) DEFAULT 0,
    sugar_g DECIMAL(10,2) DEFAULT 0,
    sodium_mg DECIMAL(10,2) DEFAULT 0,
    fiber_g DECIMAL(10,2) DEFAULT 0,
    iron_mg DECIMAL(10,2) DEFAULT 0,
    phosphorus_mg DECIMAL(10,2) DEFAULT 0,
    potassium_mg DECIMAL(10,2) DEFAULT 0,
    vitamin_a_ug_rae DECIMAL(10,2) DEFAULT 0,
    beta_carotene_ug DECIMAL(10,2) DEFAULT 0,
    retinol_ug DECIMAL(10,2) DEFAULT 0,
    source VARCHAR(50),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_foods_external_food_code UNIQUE (external_food_code)
);

CREATE INDEX idx_foods_name ON foods (name);
CREATE INDEX idx_foods_category ON foods (category);

CREATE TABLE food_favorites (
    favorite_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    food_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_food_favorites_user_food UNIQUE (user_id, food_id)
);

CREATE TABLE diets (
    diet_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    meal_type VARCHAR(20) NOT NULL,
    eaten_at DATETIME NOT NULL,
    memo TEXT,
    total_calories DECIMAL(10,2) DEFAULT 0,
    total_protein_g DECIMAL(10,2) DEFAULT 0,
    total_carbs_g DECIMAL(10,2) DEFAULT 0,
    total_fat_g DECIMAL(10,2) DEFAULT 0,
    total_sugar_g DECIMAL(10,2) DEFAULT 0,
    total_sodium_mg DECIMAL(10,2) DEFAULT 0,
    total_fiber_g DECIMAL(10,2) DEFAULT 0,
    total_iron_mg DECIMAL(10,2) DEFAULT 0,
    total_phosphorus_mg DECIMAL(10,2) DEFAULT 0,
    total_potassium_mg DECIMAL(10,2) DEFAULT 0,
    total_vitamin_a_ug_rae DECIMAL(10,2) DEFAULT 0,
    total_beta_carotene_ug DECIMAL(10,2) DEFAULT 0,
    total_retinol_ug DECIMAL(10,2) DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_diets_user_eaten_at ON diets (user_id, eaten_at);

CREATE TABLE diet_items (
    diet_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    diet_id BIGINT NOT NULL,
    food_id BIGINT NOT NULL,
    input_amount DECIMAL(8,2) NOT NULL,
    input_unit VARCHAR(10) NOT NULL,
    amount_g DECIMAL(8,2),
    amount_ml DECIMAL(8,2),
    calories DECIMAL(10,2) DEFAULT 0,
    protein_g DECIMAL(10,2) DEFAULT 0,
    carbs_g DECIMAL(10,2) DEFAULT 0,
    fat_g DECIMAL(10,2) DEFAULT 0,
    sugar_g DECIMAL(10,2) DEFAULT 0,
    sodium_mg DECIMAL(10,2) DEFAULT 0,
    fiber_g DECIMAL(10,2) DEFAULT 0,
    iron_mg DECIMAL(10,2) DEFAULT 0,
    phosphorus_mg DECIMAL(10,2) DEFAULT 0,
    potassium_mg DECIMAL(10,2) DEFAULT 0,
    vitamin_a_ug_rae DECIMAL(10,2) DEFAULT 0,
    beta_carotene_ug DECIMAL(10,2) DEFAULT 0,
    retinol_ug DECIMAL(10,2) DEFAULT 0
);

CREATE INDEX idx_diet_items_diet ON diet_items (diet_id);
CREATE INDEX idx_diet_items_food ON diet_items (food_id);

CREATE TABLE daily_nutrition_summaries (
    summary_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    summary_date DATE NOT NULL,
    total_calories DECIMAL(10,2) DEFAULT 0,
    total_protein_g DECIMAL(10,2) DEFAULT 0,
    total_carbs_g DECIMAL(10,2) DEFAULT 0,
    total_fat_g DECIMAL(10,2) DEFAULT 0,
    total_sugar_g DECIMAL(10,2) DEFAULT 0,
    total_sodium_mg DECIMAL(10,2) DEFAULT 0,
    health_score INT DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_daily_summaries_user_date UNIQUE (user_id, summary_date)
);

CREATE TABLE boss_seasons (
    season_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    season_code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(1000),
    target_nutrient VARCHAR(50),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_boss_seasons_code UNIQUE (season_code)
);

CREATE TABLE bosses (
    boss_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    season_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    difficulty VARCHAR(20) NOT NULL,
    max_hp INT NOT NULL,
    image_url VARCHAR(500),
    reward_exp INT NOT NULL DEFAULT 0,
    reward_coin INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    starts_at DATETIME,
    ends_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_bosses_season_difficulty UNIQUE (season_id, difficulty)
);

CREATE TABLE boss_condition_templates (
    condition_template_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(500) NOT NULL,
    target_type VARCHAR(50) NOT NULL,
    condition_category VARCHAR(50) NOT NULL,
    metric_type VARCHAR(50) NOT NULL,
    comparison_type VARCHAR(50) NOT NULL,
    aggregation_type VARCHAR(50) NOT NULL,
    evaluation_scope VARCHAR(50) NOT NULL,
    threshold_value DECIMAL(10,2),
    threshold_min_value DECIMAL(10,2),
    threshold_max_value DECIMAL(10,2),
    threshold_unit VARCHAR(30),
    target_value INT NOT NULL DEFAULT 1,
    required_days INT,
    unit VARCHAR(30) NOT NULL,
    difficulty VARCHAR(20) NOT NULL DEFAULT 'EASY',
    required_for_clear BOOLEAN NOT NULL DEFAULT TRUE,
    verification_supported BOOLEAN NOT NULL DEFAULT TRUE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE boss_common_conditions (
    condition_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    condition_template_id BIGINT,
    season_id BIGINT NOT NULL,
    boss_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    target_type VARCHAR(50) NOT NULL,
    condition_category VARCHAR(50),
    metric_type VARCHAR(50),
    comparison_type VARCHAR(50),
    aggregation_type VARCHAR(50),
    evaluation_scope VARCHAR(50),
    threshold_value DECIMAL(10,2),
    threshold_min_value DECIMAL(10,2),
    threshold_max_value DECIMAL(10,2),
    threshold_unit VARCHAR(50),
    target_value INT NOT NULL,
    required_days INT,
    required_for_clear BOOLEAN NOT NULL DEFAULT TRUE,
    verification_supported BOOLEAN NOT NULL DEFAULT TRUE,
    unit VARCHAR(50),
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE characters (
    character_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    level INT NOT NULL DEFAULT 1,
    xp INT NOT NULL DEFAULT 0,
    stage VARCHAR(30) NOT NULL DEFAULT 'EGG',
    mood VARCHAR(30) NOT NULL DEFAULT 'NORMAL',
    appearance_type VARCHAR(30) NOT NULL DEFAULT 'NORMAL',
    streak_days INT NOT NULL DEFAULT 0,
    best_streak_days INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_characters_user UNIQUE (user_id)
);

CREATE TABLE xp_histories (
    xp_history_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    character_id BIGINT NOT NULL,
    source_type VARCHAR(30) NOT NULL,
    source_id BIGINT NOT NULL,
    xp_amount INT NOT NULL,
    reason VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_xp_source UNIQUE (character_id, source_type, source_id)
);

CREATE TABLE guilds (
    guild_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    invite_code VARCHAR(50) NOT NULL,
    owner_user_id BIGINT NOT NULL,
    max_members INT NOT NULL DEFAULT 30,
    guild_point INT NOT NULL DEFAULT 0,
    visibility VARCHAR(20) NOT NULL DEFAULT 'PRIVATE',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_guilds_name UNIQUE (name),
    CONSTRAINT uk_guilds_invite_code UNIQUE (invite_code)
);

CREATE TABLE guild_members (
    guild_member_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    guild_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
    joined_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    left_at DATETIME,
    CONSTRAINT uk_guild_members_guild_user UNIQUE (guild_id, user_id)
);

CREATE TABLE guild_notices (
    notice_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    guild_id BIGINT NOT NULL,
    writer_user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE guild_join_requests (
    request_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    guild_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    handled_by BIGINT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    handled_at DATETIME
);

CREATE INDEX idx_join_requests_guild_status ON guild_join_requests (guild_id, status);
CREATE INDEX idx_join_requests_user_status ON guild_join_requests (user_id, status);
CREATE INDEX idx_join_requests_guild_user_status ON guild_join_requests (guild_id, user_id, status);

CREATE TABLE guild_chats (
    chat_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    guild_id BIGINT NOT NULL,
    sender_user_id BIGINT,
    message_type VARCHAR(20) NOT NULL DEFAULT 'USER',
    content VARCHAR(1000) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at DATETIME
);

CREATE INDEX idx_guild_chats_guild_created ON guild_chats (guild_id, created_at);
CREATE INDEX idx_guild_chats_guild_chat_id ON guild_chats (guild_id, chat_id);
CREATE INDEX idx_guild_chats_sender ON guild_chats (sender_user_id);

CREATE TABLE boss_battles (
    battle_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    guild_id BIGINT NOT NULL,
    boss_id BIGINT NOT NULL,
    season_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',
    max_hp INT NOT NULL,
    current_hp INT NOT NULL,
    total_damage INT NOT NULL DEFAULT 0,
    started_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ended_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE boss_battle_participants (
    participant_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    battle_id BIGINT NOT NULL,
    guild_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    guild_member_id BIGINT,
    role_at_start VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    joined_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    left_at DATETIME,
    snapshot_nickname VARCHAR(100),
    snapshot_profile_image_url VARCHAR(500),
    snapshot_character_id BIGINT,
    snapshot_character_name VARCHAR(100),
    snapshot_character_level INT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_battle_participant_user UNIQUE (battle_id, user_id)
);

CREATE INDEX idx_battle_participants_battle ON boss_battle_participants (battle_id);
CREATE INDEX idx_battle_participants_guild_status ON boss_battle_participants (guild_id, status);
CREATE INDEX idx_battle_participants_user_status ON boss_battle_participants (user_id, status);
CREATE INDEX idx_battle_participants_battle_status ON boss_battle_participants (battle_id, status);

CREATE TABLE boss_battle_conditions (
    battle_condition_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    battle_id BIGINT NOT NULL,
    condition_id BIGINT,
    condition_template_id BIGINT,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    target_type VARCHAR(50) NOT NULL,
    condition_category VARCHAR(50),
    metric_type VARCHAR(50),
    comparison_type VARCHAR(50),
    aggregation_type VARCHAR(50),
    evaluation_scope VARCHAR(50),
    threshold_value DECIMAL(10,2),
    threshold_min_value DECIMAL(10,2),
    threshold_max_value DECIMAL(10,2),
    threshold_unit VARCHAR(50),
    target_value INT NOT NULL,
    required_days INT,
    current_value INT NOT NULL DEFAULT 0,
    damage INT NOT NULL DEFAULT 0,
    unit VARCHAR(50),
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    required_for_clear BOOLEAN NOT NULL DEFAULT TRUE,
    verification_supported BOOLEAN NOT NULL DEFAULT TRUE,
    completed_at DATETIME,
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE boss_battle_damage_logs (
    damage_log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    battle_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    damage INT NOT NULL,
    source_type VARCHAR(50) NOT NULL,
    source_id BIGINT,
    description VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_boss_battles_guild_status ON boss_battles (guild_id, status);
CREATE INDEX idx_boss_battles_guild_season ON boss_battles (guild_id, season_id);
CREATE INDEX idx_boss_battles_boss_id ON boss_battles (boss_id);
CREATE INDEX idx_battle_conditions_battle ON boss_battle_conditions (battle_id);
CREATE INDEX idx_battle_conditions_completed ON boss_battle_conditions (battle_id, completed);
CREATE INDEX idx_damage_logs_battle_created ON boss_battle_damage_logs (battle_id, created_at);
CREATE INDEX idx_damage_logs_battle_user ON boss_battle_damage_logs (battle_id, user_id);

CREATE TABLE boss_battle_condition_progress (
    progress_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    battle_condition_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    current_streak_days INT NOT NULL DEFAULT 0,
    best_streak_days INT NOT NULL DEFAULT 0,
    progress_value DECIMAL(12,4),
    last_checked_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',
    completed_at DATETIME,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_condition_progress_condition_user UNIQUE (battle_condition_id, user_id)
);

CREATE TABLE quest_templates (
    template_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(500) NOT NULL,
    quest_type VARCHAR(50) NOT NULL,
    condition_category VARCHAR(50) NOT NULL,
    metric_type VARCHAR(50) NOT NULL,
    comparison_type VARCHAR(50) NOT NULL,
    aggregation_type VARCHAR(50) NOT NULL,
    evaluation_scope VARCHAR(50) NOT NULL,
    threshold_value DECIMAL(10,2),
    threshold_min_value DECIMAL(10,2),
    threshold_max_value DECIMAL(10,2),
    threshold_unit VARCHAR(30),
    target_value INT NOT NULL DEFAULT 1,
    unit VARCHAR(30) NOT NULL,
    damage INT NOT NULL DEFAULT 100,
    reward_exp INT NOT NULL DEFAULT 30,
    reward_coin INT NOT NULL DEFAULT 10,
    difficulty VARCHAR(20) NOT NULL DEFAULT 'EASY',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE quests (
    quest_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    battle_id BIGINT NOT NULL,
    guild_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    quest_template_id BIGINT,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    quest_type VARCHAR(50) NOT NULL,
    condition_category VARCHAR(50),
    metric_type VARCHAR(50),
    comparison_type VARCHAR(50),
    aggregation_type VARCHAR(50),
    evaluation_scope VARCHAR(50),
    threshold_value DECIMAL(10,2),
    threshold_min_value DECIMAL(10,2),
    threshold_max_value DECIMAL(10,2),
    threshold_unit VARCHAR(30),
    target_value INT NOT NULL,
    current_value INT NOT NULL DEFAULT 0,
    unit VARCHAR(50),
    damage INT NOT NULL DEFAULT 0,
    reward_exp INT NOT NULL DEFAULT 0,
    reward_coin INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',
    source_type VARCHAR(30) NOT NULL DEFAULT 'PLACEHOLDER',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at DATETIME,
    rewarded_at DATETIME,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_quests_battle_user UNIQUE (battle_id, user_id)
);

CREATE INDEX idx_quests_battle_user ON quests (battle_id, user_id);
CREATE INDEX idx_quests_battle_status ON quests (battle_id, status);
CREATE INDEX idx_quests_user_status ON quests (user_id, status);
CREATE INDEX idx_quests_guild_battle ON quests (guild_id, battle_id);

CREATE TABLE quest_verifications (
    verification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quest_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    battle_id BIGINT NOT NULL,
    summary_id BIGINT,
    diet_id BIGINT,
    quest_type VARCHAR(50) NOT NULL,
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    damage_amount INT NOT NULL DEFAULT 0,
    message VARCHAR(500),
    verified_date DATE,
    verified_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_quest_verifications_quest UNIQUE (quest_id)
);

CREATE INDEX idx_quest_verifications_quest ON quest_verifications (quest_id);
CREATE INDEX idx_quest_verifications_user_date ON quest_verifications (user_id, verified_date);
CREATE INDEX idx_quest_verifications_battle ON quest_verifications (battle_id);

CREATE TABLE reward_claims (
    reward_claim_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    source_type VARCHAR(30) NOT NULL,
    source_id BIGINT NOT NULL,
    xp_amount INT NOT NULL DEFAULT 0,
    badge_id BIGINT,
    guild_point INT NOT NULL DEFAULT 0,
    coin_amount INT NOT NULL DEFAULT 0,
    claimed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_reward_claims_user_source UNIQUE (user_id, source_type, source_id)
);

CREATE TABLE guild_score_logs (
    score_log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    guild_id BIGINT NOT NULL,
    user_id BIGINT,
    battle_id BIGINT,
    source_type VARCHAR(50) NOT NULL,
    source_id BIGINT,
    score INT NOT NULL,
    score_date DATE NOT NULL,
    description VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_guild_score_logs_guild_date ON guild_score_logs (guild_id, score_date);
CREATE INDEX idx_guild_score_logs_battle ON guild_score_logs (battle_id);
CREATE INDEX idx_guild_score_logs_source ON guild_score_logs (source_type, source_id);
CREATE INDEX idx_guild_score_logs_date ON guild_score_logs (score_date);
