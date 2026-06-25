# #32 분석/식단 영양 기준 API 재설계 최종안

## 이슈 정보

- 이슈 번호: `#32`
- 권장 브랜치명: `feature/#32-analysis-nutrition-standards`
- 라벨: `backend`, `feature`
- 관련 화면: 홈, 식단, 분석
- 목적: 공식 영양 기준을 바탕으로 사용자별 목표 영양소를 계산하고, 홈/식단/분석 화면에서 중복 API 없이 일관된 영양 수치를 제공한다.

## 현재 작업 상태 참고

현재 작업 브랜치는 `feature/#31-frontend-backend-integration`이고, 프론트엔드 API 연동 관련 변경이 작업 트리에 남아 있다.

이번 문서는 #31 프론트 변경을 구현 범위로 포함하지 않는다. #32에서는 백엔드 API 계약과 영양 기준 계산 구조를 정리한다.

현재 추가된 문서:

```text
Readme/issue-32-analysis-nutrition-standard.md
```

## 최종 결정 요약

1. `GET /api/v1/nutrition/daily`는 프론트 화면에서 직접 사용하지 않는다.
2. `nutrition` 도메인 자체는 삭제하지 않고 내부 계산 도메인으로 유지한다.
3. 식단 화면의 영양소 합계는 `GET /api/v1/diets?date=...`의 `dailySummary`로 제공한다.
4. 홈 화면과 분석 일간 화면은 `GET /api/v1/analysis/daily?date=...`를 사용한다.
5. 분석 주간 화면은 신규 `GET /api/v1/analysis/weekly?startDate=...&endDate=...`를 사용한다.
6. `채소` 지표는 홈, 식단, 분석 화면 전체에서 `식이섬유`로 변경한다.
7. 기존 "또래 비교 인사이트"는 점수 비교가 아니라 공식 기준 대비 영양 인사이트로 변경한다.
8. Health Score는 AI가 아니라 deterministic function으로 산출한다.
9. 공식 기준 데이터는 서버 DB에 versioned seed data로 저장한다.
10. `dashboard` 도메인은 길드/보스 대시보드 전용으로 유지하고 개인 분석 API와 합치지 않는다.

## 도메인 책임

| 도메인 | 역할 | 외부 API 노출 |
| --- | --- | --- |
| `diet` | 날짜별 식단 기록, 끼니 카드, 식단 합계 영양소 | 유지 |
| `analysis` | 홈/분석 화면에서 쓰는 개인 일간/주간 분석 데이터 조립 | 유지/확장 |
| `nutrition` | 영양소 집계, 공식 기준 목표 계산, health score 산출 | 외부 API 제거 또는 deprecated |
| `dashboard` | 길드/보스 대시보드 집계 | 현행 유지 |
| `ai` | 일간/주간 AI 리포트 생성과 조회 | 유지 |
| `coach` | 코치 선택, 끼니 피드백 생성과 조회 | 유지 |

## 제거 또는 Deprecated

```http
GET /api/v1/nutrition/daily
```

제거 이유:

- 식단 화면은 `GET /api/v1/diets`의 `dailySummary`만으로 충분하다.
- 분석 화면은 `GET /api/v1/analysis/daily`가 식단, 영양 분석, AI 리포트, 코치 피드백을 조립한다.
- 홈 화면은 `GET /api/v1/analysis/daily`에서 health score와 오늘 kcal을 가져오면 된다.
- `nutrition/daily`를 프론트에서 직접 호출하면 같은 날짜의 영양 합계를 중복 조회/계산하게 된다.

유지할 것:

- `NutritionService`
- `NutritionRepository`
- 영양 집계 쿼리
- 목표 영양소 계산 로직
- health score 계산 로직

## 변경 또는 신규 API

### Update

```http
GET /api/v1/diets?date=2026-05-15
GET /api/v1/analysis/daily?date=2026-05-15
```

### Create

```http
GET /api/v1/analysis/weekly?startDate=2026-05-11&endDate=2026-05-17
```

## 화면별 최종 API

### 회원가입/로그인 화면

```http
POST /api/v1/auth/signup
POST /api/v1/auth/login
```

### 온보딩 화면

```http
POST /api/v1/users/me/onboarding
```

온보딩 정보는 사용자별 목표 영양소 계산에 사용한다.

목표 계산에 사용할 정보:

- 성별
- 생년월일 또는 나이
- 키
- 체중
- 활동 수준
- 목표 유형이 있으면 반영

## 홈 화면

### 사용 API

```http
GET /api/v1/users/me
GET /api/v1/characters/me
GET /api/v1/characters/me/equipments
GET /api/v1/analysis/daily?date=2026-05-15
GET /api/v1/guilds/me/status
GET /api/v1/guilds/{guildId}/boss-battles/current
GET /api/v1/boss-battles/{battleId}/quests/me
```

### 사용 방식

| 화면 요소 | API | 사용 데이터 |
| --- | --- | --- |
| 인사말 | `GET /api/v1/users/me` | `nickname` |
| 캐릭터 레벨/XP/연속 기록 | `GET /api/v1/characters/me` | `level`, `xp`, `requiredXp`, `xpProgressRate`, `streakDays`, `bestStreakDays`, `mood`, `appearanceType` |
| 장착 아이템 | `GET /api/v1/characters/me/equipments` | 장착 슬롯별 아이템 |
| Health Score | `GET /api/v1/analysis/daily` | `healthScore` |
| 오늘 kcal | `GET /api/v1/analysis/daily` | `diet.dailySummary.totalCalories`, `diet.dailySummary.targetCalories`, `diet.dailySummary.calorieRate` |
| 오늘 끼니 카드 | `GET /api/v1/analysis/daily` | `diet.meals` |
| 길드 가입 여부 | `GET /api/v1/guilds/me/status` | `status`, `guild.guildId` |
| 현재 보스전 | `GET /api/v1/guilds/{guildId}/boss-battles/current` | `battleId`, `bossName`, `currentHp`, `maxHp`, `endsAt` |
| 내 퀘스트 | `GET /api/v1/boss-battles/{battleId}/quests/me` | `title`, `rewardExp`, `currentValue`, `targetValue`, `status` |

### 홈 전용 API 여부

이번 #32에서는 `GET /api/v1/home`을 만들지 않는다.

서버 내부에서 길드 가입 여부를 확인하는 것은 가능하지만, 프론트가 그 결과를 받으려면 결국 어떤 API 응답에 포함되어야 한다. 현재 구조에서는 `GET /api/v1/guilds/me/status`가 가장 적절하다.

홈 API 호출 수가 실제 성능 문제가 되면 이후 별도 이슈에서 홈 집계 API를 추가한다.

## 식단 화면

### 사용 API

```http
GET /api/v1/diets?date=2026-05-15
POST /api/v1/diets
GET /api/v1/diets/{dietId}
PATCH /api/v1/diets/{dietId}
DELETE /api/v1/diets/{dietId}
GET /api/v1/foods?keyword=...
GET /api/v1/foods/{foodId}
GET /api/v1/foods/frequent
```

### 영양소 표시 방식

식단 화면 하단의 "오늘 합계"는 `GET /api/v1/diets?date=...` 응답의 `dailySummary`를 사용한다.

```text
data.meals        -> 아침/점심/간식/저녁 카드
data.dailySummary -> 오늘 합계 영양소 바
```

따라서 식단 화면에서는 `GET /api/v1/nutrition/daily`를 호출하지 않는다.

### `DailyNutritionSummaryResponse` 변경

기존 채소 필드:

```text
vegetableServings
targetVegetableServings
vegetableRate
```

변경할 식이섬유 필드:

```text
totalFiber
targetFiber
fiberRate
```

예시:

```json
{
  "date": "2026-05-15",
  "meals": [],
  "dailySummary": {
    "totalCalories": 1213,
    "targetCalories": 2000,
    "calorieRate": 61,
    "totalProtein": 60,
    "targetProtein": 90,
    "proteinRate": 67,
    "totalCarbs": 171,
    "targetCarbs": 260,
    "carbsRate": 66,
    "totalFat": 30,
    "targetFat": 65,
    "fatRate": 46,
    "totalFiber": 0,
    "targetFiber": 25,
    "fiberRate": 0
  }
}
```

## 분석 화면

### 일간 리포트

```http
GET /api/v1/analysis/daily?date=2026-05-15
GET /api/v1/ai/reports/daily?date=2026-05-15
POST /api/v1/ai/reports/daily
```

`GET /api/v1/analysis/daily`는 일간 분석 화면의 기본 수치 데이터를 제공한다.

포함 데이터:

- 날짜
- health score
- 식단 기록
- 끼니 기록 수
- 영양소 섭취량
- 목표 영양소
- 달성률
- 저장된 일간 AI 리포트
- 최신 끼니 코치 피드백

AI 리포트 생성은 화면 진입마다 자동 호출하지 않는다. 리포트가 없거나 사용자가 갱신할 때만 `POST /api/v1/ai/reports/daily`를 호출한다.

### 일간 분석 응답 방향

문구, 상태, 비교 점수보다 수치 중심으로 제공한다.

```json
{
  "date": "2026-05-15",
  "healthScore": 76,
  "diet": {
    "recordedMealCount": 3,
    "targetMealCount": 4,
    "meals": []
  },
  "nutrition": {
    "intake": {
      "calories": 1213,
      "protein": 60,
      "carbs": 171,
      "fat": 30,
      "sodium": 2380,
      "fiber": 0
    },
    "target": {
      "calories": 2000,
      "protein": 90,
      "carbs": 260,
      "fat": 65,
      "sodium": 2000,
      "fiber": 25
    },
    "achievementRate": {
      "calories": 61,
      "protein": 67,
      "carbs": 66,
      "fat": 46,
      "sodium": 119,
      "fiber": 0
    }
  },
  "dailyReport": null,
  "latestMealFeedback": null
}
```

프론트는 수치를 바탕으로 `부족`, `초과`, `적정` 배지를 계산한다.

### 주간 리포트

```http
GET /api/v1/analysis/weekly?startDate=2026-05-11&endDate=2026-05-17
GET /api/v1/ai/reports/weekly?weekStartDate=2026-05-11&weekEndDate=2026-05-17
POST /api/v1/ai/reports/weekly
```

`GET /api/v1/nutrition/daily`를 7번 호출해서 주간 평균을 만드는 방식은 사용하지 않는다. 서버가 주간 범위를 한 번에 집계한다.

주간 분석 응답 예시:

```json
{
  "startDate": "2026-05-11",
  "endDate": "2026-05-17",
  "averageHealthScore": 75,
  "scoreDiffFromPreviousWeek": 8,
  "recordRate": 92,
  "recordedDays": 6,
  "totalDays": 7,
  "dailyScores": [
    { "date": "2026-05-11", "dayOfWeek": "MON", "healthScore": 70 },
    { "date": "2026-05-12", "dayOfWeek": "TUE", "healthScore": 65 }
  ],
  "weeklyNutritionAverage": {
    "calories": 1530,
    "protein": 61,
    "carbs": 210,
    "fat": 48,
    "sodium": 2100,
    "fiber": 17
  },
  "weeklyReport": null
}
```

### 코치 한마디

```http
GET /api/v1/coaches
PUT /api/v1/coaches/me
GET /api/v1/coaches/me/diets/{dietId}/feedback
POST /api/v1/coaches/me/diets/{dietId}/feedback
```

코치 목록과 선택은 분석 화면의 코치 변경 UI에서 사용한다. 끼니 피드백 생성은 매번 자동 생성하지 않고, 없을 때 생성하거나 사용자 요청 시 생성한다.

## 오늘의 목표 달성

`GET /api/v1/analysis/daily`의 `nutrition` 수치를 사용한다.

화면에 필요한 항목:

- 칼로리
- 단백질
- 탄수화물
- 지방
- 나트륨
- 식이섬유

각 항목은 다음 세 값을 가진다.

```text
섭취량
목표량
달성률
```

## 기준 대비 영양 인사이트

기존 "또래 비교 인사이트"는 점수 비교가 아니라 공식 기준 대비 섭취량 비교로 바꾼다.

권장 명칭:

```text
기준 대비 영양 인사이트
```

비교 기준:

- 같은 성별
- 같은 연령대
- 공식 영양소 섭취기준

서버는 별도 비교 점수를 만들지 않고, 사용자 섭취량과 목표 기준값을 내려준다. 화면 문구는 프론트에서 계산한다. 이후 문구 품질이 중요해지면 별도 메시지 생성 정책을 추가한다.

## Health Score 산출 방식

AI가 아니라 deterministic function으로 계산한다. 같은 입력이면 항상 같은 점수가 나와야 한다.

권장 가중치:

| 항목 | 가중치 | 기준 |
| --- | ---: | --- |
| 칼로리 | 25 | 목표 대비 적정 범위 |
| 단백질 | 20 | 부족 여부 |
| 탄수화물 | 15 | 부족/초과 여부 |
| 지방 | 15 | 부족/초과 여부 |
| 나트륨 | 15 | 초과 여부 |
| 식이섬유 | 10 | 부족 여부 |

총점은 100점 만점이다.

나트륨은 낮을수록 좋은 항목이 아니라, 권장 상한을 크게 넘는 경우 감점하는 방식으로 처리한다.

## 공식 기준 데이터 저장 방식

공식 자료 기반 기준값은 서버 DB에 versioned seed data로 저장한다.

### 기준 테이블 예시

```sql
nutrition_reference_standards
```

필드 예시:

```text
id
standard_version
source_name
gender
age_min
age_max
calorie_kcal
protein_g
carbs_g
fat_g
sodium_mg
fiber_g
created_at
updated_at
```

### 사용자별 계산 결과 저장

기존 `health_profiles`에 목표값을 저장한다.

현재 목표값이 있는 항목은 유지하고, 식이섬유와 기준 버전을 추가한다.

추가 후보:

```text
target_fiber_g
nutrition_standard_version
nutrition_target_calculated_at
```

온보딩 또는 건강 프로필 수정 시 목표값을 다시 계산한다.

## 공식 근거

기준값은 다음 공식 자료를 우선한다.

- 보건복지부/한국영양학회, `2020 한국인 영양소 섭취기준`
- 식품의약품안전처 식품영양성분 데이터베이스

식품별 실제 섭취 영양성분은 식품 DB를 기준으로 하고, 사용자별 목표 영양소는 한국인 영양소 섭취기준을 기준으로 한다.

## 보스/길드/상점/마이페이지 API

이번 #32 범위에서는 변경하지 않는다.

### 보스 화면

```http
GET /api/v1/bosses/{bossId}
GET /api/v1/bosses/current
POST /api/v1/guilds/{guildId}/boss-battles
POST /api/v1/boss-battles/{battleId}/common-conditions/verify
GET /api/v1/boss-battles/{battleId}/hp
POST /api/v1/boss-battles/{battleId}/reward
GET /api/v1/boss-battles/{battleId}/dashboard
GET /api/v1/boss-battles/{battleId}/quests
GET /api/v1/boss-battles/{battleId}/quests/contributions
POST /api/v1/boss-battles/{battleId}/quests/generate
GET /api/v1/boss-battles/{battleId}/quests/me
GET /api/v1/quests/{questId}
POST /api/v1/quests/{questId}/reward
POST /api/v1/quests/{questId}/verify
```

### 길드 화면

```http
GET /api/v1/guilds
POST /api/v1/guilds
GET /api/v1/guilds/me
GET /api/v1/guilds/me/status
POST /api/v1/guilds/join-requests
GET /api/v1/guilds/join-requests/me
GET /api/v1/guilds/{guildId}
PATCH /api/v1/guilds/{guildId}
DELETE /api/v1/guilds/{guildId}
GET /api/v1/guilds/{guildId}/members
GET /api/v1/guilds/{guildId}/members/{memberId}
DELETE /api/v1/guilds/{guildId}/members/me
DELETE /api/v1/guilds/{guildId}/members/{memberId}
POST /api/v1/guilds/{guildId}/join-requests
GET /api/v1/guilds/{guildId}/join-requests
DELETE /api/v1/guilds/{guildId}/join-requests/{requestId}
POST /api/v1/guilds/{guildId}/join-requests/{requestId}/approve
POST /api/v1/guilds/{guildId}/join-requests/{requestId}/reject
GET /api/v1/guilds/{guildId}/notices
GET /api/v1/guilds/{guildId}/notices/{noticeId}
POST /api/v1/guilds/{guildId}/notices
PATCH /api/v1/guilds/{guildId}/notices/{noticeId}
DELETE /api/v1/guilds/{guildId}/notices/{noticeId}
GET /api/v1/guilds/{guildId}/dashboard
GET /api/v1/guilds/{guildId}/reports/weekly
```

### 상점 화면

```http
GET /api/v1/coins/me
GET /api/v1/items/me
GET /api/v1/items/me/{userItemId}
GET /api/v1/shop
GET /api/v1/shop/items
GET /api/v1/shop/items/{itemId}
POST /api/v1/shop/items/{itemId}/purchase
```

### 마이페이지

```http
GET /api/v1/users/me
PATCH /api/v1/users/me
DELETE /api/v1/users/me
GET /api/v1/users/me/health-profile
PATCH /api/v1/users/me/health-profile
POST /api/v1/users/me/onboarding
PATCH /api/v1/users/me/password
PUT /api/v1/users/me/profile-image
DELETE /api/v1/users/me/profile-image
GET /api/v1/characters/me
GET /api/v1/items/me
```

## 작업 상세

### Entity / Domain 설계

- `nutrition_reference_standards` 기준 테이블 추가
- `health_profiles`에 식이섬유 목표값과 기준 버전 추가
- `nutrition`은 외부 화면 API가 아니라 내부 계산 도메인으로 유지
- `analysis`는 개인 분석 화면 조립 도메인으로 유지
- `dashboard`는 길드/보스 대시보드 도메인으로 유지

### DTO 작성

- `AnalysisDailyResponse` 재설계
- `AnalysisWeeklyResponse` 신규 작성
- `DailyNutritionSummaryResponse`에서 채소 필드를 식이섬유 필드로 변경
- 필요 시 내부용 `NutritionTargetResponse`, `NutrientIntakeResponse` 분리

### Repository 작성

- 공식 기준 조회 Repository 추가
- 날짜별 영양소 집계 쿼리에 식이섬유 포함
- 주간 분석 집계 쿼리 추가

### Service 로직 구현

- 온보딩 기반 목표 영양소 계산
- 식이섬유 목표값 계산
- health score 계산
- 일간 분석 조립
- 주간 분석 조립

### Controller 구현

- `GET /api/v1/analysis/daily` 재설계
- `GET /api/v1/analysis/weekly` 추가
- `GET /api/v1/nutrition/daily` 제거 또는 deprecated 처리

### 예외 처리

- 건강 프로필이 없는 경우 온보딩 필요 에러 반환
- 공식 기준 데이터를 찾을 수 없는 경우 기본 기준 또는 명확한 비즈니스 에러 처리
- 날짜 범위가 잘못된 경우 주간 분석 요청 검증 실패 처리

### API 문서화

- Swagger에서 `nutrition/daily` 제거 또는 deprecated 표시
- `analysis/daily`, `analysis/weekly` 응답 예시 추가
- `vegetable` 필드 제거 및 `fiber` 필드 반영

### 테스트

- 목표 영양소 계산 테스트
- health score 계산 테스트
- `GET /api/v1/diets`의 `dailySummary` 식이섬유 응답 테스트
- `GET /api/v1/analysis/daily` 응답 조립 테스트
- `GET /api/v1/analysis/weekly` 주간 집계 테스트

## 최종 API 변경 요약

```text
Deprecated/Remove
- GET /api/v1/nutrition/daily

Update
- GET /api/v1/diets?date=...
- GET /api/v1/analysis/daily?date=...

Create
- GET /api/v1/analysis/weekly?startDate=...&endDate=...
```

이번 이슈의 핵심은 API를 많이 추가하는 것이 아니라, `nutrition` API 중복 노출을 줄이고 `analysis`와 `diets`를 화면 기준의 단일 데이터 소스로 정리하는 것이다.
