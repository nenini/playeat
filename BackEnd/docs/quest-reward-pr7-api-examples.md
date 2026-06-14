# PR7 Quest Verification and Reward API Examples

## Quest Verify

`POST /api/v1/quests/{questId}/verify`

로그인한 사용자가 본인 퀘스트를 검증한다. 현재 PR7에서는 `RECORD_DIET`만 지원하며, 오늘 날짜에 `diets.eaten_at` 기준 식단 기록이 1개 이상 있으면 성공한다.

```json
{
  "questId": 1,
  "battleId": 1,
  "guildId": 1,
  "status": "COMPLETED",
  "verified": true,
  "damage": 220,
  "currentHp": 880,
  "totalDamage": 220,
  "bossBattleStatus": "IN_PROGRESS",
  "message": "퀘스트 검증에 성공했습니다."
}
```

검증 성공 시 같은 트랜잭션에서 아래 처리를 수행한다.

- `quests.status = COMPLETED`
- `quest_verifications` 저장 (`diet_id` 저장, `summary_id`는 null 허용)
- `boss_battles.current_hp` 감소 및 `total_damage` 증가
- `boss_battle_damage_logs` 저장
- `guild_score_logs`에 `QUEST_COMPLETE` 점수 저장
- `SUGAR_UNDER_LIMIT` 공통 조건 재계산
- HP가 0이고 공통 조건이 모두 완료되면 `boss_battles.status = DEFEATED`

## Quest Reward

`POST /api/v1/quests/{questId}/reward`

완료된 개인 퀘스트의 XP와 코인 보상을 수령한다. `reward_claims`의 `(user_id, source_type, source_id)` unique 제약으로 중복 수령을 막는다.

```json
{
  "sourceType": "QUEST",
  "sourceId": 1,
  "xpAmount": 30,
  "coinAmount": 10,
  "claimedAt": "2026-06-10T10:30:00"
}
```

보상 수령 시 같은 트랜잭션에서 `reward_claims` 저장, `CharacterGrowthService.addXp`, `CoinService.earn`, `quests.status = REWARDED`를 처리한다.

## Boss Battle Reward

`POST /api/v1/boss-battles/{battleId}/reward`

`DEFEATED` 상태의 보스전 보상을 길드원이 1회 수령한다.

```json
{
  "sourceType": "BOSS_BATTLE",
  "sourceId": 1,
  "xpAmount": 800,
  "coinAmount": 100,
  "claimedAt": "2026-06-10T10:30:00"
}
```

보스전 보상도 사용자별 1회만 가능하며 `reward_claims`에 `sourceType = BOSS_BATTLE`, `sourceId = battleId`로 기록한다.

## Common Condition Policy

PR7에서 실제 계산하는 공통 조건은 `SUGAR_UNDER_LIMIT`다.

- 보스전 시작일부터 오늘까지 active 길드원의 `daily_nutrition_summaries.total_sugar_g <= threshold_value`인 member-date 수를 계산한다.
- `current_value >= target_value`가 되면 조건을 완료 처리한다.
- 조건 완료 시 `boss_battle_conditions.damage`만큼 보스 HP를 감소시키고 `COMMON_CONDITION_COMPLETE` 점수 로그를 남긴다.

`PROCESSED_DRINK_ZERO`, `VEGETABLE_VARIETY` 등은 이후 식품 카테고리 검증 품질이 갖춰지면 같은 구조로 확장한다.

## Migration

```bash
docker exec -i nyamnyam-mysql mysql --default-character-set=utf8mb4 -u root -p비밀번호 nyamnyam < scripts/15-quest-verification-reward-pr7-migration.sql
```
