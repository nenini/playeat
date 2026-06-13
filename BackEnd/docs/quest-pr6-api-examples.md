# PR6 Quest API Examples

Base path: `/api/v1`

## Placeholder 퀘스트 damage 정책

PR6에서는 AI를 호출하지 않고 `PlaceholderQuestGenerator`가 기본 퀘스트를 생성한다. damage는 고정값이 아니라 보스전 실제 HP와 난이도별 개인 퀘스트 비중으로 계산한다.

```text
actualMaxHp = baseHp + activeMemberCount * hpPerMember
personalTotalDamage = actualMaxHp * personalDamageRatio
questDamage = personalTotalDamage / activeMemberCount
```

| difficulty | baseHp | hpPerMember | personal | common |
| --- | ---: | ---: | ---: | ---: |
| EASY | 500 | 150 | 80% | 20% |
| NORMAL | 1000 | 250 | 70% | 30% |
| HARD | 1800 | 400 | 60% | 40% |

퀘스트별 damage에 나머지가 생기면 앞 길드원부터 1씩 분배한다. `sourceType`은 계속 `PLACEHOLDER`다.

## 보스전 전체 퀘스트 조회

`GET /boss-battles/{battleId}/quests`

```json
{
  "success": true,
  "data": {
    "battleId": 1,
    "guildId": 1,
    "quests": [
      {
        "questId": 1,
        "userId": 3,
        "nickname": "예린",
        "profileImageUrl": "https://example.com/profile.png",
        "characterId": 1,
        "characterName": "냠냠이",
        "characterLevel": 7,
        "title": "오늘 식단 기록하기",
        "description": "오늘 하루 식단을 1회 이상 기록하세요.",
        "questType": "RECORD_DIET",
        "targetValue": 1,
        "currentValue": 0,
        "unit": "회",
        "damage": 320,
        "rewardExp": 30,
        "rewardCoin": 10,
        "status": "IN_PROGRESS",
        "isMe": true,
        "createdAt": "2026-06-10T10:30:00",
        "completedAt": null
      }
    ]
  },
  "message": "보스전 퀘스트 목록 조회에 성공했습니다."
}
```

## 내 퀘스트 조회

`GET /boss-battles/{battleId}/quests/me`

```json
{
  "success": true,
  "data": {
    "quest": null
  },
  "message": "내 퀘스트 조회에 성공했습니다."
}
```

## 퀘스트 상세 조회

`GET /quests/{questId}`

```json
{
  "success": true,
  "data": {
    "questId": 1,
    "battleId": 1,
    "guildId": 1,
    "userId": 3,
    "nickname": "예린",
    "characterName": "냠냠이",
    "title": "오늘 식단 기록하기",
    "questType": "RECORD_DIET",
    "targetValue": 1,
    "currentValue": 0,
    "unit": "회",
    "damage": 320,
    "rewardExp": 30,
    "rewardCoin": 10,
    "status": "IN_PROGRESS",
    "sourceType": "PLACEHOLDER",
    "isMe": true
  },
  "message": "퀘스트 상세 조회에 성공했습니다."
}
```

## 보스전 퀘스트 생성

`POST /boss-battles/{battleId}/quests/generate`

```json
{
  "success": true,
  "data": {
    "battleId": 1,
    "guildId": 1,
    "createdCount": 3,
    "skippedCount": 1,
    "quests": [
      {
        "questId": 1,
        "userId": 3,
        "nickname": "예린",
        "title": "오늘 식단 기록하기",
        "questType": "RECORD_DIET",
        "targetValue": 1,
        "unit": "회",
        "damage": 320,
        "rewardExp": 30,
        "rewardCoin": 10,
        "status": "IN_PROGRESS"
      }
    ]
  },
  "message": "보스전 퀘스트 생성에 성공했습니다."
}
```

## 퀘스트 기여도 조회

`GET /boss-battles/{battleId}/quests/contributions`

```json
{
  "success": true,
  "data": {
    "battleId": 1,
    "guildId": 1,
    "contributions": [
      {
        "userId": 3,
        "nickname": "예린",
        "profileImageUrl": "https://example.com/profile.png",
        "characterName": "냠냠이",
        "characterLevel": 7,
        "totalQuestCount": 1,
        "completedQuestCount": 0,
        "totalDamage": 0,
        "expectedDamage": 320,
        "isMe": true
      }
    ]
  },
  "message": "보스전 퀘스트 기여도 조회에 성공했습니다."
}
```
