# PR8 Ranking 및 Dashboard API 예시

기준 경로는 서버 설정상 `/api/v1`로 노출되며, 컨트롤러 내부 매핑은 기존 프로젝트 스타일에 맞춰 `/v1`을 사용한다.

## 길드 주간 랭킹 조회

`GET /api/v1/guilds/rankings?weekStartDate=2026-06-08&weekEndDate=2026-06-14&size=10`

프론트의 메인 "길드 순위" 영역은 이 API를 사용한다. 전체 길드를 대상으로 `weeklyScore` 기준 순위를 계산하며, 보스별 랭킹과 별개의 메인 랭킹이다.

정렬 기준:

```text
weeklyScore DESC
recordRate DESC
questCompletionRate DESC
bossDamage DESC
guildId ASC
```

```json
{
  "weekStartDate": "2026-06-08",
  "weekEndDate": "2026-06-14",
  "myGuildRank": 1,
  "rankings": [
    {
      "rank": 1,
      "guildId": 1,
      "guildName": "잘먹잘싸",
      "myGuild": true,
      "weeklyScore": 2840,
      "recordRate": 92.0,
      "questCompletionRate": 70.8,
      "bossDamage": 108
    }
  ]
}
```

## 보스별 랭킹 조회

`GET /api/v1/bosses/1/rankings`

이 API는 보스 상세 화면 또는 보스전 관련 서브 랭킹에서 사용한다. 전체 길드 경쟁 순위가 아니라, 특정 보스를 누가 더 잘 공략했는지 보여준다.

정렬 기준:

```text
클리어한 길드 우선
totalDamage DESC
endedAt ASC
startedAt ASC
guildId ASC
```

```json
{
  "bossId": 1,
  "bossName": "당분 드래곤",
  "difficulty": "EASY",
  "myGuildRank": 1,
  "rankings": [
    {
      "rank": 1,
      "guildId": 1,
      "guildName": "잘먹잘싸",
      "myGuild": true,
      "status": "DEFEATED",
      "maxHp": 1100,
      "currentHp": 0,
      "totalDamage": 1100,
      "hpRate": 0.0,
      "startedAt": "2026-06-10T10:30:00",
      "endedAt": "2026-06-13T20:00:00"
    }
  ]
}
```

## 길드 대시보드 조회

`GET /api/v1/guilds/1/dashboard`

`myRank`는 보스별 랭킹 순위가 아니라 `GET /api/v1/guilds/rankings`와 동일한 전체 길드 주간 포인트 랭킹 기준이다. `weeklyScore`도 메인 길드 순위에 사용하는 점수와 같은 공식으로 계산한다.

```json
{
  "guildId": 1,
  "guildName": "잘먹잘싸",
  "myRank": 1,
  "weeklyScore": 2840,
  "recordRate": 92.0,
  "bossDamage": 108,
  "questCompletedCount": 17,
  "questTotalCount": 24,
  "dailyScores": [
    { "dayOfWeek": "MON", "score": 420 },
    { "dayOfWeek": "TUE", "score": 510 },
    { "dayOfWeek": "WED", "score": 620 },
    { "dayOfWeek": "THU", "score": 480 },
    { "dayOfWeek": "FRI", "score": 530 },
    { "dayOfWeek": "SAT", "score": 280 },
    { "dayOfWeek": "SUN", "score": 0 }
  ]
}
```

## 보스전 대시보드 조회

`GET /api/v1/boss-battles/1/dashboard`

```json
{
  "battleId": 1,
  "guildId": 1,
  "bossName": "당분 드래곤",
  "difficulty": "NORMAL",
  "status": "IN_PROGRESS",
  "maxHp": 2000,
  "currentHp": 1200,
  "totalDamage": 800,
  "hpRate": 60.0,
  "questCompletedCount": 3,
  "questTotalCount": 5,
  "commonConditionCompletedCount": 1,
  "commonConditionTotalCount": 2,
  "weeklyScore": 2840
}
```

## 길드 주간 리포트 조회

`GET /api/v1/guilds/1/reports/weekly?weekStartDate=2026-06-08&weekEndDate=2026-06-14`

```json
{
  "guildId": 1,
  "guildName": "잘먹잘싸",
  "weekStartDate": "2026-06-08",
  "weekEndDate": "2026-06-14",
  "recordRate": 92.0,
  "bossDamage": 108,
  "weeklyScore": 2840,
  "questCompletedCount": 17,
  "questTotalCount": 24,
  "dailyStats": [
    {
      "date": "2026-06-08",
      "dayOfWeek": "MON",
      "recordCount": 4,
      "activeMemberCount": 4,
      "recordRate": 100.0,
      "questCompletedCount": 3,
      "damage": 200,
      "score": 420
    }
  ]
}
```

## 계산 정책

메인 길드 순위:

```text
GET /api/v1/guilds/rankings
전체 길드 주간 weeklyScore 기준
```

보스별 서브 랭킹:

```text
GET /api/v1/bosses/{bossId}/rankings
특정 bossId의 클리어 여부와 totalDamage 기준
```

주간 점수:

```text
weeklyScore =
round(recordRate / 100 * 1000)
+ round(questCompletionRate / 100 * 1000)
+ round(bossProgressRate / 100 * 800)
+ clearBonus
```

기록률:

```text
recordRate = recordedMemberDateCount / (activeMemberCount * elapsedDaysInWeek) * 100
```

요일별 점수:

```text
dailyScore = guild_score_logs 합계
dailyScore 로그가 없으면 recordCount * 30
```

PR7에서 퀘스트 검증, 공통 조건 완료, 보스 클리어가 구현되면 `GuildScoreService.addScoreLog(...)`를 호출해 `QUEST_COMPLETE`, `COMMON_CONDITION_COMPLETE`, `BOSS_CLEAR` 이벤트 점수를 쌓으면 된다.
