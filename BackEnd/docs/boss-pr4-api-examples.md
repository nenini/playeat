# Boss PR4 API Examples

## GET /api/v1/bosses/current

```json
{
  "seasonId": 1,
  "seasonName": "2026년 6월 2주차",
  "startsAt": "2026-06-10T00:00:00",
  "endsAt": "2026-06-16T23:59:59",
  "bosses": [
    {
      "bossId": 1,
      "name": "설탕 슬라임",
      "description": "달콤한 간식을 좋아하는 초급 보스",
      "difficulty": "EASY",
      "maxHp": 1000,
      "imageUrl": "https://example.com/boss-easy.png",
      "rewardExp": 100,
      "rewardCoin": 50
    }
  ],
  "commonConditions": [
    {
      "conditionId": 1,
      "title": "길드원 4명 이상 식단 기록",
      "description": "이번 시즌 동안 길드원 4명 이상이 식단을 기록해야 합니다.",
      "targetType": "DIET_RECORD_MEMBER_COUNT",
      "targetValue": 4,
      "unit": "명",
      "sortOrder": 1
    }
  ]
}
```

## GET /api/v1/bosses/{bossId}

```json
{
  "bossId": 1,
  "seasonId": 1,
  "name": "설탕 슬라임",
  "description": "달콤한 간식을 좋아하는 초급 보스",
  "difficulty": "EASY",
  "maxHp": 1000,
  "imageUrl": "https://example.com/boss-easy.png",
  "rewardExp": 100,
  "rewardCoin": 50,
  "status": "ACTIVE",
  "startsAt": "2026-06-10T00:00:00",
  "endsAt": "2026-06-16T23:59:59",
  "commonConditions": [
    {
      "conditionId": 1,
      "title": "길드원 4명 이상 식단 기록",
      "description": "이번 시즌 동안 길드원 4명 이상이 식단을 기록해야 합니다.",
      "targetType": "DIET_RECORD_MEMBER_COUNT",
      "targetValue": 4,
      "unit": "명",
      "sortOrder": 1
    }
  ]
}
```
