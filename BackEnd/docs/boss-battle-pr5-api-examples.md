# Boss Battle PR5 API Examples

## 데미지 밸런스 정책

보스전 생성 시 `bosses.max_hp`를 그대로 쓰지 않고, 난이도 정책과 active 길드원 수로 실제 HP를 계산한다.

```text
actualMaxHp = baseHp + activeMemberCount * hpPerMember
personalTotalDamage = actualMaxHp * personalDamageRatio
commonConditionTotalDamage = actualMaxHp * commonConditionDamageRatio
conditionDamage = commonConditionTotalDamage / conditionCount
```

| difficulty | baseHp | hpPerMember | personal | common |
| --- | ---: | ---: | ---: | ---: |
| EASY | 500 | 150 | 80% | 20% |
| NORMAL | 1000 | 250 | 70% | 30% |
| HARD | 1800 | 400 | 60% | 40% |

공통 조건 damage는 나머지가 생기면 앞 조건부터 1씩 분배한다.

## POST /api/v1/guilds/{guildId}/boss-battles

```json
{
  "bossId": 1
}
```

```json
{
  "battleId": 1,
  "guildId": 1,
  "bossId": 1,
  "seasonId": 1,
  "bossName": "설탕 슬라임",
  "difficulty": "EASY",
  "status": "IN_PROGRESS",
  "maxHp": 800,
  "currentHp": 800,
  "startedAt": "2026-06-10T10:30:00"
}
```

## GET /api/v1/guilds/{guildId}/boss-battles/current

```json
{
  "battle": {
    "battleId": 1,
    "guildId": 1,
    "bossId": 1,
    "bossName": "설탕 슬라임",
    "difficulty": "EASY",
    "bossImageUrl": "https://example.com/boss-easy.png",
    "status": "IN_PROGRESS",
    "maxHp": 800,
    "currentHp": 640,
    "totalDamage": 160,
    "startedAt": "2026-06-10T10:30:00",
    "endedAt": null,
    "endsAt": "2026-06-16T23:59:59"
  }
}
```

## GET /api/v1/boss-battles/{battleId}

```json
{
  "battleId": 1,
  "guildId": 1,
  "guildName": "잘먹잘싸",
  "bossId": 1,
  "bossName": "설탕 슬라임",
  "difficulty": "EASY",
  "bossImageUrl": "https://example.com/boss-easy.png",
  "status": "IN_PROGRESS",
  "maxHp": 800,
  "currentHp": 640,
  "totalDamage": 160,
  "hpRate": 80.0,
  "startedAt": "2026-06-10T10:30:00",
  "endedAt": null,
  "endsAt": "2026-06-16T23:59:59",
  "commonConditions": [
    {
      "battleConditionId": 1,
      "title": "당류 50g 이하 유지",
      "description": "하루 당류 섭취량을 50g 이하로 유지합니다.",
      "targetType": "SUGAR_UNDER_LIMIT",
      "thresholdValue": 50,
      "thresholdUnit": "g",
      "targetValue": 3,
      "requiredDays": 3,
      "currentValue": 0,
      "damage": 160,
      "unit": "일",
      "completed": false,
      "sortOrder": 1
    }
  ],
  "recentDamageLogs": []
}
```

## GET /api/v1/boss-battles/{battleId}/hp

```json
{
  "battleId": 1,
  "status": "IN_PROGRESS",
  "maxHp": 800,
  "currentHp": 640,
  "totalDamage": 160,
  "hpRate": 80.0
}
```

## GET /api/v1/guilds/{guildId}/boss-battles/history

```json
{
  "battles": [],
  "page": 0,
  "size": 10,
  "hasNext": false
}
```
