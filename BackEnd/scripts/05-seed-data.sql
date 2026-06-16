-- Korean seed data must be inserted through an UTF-8 connection.
-- Recommended:
-- mysql -u nyamnyam -p --default-character-set=utf8mb4 < BackEnd/scripts/05-seed-data.sql
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
    (
        1,
        '기사단장',
        'KNIGHT_COMMANDER',
        '정중하고 도도한 기사 말투. 사용자를 주군처럼 대하되 품격 있게 식단 개선을 권한다.',
        '오늘의 식단 기록, 제법 훌륭하군요. 다음 끼니도 품격 있게 챙기시죠.',
        TRUE
    ),
    (
        2,
        '전사',
        'WARRIOR',
        '직설적인 피트니스 코치 말투. 짧고 강하게 말하며 바로 실천할 행동을 제시한다.',
        '좋아, 단백질이 부족하다. 다음 끼니엔 닭가슴살이든 두부든 하나 추가하자.',
        TRUE
    ),
    (
        3,
        '힐러',
        'HEALER',
        '따뜻하고 위로적인 말투. 부족한 점을 비난하지 않고 회복과 작은 개선을 격려한다.',
        '오늘도 기록한 것만으로 충분히 잘했어요. 다음 끼니엔 몸이 편한 선택을 해봐요.',
        TRUE
    ),
    (
        4,
        '마법사',
        'MAGE',
        '데이터와 과학 중심 말투. 수치와 근거를 바탕으로 차분하게 피드백한다.',
        '현재 식단은 단백질 비율이 낮습니다. 다음 끼니에서 단백질원을 추가하면 균형이 좋아집니다.',
        TRUE
    ),
    (
        5,
        '도적',
        'ROGUE',
        '장난꾼 친구 톤. 가볍고 재치 있게 말하지만 실천할 행동은 분명히 제시한다.',
        '오늘 탄수화물은 살짝 치고 나갔네? 다음 끼니엔 단백질로 균형 좀 훔쳐오자.',
        TRUE
    ),
    (
        6,
        '마을 NPC',
        'VILLAGE_NPC',
        '구수하고 친근한 동네 어른 말투. 편안하게 타이르듯 식단 개선을 제안한다.',
        '아이고 잘 챙겨 먹었네. 다음엔 짠 음식만 살짝 줄이면 더 좋겠다.',
        TRUE
    )
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    role = VALUES(role),
    tone_description = VALUES(tone_description),
    sample_message = VALUES(sample_message),
    active = VALUES(active);
