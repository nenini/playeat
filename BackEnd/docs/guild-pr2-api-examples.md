# Guild PR2 API Examples

All endpoints require a Bearer access token.

## PATCH /api/v1/guilds/{guildId}

```json
{
  "name": "단백질 원정대",
  "description": "건강하게 먹고 함께 보스 잡는 길드",
  "maxMembers": 30
}
```

```json
{
  "guildId": 1,
  "name": "단백질 원정대",
  "description": "건강하게 먹고 함께 보스 잡는 길드",
  "inviteCode": "NYAM-A7K3",
  "memberCount": 6,
  "maxMembers": 30,
  "visibility": "PRIVATE",
  "status": "ACTIVE",
  "updatedAt": "2026-06-10T10:30:00"
}
```

## DELETE /api/v1/guilds/{guildId}

```json
{
  "guildId": 1,
  "status": "INACTIVE",
  "deletedAt": "2026-06-10T10:30:00"
}
```

## DELETE /api/v1/guilds/{guildId}/members/me

```json
{
  "guildId": 1,
  "userId": 5,
  "leftAt": "2026-06-10T10:30:00"
}
```

## GET /api/v1/guilds/{guildId}/members/{memberId}

```json
{
  "memberId": 1,
  "userId": 1,
  "nickname": "예린",
  "profileImageUrl": "https://example.com/profile.png",
  "characterId": 1,
  "characterName": "냠냠이",
  "characterLevel": 7,
  "characterStage": "BABY",
  "characterMood": "HAPPY",
  "characterAppearanceType": "NORMAL",
  "streakDays": 5,
  "role": "OWNER",
  "joinedAt": "2026-06-09T10:30:00",
  "isMe": true,
  "weeklyRecordRate": 0,
  "bossContribution": 0,
  "completedQuestCount": 0
}
```

## DELETE /api/v1/guilds/{guildId}/members/{memberId}

```json
{
  "guildId": 1,
  "memberId": 3,
  "userId": 5,
  "kickedAt": "2026-06-10T10:30:00"
}
```

## GET /api/v1/guilds/{guildId}/notices

```json
{
  "notices": [
    {
      "noticeId": 10,
      "guildId": 1,
      "writerUserId": 1,
      "writerNickname": "예린",
      "title": "이번 주 보스전 안내",
      "content": "이번 주는 당류를 줄이는 퀘스트가 많습니다.",
      "createdAt": "2026-06-10T10:00:00",
      "updatedAt": "2026-06-10T10:30:00",
      "editable": true
    }
  ]
}
```

## GET /api/v1/guilds/{guildId}/notices/{noticeId}

```json
{
  "noticeId": 10,
  "guildId": 1,
  "writerUserId": 1,
  "writerNickname": "예린",
  "title": "이번 주 보스전 안내",
  "content": "이번 주는 당류를 줄이는 퀘스트가 많습니다.",
  "createdAt": "2026-06-10T10:00:00",
  "updatedAt": "2026-06-10T10:30:00",
  "editable": true
}
```

## POST /api/v1/guilds/{guildId}/notices

```json
{
  "title": "이번 주 보스전 안내",
  "content": "이번 주는 당류를 줄이는 퀘스트가 많습니다."
}
```

```json
{
  "noticeId": 10,
  "guildId": 1,
  "writerUserId": 1,
  "writerNickname": "예린",
  "title": "이번 주 보스전 안내",
  "content": "이번 주는 당류를 줄이는 퀘스트가 많습니다.",
  "createdAt": "2026-06-10T10:00:00",
  "updatedAt": "2026-06-10T10:30:00"
}
```

## PATCH /api/v1/guilds/{guildId}/notices/{noticeId}

```json
{
  "title": "이번 주 보스전 안내",
  "content": "이번 주는 단백질 기록 퀘스트가 많습니다."
}
```

```json
{
  "noticeId": 10,
  "guildId": 1,
  "writerUserId": 1,
  "writerNickname": "예린",
  "title": "이번 주 보스전 안내",
  "content": "이번 주는 단백질 기록 퀘스트가 많습니다.",
  "createdAt": "2026-06-10T10:00:00",
  "updatedAt": "2026-06-10T11:00:00"
}
```

## DELETE /api/v1/guilds/{guildId}/notices/{noticeId}

```json
{
  "guildId": 1,
  "noticeId": 10,
  "deleted": true
}
```
