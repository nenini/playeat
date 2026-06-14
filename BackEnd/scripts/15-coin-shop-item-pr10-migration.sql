SET NAMES utf8mb4;
USE nyamnyam;

CREATE TABLE IF NOT EXISTS user_coin_balances (
    user_id BIGINT PRIMARY KEY,
    balance INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_coin_balances_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS coin_transactions (
    transaction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    transaction_type VARCHAR(30) NOT NULL,
    amount INT NOT NULL,
    balance_after INT NOT NULL,
    source_type VARCHAR(30) NOT NULL,
    source_id BIGINT,
    description VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_coin_transactions_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,
    INDEX idx_coin_transactions_user_created (user_id, created_at),
    INDEX idx_coin_transactions_source (source_type, source_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS items (
    item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    item_type VARCHAR(30) NOT NULL DEFAULT 'EQUIPMENT',
    slot_type VARCHAR(30) NOT NULL,
    price INT NOT NULL DEFAULT 0,
    image_url VARCHAR(500),
    default_item BOOLEAN NOT NULL DEFAULT FALSE,
    purchasable BOOLEAN NOT NULL DEFAULT TRUE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_items_active_sort (active, sort_order),
    INDEX idx_items_slot_type (slot_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS user_items (
    user_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    acquired_type VARCHAR(30) NOT NULL,
    acquired_source_id BIGINT,
    acquired_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_items_user_item UNIQUE (user_id, item_id),
    CONSTRAINT fk_user_items_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_user_items_item
        FOREIGN KEY (item_id)
        REFERENCES items(item_id)
        ON DELETE RESTRICT,
    INDEX idx_user_items_user (user_id),
    INDEX idx_user_items_item (item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS character_equipments (
    equipment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    character_id BIGINT NOT NULL,
    slot_type VARCHAR(30) NOT NULL,
    user_item_id BIGINT NOT NULL,
    equipped_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_character_equipments_character_slot UNIQUE (character_id, slot_type),
    CONSTRAINT fk_character_equipments_character
        FOREIGN KEY (character_id)
        REFERENCES characters(character_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_character_equipments_user_item
        FOREIGN KEY (user_item_id)
        REFERENCES user_items(user_item_id)
        ON DELETE CASCADE,
    INDEX idx_character_equipments_character (character_id),
    INDEX idx_character_equipments_user_item (user_item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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
SELECT '나무막대기',
       '그냥 주운 나뭇가지',
       'EQUIPMENT',
       'HAND',
       0,
       '/images/items/wood-stick.png',
       TRUE,
       FALSE,
       TRUE,
       1
WHERE NOT EXISTS (
    SELECT 1 FROM items WHERE name = '나무막대기'
);

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
SELECT '칼',
       '번쩍이는 강철 검',
       'EQUIPMENT',
       'HAND',
       500,
       '/images/items/sword.png',
       FALSE,
       TRUE,
       TRUE,
       2
WHERE NOT EXISTS (
    SELECT 1 FROM items WHERE name = '칼'
);

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
SELECT '지팡이',
       '마법의 기운이 흐른다',
       'EQUIPMENT',
       'HAND',
       900,
       '/images/items/staff.png',
       FALSE,
       TRUE,
       TRUE,
       3
WHERE NOT EXISTS (
    SELECT 1 FROM items WHERE name = '지팡이'
);

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
SELECT '왕관',
       '길드 최고의 명예',
       'EQUIPMENT',
       'HEAD',
       1500,
       '/images/items/crown.png',
       FALSE,
       TRUE,
       TRUE,
       4
WHERE NOT EXISTS (
    SELECT 1 FROM items WHERE name = '왕관'
);
