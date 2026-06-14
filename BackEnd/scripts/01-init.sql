CREATE DATABASE IF NOT EXISTS nyamnyam
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE nyamnyam;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS guild_rankings;
DROP TABLE IF EXISTS guild_score_logs;
DROP TABLE IF EXISTS reward_claims;
DROP TABLE IF EXISTS quest_verifications;
DROP TABLE IF EXISTS quests;
DROP TABLE IF EXISTS boss_battle_condition_progress;
DROP TABLE IF EXISTS boss_battle_damage_logs;
DROP TABLE IF EXISTS boss_battle_conditions;
DROP TABLE IF EXISTS boss_common_conditions;
DROP TABLE IF EXISTS boss_battles;
DROP TABLE IF EXISTS bosses;
DROP TABLE IF EXISTS boss_seasons;
DROP TABLE IF EXISTS guild_chats;
DROP TABLE IF EXISTS guild_join_requests;
DROP TABLE IF EXISTS guild_notices;
DROP TABLE IF EXISTS guild_members;
DROP TABLE IF EXISTS guilds;
DROP TABLE IF EXISTS ai_reports;
DROP TABLE IF EXISTS ai_feedbacks;
DROP TABLE IF EXISTS user_badges;
DROP TABLE IF EXISTS badges;
DROP TABLE IF EXISTS xp_histories;
DROP TABLE IF EXISTS characters;
DROP TABLE IF EXISTS daily_nutrition_summaries;
DROP TABLE IF EXISTS diet_items;
DROP TABLE IF EXISTS diets;
DROP TABLE IF EXISTS food_favorites;
DROP TABLE IF EXISTS foods;
DROP TABLE IF EXISTS health_profiles;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS coaches;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE coaches (
    coach_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    role VARCHAR(50) NOT NULL,
    tone_description VARCHAR(500) NOT NULL,
    sample_message VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

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
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deactivated_at DATETIME,
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT fk_users_selected_coach
        FOREIGN KEY (selected_coach_id)
        REFERENCES coaches(coach_id)
        ON DELETE SET NULL
) ENGINE=InnoDB;

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
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_health_profiles_user UNIQUE (user_id),
    CONSTRAINT fk_health_profiles_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

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
    CONSTRAINT uk_foods_external_food_code UNIQUE (external_food_code),
    INDEX idx_foods_name (name),
    INDEX idx_foods_category (category)
) ENGINE=InnoDB;

CREATE TABLE food_favorites (
    favorite_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    food_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_food_favorites_user_food UNIQUE (user_id, food_id),
    CONSTRAINT fk_food_favorites_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_food_favorites_food
        FOREIGN KEY (food_id)
        REFERENCES foods(food_id)
        ON DELETE CASCADE,
    INDEX idx_food_favorites_user (user_id)
) ENGINE=InnoDB;

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
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_diets_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,
    INDEX idx_diets_user_eaten_at (user_id, eaten_at)
) ENGINE=InnoDB;

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
    retinol_ug DECIMAL(10,2) DEFAULT 0,
    CONSTRAINT fk_diet_items_diet
        FOREIGN KEY (diet_id)
        REFERENCES diets(diet_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_diet_items_food
        FOREIGN KEY (food_id)
        REFERENCES foods(food_id)
        ON DELETE RESTRICT,
    INDEX idx_diet_items_diet (diet_id),
    INDEX idx_diet_items_food (food_id)
) ENGINE=InnoDB;

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
    total_fiber_g DECIMAL(10,2) DEFAULT 0,
    total_iron_mg DECIMAL(10,2) DEFAULT 0,
    total_phosphorus_mg DECIMAL(10,2) DEFAULT 0,
    total_potassium_mg DECIMAL(10,2) DEFAULT 0,
    total_vitamin_a_ug_rae DECIMAL(10,2) DEFAULT 0,
    total_beta_carotene_ug DECIMAL(10,2) DEFAULT 0,
    total_retinol_ug DECIMAL(10,2) DEFAULT 0,
    health_score INT DEFAULT 0,
    streak_counted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_daily_summaries_user_date UNIQUE (user_id, summary_date),
    CONSTRAINT fk_daily_summaries_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,
    INDEX idx_daily_summaries_date (summary_date)
) ENGINE=InnoDB;

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
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_characters_user UNIQUE (user_id),
    CONSTRAINT fk_characters_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE xp_histories (
    xp_history_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    character_id BIGINT NOT NULL,
    source_type VARCHAR(30) NOT NULL,
    source_id BIGINT NOT NULL,
    xp_amount INT NOT NULL,
    reason VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_xp_histories_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_xp_histories_character
        FOREIGN KEY (character_id)
        REFERENCES characters(character_id)
        ON DELETE CASCADE,
    CONSTRAINT uq_xp_source UNIQUE (character_id, source_type, source_id),
    INDEX idx_xp_histories_user_created (user_id, created_at)
) ENGINE=InnoDB;

CREATE TABLE badges (
    badge_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    image_url VARCHAR(500),
    condition_type VARCHAR(50),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE user_badges (
    user_badge_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    badge_id BIGINT NOT NULL,
    source_type VARCHAR(30),
    source_id BIGINT,
    earned_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_badges_user_badge UNIQUE (user_id, badge_id),
    CONSTRAINT fk_user_badges_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_user_badges_badge
        FOREIGN KEY (badge_id)
        REFERENCES badges(badge_id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE ai_feedbacks (
    feedback_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    diet_id BIGINT NOT NULL,
    coach_id BIGINT,
    message TEXT NOT NULL,
    model_name VARCHAR(100),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ai_feedbacks_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_ai_feedbacks_diet
        FOREIGN KEY (diet_id)
        REFERENCES diets(diet_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_ai_feedbacks_coach
        FOREIGN KEY (coach_id)
        REFERENCES coaches(coach_id)
        ON DELETE SET NULL,
    INDEX idx_ai_feedbacks_diet (diet_id)
) ENGINE=InnoDB;

CREATE TABLE ai_reports (
    report_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    report_type VARCHAR(20) NOT NULL,
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    summary TEXT,
    strengths_json JSON,
    warnings_json JSON,
    next_action TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_ai_reports_user_period UNIQUE (user_id, report_type, period_start, period_end),
    CONSTRAINT fk_ai_reports_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,
    INDEX idx_ai_reports_user_type (user_id, report_type)
) ENGINE=InnoDB;

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
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_guilds_name UNIQUE (name),
    CONSTRAINT uk_guilds_invite_code UNIQUE (invite_code),
    CONSTRAINT fk_guilds_owner
        FOREIGN KEY (owner_user_id)
        REFERENCES users(user_id)
        ON DELETE RESTRICT,
    INDEX idx_guilds_status_visibility (status, visibility)
) ENGINE=InnoDB;

CREATE TABLE guild_members (
    guild_member_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    guild_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
    joined_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    left_at DATETIME,
    CONSTRAINT uk_guild_members_guild_user UNIQUE (guild_id, user_id),
    CONSTRAINT fk_guild_members_guild
        FOREIGN KEY (guild_id)
        REFERENCES guilds(guild_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_guild_members_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,
    INDEX idx_guild_members_user (user_id)
) ENGINE=InnoDB;

CREATE TABLE guild_notices (
    notice_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    guild_id BIGINT NOT NULL,
    writer_user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_guild_notices_guild
        FOREIGN KEY (guild_id)
        REFERENCES guilds(guild_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_guild_notices_writer
        FOREIGN KEY (writer_user_id)
        REFERENCES users(user_id)
        ON DELETE RESTRICT,
    INDEX idx_guild_notices_guild_created (guild_id, created_at)
) ENGINE=InnoDB;

CREATE TABLE guild_join_requests (
    request_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    guild_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    handled_by BIGINT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    handled_at DATETIME,
    CONSTRAINT fk_join_requests_guild
        FOREIGN KEY (guild_id)
        REFERENCES guilds(guild_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_join_requests_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_join_requests_handled_by
        FOREIGN KEY (handled_by)
        REFERENCES users(user_id)
        ON DELETE SET NULL,
    INDEX idx_join_requests_guild_status (guild_id, status),
    INDEX idx_join_requests_user_status (user_id, status),
    INDEX idx_join_requests_guild_user_status (guild_id, user_id, status)
) ENGINE=InnoDB;

CREATE TABLE guild_chats (
    chat_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    guild_id BIGINT NOT NULL,
    sender_user_id BIGINT,
    message_type VARCHAR(20) NOT NULL DEFAULT 'USER',
    content VARCHAR(1000) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at DATETIME,
    CONSTRAINT fk_guild_chats_guild
        FOREIGN KEY (guild_id)
        REFERENCES guilds(guild_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_guild_chats_sender
        FOREIGN KEY (sender_user_id)
        REFERENCES users(user_id)
        ON DELETE SET NULL,
    INDEX idx_guild_chats_guild_created (guild_id, created_at),
    INDEX idx_guild_chats_guild_chat_id (guild_id, chat_id),
    INDEX idx_guild_chats_sender (sender_user_id)
) ENGINE=InnoDB;

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
    CONSTRAINT uk_boss_seasons_code UNIQUE (season_code),
    INDEX idx_boss_seasons_active_dates (active, start_date, end_date)
) ENGINE=InnoDB;

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
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_bosses_season
        FOREIGN KEY (season_id)
        REFERENCES boss_seasons(season_id)
        ON DELETE CASCADE,
    CONSTRAINT uk_bosses_season_difficulty UNIQUE (season_id, difficulty),
    INDEX idx_bosses_season (season_id),
    INDEX idx_bosses_difficulty (difficulty),
    INDEX idx_bosses_status_period (status, starts_at, ends_at)
) ENGINE=InnoDB;

CREATE TABLE boss_common_conditions (
    condition_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    season_id BIGINT NOT NULL,
    boss_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    target_type VARCHAR(50) NOT NULL,
    threshold_value DECIMAL(10,2),
    threshold_unit VARCHAR(50),
    target_value INT NOT NULL,
    required_days INT,
    unit VARCHAR(50),
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_boss_common_conditions_season
        FOREIGN KEY (season_id)
        REFERENCES boss_seasons(season_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_boss_common_conditions_boss
        FOREIGN KEY (boss_id)
        REFERENCES bosses(boss_id)
        ON DELETE CASCADE,
    INDEX idx_boss_common_conditions_season (season_id),
    INDEX idx_boss_common_conditions_boss_sort (boss_id, sort_order),
    INDEX idx_boss_common_conditions_target_type (target_type)
) ENGINE=InnoDB;

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
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_boss_battles_guild
        FOREIGN KEY (guild_id)
        REFERENCES guilds(guild_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_boss_battles_boss
        FOREIGN KEY (boss_id)
        REFERENCES bosses(boss_id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_boss_battles_season
        FOREIGN KEY (season_id)
        REFERENCES boss_seasons(season_id)
        ON DELETE RESTRICT,
    INDEX idx_boss_battles_guild_status (guild_id, status),
    INDEX idx_boss_battles_guild_season (guild_id, season_id),
    INDEX idx_boss_battles_boss_id (boss_id)
) ENGINE=InnoDB;

CREATE TABLE boss_battle_conditions (
    battle_condition_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    battle_id BIGINT NOT NULL,
    condition_id BIGINT,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    target_type VARCHAR(50) NOT NULL,
    threshold_value DECIMAL(10,2),
    threshold_unit VARCHAR(50),
    target_value INT NOT NULL,
    required_days INT,
    current_value INT NOT NULL DEFAULT 0,
    damage INT NOT NULL DEFAULT 0,
    unit VARCHAR(50),
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    completed_at DATETIME,
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_battle_conditions_battle
        FOREIGN KEY (battle_id)
        REFERENCES boss_battles(battle_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_battle_conditions_common
        FOREIGN KEY (condition_id)
        REFERENCES boss_common_conditions(condition_id)
        ON DELETE SET NULL,
    INDEX idx_battle_conditions_battle (battle_id),
    INDEX idx_battle_conditions_completed (battle_id, completed)
) ENGINE=InnoDB;

CREATE TABLE boss_battle_damage_logs (
    damage_log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    battle_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    damage INT NOT NULL,
    source_type VARCHAR(50) NOT NULL,
    source_id BIGINT,
    description VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_damage_logs_battle
        FOREIGN KEY (battle_id)
        REFERENCES boss_battles(battle_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_damage_logs_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,
    INDEX idx_damage_logs_battle_created (battle_id, created_at),
    INDEX idx_damage_logs_battle_user (battle_id, user_id)
) ENGINE=InnoDB;

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
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_condition_progress_condition_user UNIQUE (battle_condition_id, user_id),
    CONSTRAINT fk_condition_progress_battle_condition
        FOREIGN KEY (battle_condition_id)
        REFERENCES boss_battle_conditions(battle_condition_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_condition_progress_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,
    INDEX idx_condition_progress_user_status (user_id, status),
    INDEX idx_condition_progress_condition_status (battle_condition_id, status)
) ENGINE=InnoDB;

CREATE TABLE quests (
    quest_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    battle_id BIGINT NOT NULL,
    guild_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    quest_type VARCHAR(50) NOT NULL,
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
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_quests_battle_user UNIQUE (battle_id, user_id),
    CONSTRAINT fk_quests_battle
        FOREIGN KEY (battle_id)
        REFERENCES boss_battles(battle_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_quests_guild
        FOREIGN KEY (guild_id)
        REFERENCES guilds(guild_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_quests_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,
    INDEX idx_quests_battle_user (battle_id, user_id),
    INDEX idx_quests_battle_status (battle_id, status),
    INDEX idx_quests_user_status (user_id, status),
    INDEX idx_quests_guild_battle (guild_id, battle_id)
) ENGINE=InnoDB;

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
    CONSTRAINT uk_quest_verifications_quest UNIQUE (quest_id),
    CONSTRAINT fk_quest_verifications_quest
        FOREIGN KEY (quest_id)
        REFERENCES quests(quest_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_quest_verifications_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_quest_verifications_battle
        FOREIGN KEY (battle_id)
        REFERENCES boss_battles(battle_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_quest_verifications_summary
        FOREIGN KEY (summary_id)
        REFERENCES daily_nutrition_summaries(summary_id)
        ON DELETE SET NULL,
    CONSTRAINT fk_quest_verifications_diet
        FOREIGN KEY (diet_id)
        REFERENCES diets(diet_id)
        ON DELETE SET NULL,
    INDEX idx_quest_verifications_quest (quest_id),
    INDEX idx_quest_verifications_user_date (user_id, verified_date),
    INDEX idx_quest_verifications_battle (battle_id)
) ENGINE=InnoDB;

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
    CONSTRAINT uk_reward_claims_user_source UNIQUE (user_id, source_type, source_id),
    CONSTRAINT fk_reward_claims_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_reward_claims_badge
        FOREIGN KEY (badge_id)
        REFERENCES badges(badge_id)
        ON DELETE SET NULL
) ENGINE=InnoDB;

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
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_guild_score_logs_guild
        FOREIGN KEY (guild_id)
        REFERENCES guilds(guild_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_guild_score_logs_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE SET NULL,
    CONSTRAINT fk_guild_score_logs_battle
        FOREIGN KEY (battle_id)
        REFERENCES boss_battles(battle_id)
        ON DELETE SET NULL,
    INDEX idx_guild_score_logs_guild_date (guild_id, score_date),
    INDEX idx_guild_score_logs_battle (battle_id),
    INDEX idx_guild_score_logs_source (source_type, source_id),
    INDEX idx_guild_score_logs_date (score_date)
) ENGINE=InnoDB;

CREATE TABLE guild_rankings (
    ranking_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    guild_id BIGINT NOT NULL,
    boss_id BIGINT NOT NULL,
    season_id BIGINT NOT NULL,
    rank_no INT,
    score INT NOT NULL DEFAULT 0,
    clear_count INT NOT NULL DEFAULT 0,
    total_damage INT NOT NULL DEFAULT 0,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_guild_rankings_guild_boss_season UNIQUE (guild_id, boss_id, season_id),
    CONSTRAINT fk_guild_rankings_guild
        FOREIGN KEY (guild_id)
        REFERENCES guilds(guild_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_guild_rankings_boss
        FOREIGN KEY (boss_id)
        REFERENCES bosses(boss_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_guild_rankings_season
        FOREIGN KEY (season_id)
        REFERENCES boss_seasons(season_id)
        ON DELETE CASCADE,
    INDEX idx_guild_rankings_season_rank (season_id, rank_no)
) ENGINE=InnoDB;
