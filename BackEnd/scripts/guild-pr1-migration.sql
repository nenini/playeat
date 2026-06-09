USE nyamnyam;

SET @has_profile_image_url = (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'users'
      AND COLUMN_NAME = 'profile_image_url'
);
SET @sql = IF(
    @has_profile_image_url = 0,
    'ALTER TABLE users ADD COLUMN profile_image_url VARCHAR(500) NULL AFTER nickname',
    'SELECT ''users.profile_image_url already exists'''
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_guild_point = (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'guilds'
      AND COLUMN_NAME = 'guild_point'
);
SET @sql = IF(
    @has_guild_point = 0,
    'ALTER TABLE guilds ADD COLUMN guild_point INT NOT NULL DEFAULT 0 AFTER max_members',
    'SELECT ''guilds.guild_point already exists'''
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

ALTER TABLE guilds
    MODIFY invite_code VARCHAR(50) NOT NULL,
    MODIFY max_members INT NOT NULL DEFAULT 30,
    MODIFY visibility VARCHAR(20) NOT NULL DEFAULT 'PRIVATE';
