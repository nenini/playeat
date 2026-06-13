# Boss Battle PR5 API Examples

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
  "maxHp": 1000,
  "currentHp": 1000,
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
    "maxHp": 1000,
    "currentHp": 800,
    "totalDamage": 200,
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
  "maxHp": 1000,
  "currentHp": 800,
  "totalDamage": 200,
  "hpRate": 80.0,
  "startedAt": "2026-06-10T10:30:00",
  "endedAt": null,
  "endsAt": "2026-06-16T23:59:59",
  "commonConditions": [],
  "recentDamageLogs": []
}
```

## GET /api/v1/boss-battles/{battleId}/hp

```json
{
  "battleId": 1,
  "status": "IN_PROGRESS",
  "maxHp": 1000,
  "currentHp": 800,
  "totalDamage": 200,
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
