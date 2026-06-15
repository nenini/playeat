SET NAMES utf8mb4;
USE nyamnyam;

CREATE TABLE IF NOT EXISTS boss_battle_participants (
    participant_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    battle_id BIGINT NOT NULL,
    guild_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    guild_member_id BIGINT NULL,
    role_at_start VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    joined_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    left_at DATETIME NULL,
    snapshot_nickname VARCHAR(100) NULL,
    snapshot_profile_image_url VARCHAR(500) NULL,
    snapshot_character_id BIGINT NULL,
    snapshot_character_name VARCHAR(100) NULL,
    snapshot_character_level INT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_battle_participant_user UNIQUE (battle_id, user_id),
    CONSTRAINT fk_battle_participants_battle
        FOREIGN KEY (battle_id) REFERENCES boss_battles(battle_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_battle_participants_guild
        FOREIGN KEY (guild_id) REFERENCES guilds(guild_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_battle_participants_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_battle_participants_guild_member
        FOREIGN KEY (guild_member_id) REFERENCES guild_members(guild_member_id)
        ON DELETE SET NULL,
    CONSTRAINT fk_battle_participants_character
        FOREIGN KEY (snapshot_character_id) REFERENCES characters(character_id)
        ON DELETE SET NULL,
    INDEX idx_battle_participants_battle (battle_id),
    INDEX idx_battle_participants_guild_status (guild_id, status),
    INDEX idx_battle_participants_user_status (user_id, status),
    INDEX idx_battle_participants_battle_status (battle_id, status)
) ENGINE=InnoDB;
