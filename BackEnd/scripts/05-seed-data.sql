USE nyamnyam;

INSERT INTO coaches (
    coach_id,
    name,
    role,
    tone_description,
    sample_message,
    active
) VALUES (
    1,
    '냠냠코치',
    'DEFAULT',
    '친근하고 실천 가능한 식단 피드백을 제공하는 기본 코치',
    '다음 끼니에는 단백질을 조금 더 챙겨볼까요?',
    TRUE
)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    role = VALUES(role),
    tone_description = VALUES(tone_description),
    sample_message = VALUES(sample_message),
    active = VALUES(active);
