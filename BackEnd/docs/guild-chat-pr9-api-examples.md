# PR9 Guild Chat API Examples

Base path: `/api/v1`

## 권한 정책

- 길드 채팅 목록 조회와 메시지 전송은 해당 길드의 active member만 가능하다.
- active member 기준은 `guild_members.left_at IS NULL`이다.
- 이번 PR에서는 WebSocket 없이 REST API만 사용한다.
- `POST /guilds/{guildId}/chats`로 생성되는 메시지는 `USER` 타입이다.
- `SYSTEM` 메시지는 이후 퀘스트 완료, 보스 HP 감소, 보스 격파 이벤트에서 서비스 내부 메서드로 저장한다.

## 채팅 목록 조회

`GET /guilds/{guildId}/chats?page=0&size=30`

```json
{
  "success": true,
  "data": {
    "guildId": 1,
    "chats": [
      {
        "chatId": 10,
        "guildId": 1,
        "userId": 3,
        "nickname": "예린",
        "profileImageUrl": "https://example.com/profile.png",
        "characterId": 7,
        "characterName": "냠냠이",
        "characterLevel": 4,
        "messageType": "USER",
        "message": "오늘 퀘스트 같이 완료해요!",
        "createdAt": "2026-06-10T10:30:00",
        "isMe": true
      }
    ],
    "page": 0,
    "size": 30,
    "hasNext": false
  },
  "message": "길드 채팅 목록 조회에 성공했습니다."
}
```

## 채팅 메시지 전송

`POST /guilds/{guildId}/chats`

```json
{
  "message": "오늘 퀘스트 같이 완료해요!"
}
```

```json
{
  "success": true,
  "data": {
    "chatId": 11,
    "guildId": 1,
    "userId": 3,
    "nickname": "예린",
    "profileImageUrl": "https://example.com/profile.png",
    "characterId": 7,
    "characterName": "냠냠이",
    "characterLevel": 4,
    "messageType": "USER",
    "message": "오늘 퀘스트 같이 완료해요!",
    "createdAt": "2026-06-10T10:31:00",
    "isMe": true
  },
  "message": "길드 채팅 메시지가 전송되었습니다."
}
```

## SYSTEM 메시지 응답 예시

```json
{
  "chatId": 12,
  "guildId": 1,
  "userId": null,
  "nickname": "SYSTEM",
  "profileImageUrl": null,
  "characterId": null,
  "characterName": null,
  "characterLevel": null,
  "messageType": "SYSTEM",
  "message": "보스 HP가 100 감소했습니다.",
  "createdAt": "2026-06-10T10:32:00",
  "isMe": false
}
```
