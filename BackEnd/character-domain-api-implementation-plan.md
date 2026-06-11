# Character Domain API Implementation Plan

기준:
- 화면 초안: 메인 화면 캐릭터 카드, 레벨/경험치 바, 기분 문구, 연속 기록일, 오늘 칼로리, 건강 점수 표시
- 사용자 제안 API: `[Character]`
- 실제 public URL은 `server.servlet.context-path=/api` 때문에 `/api/v1/characters/...`이다.
- Controller 매핑은 기존 코드 스타일대로 context-path를 제외한 `/v1/characters/...`를 사용한다.
- 현재 `character` 패키지는 `.gitkeep`만 있고, DB 스키마에는 `characters`, `xp_histories` 테이블이 이미 있다.

## 결론

이번 캐릭터 MVP에서 외부에 공개할 API는 아래 3개로 확정한다.

| Method | Public URL | 목적 | MVP 포함 |
| --- | --- | --- | --- |
| GET | `/api/v1/characters/me` | 내 캐릭터 기본 정보 조회 | 포함 |
| PATCH | `/api/v1/characters/me/name` | 캐릭터 이름 수정 | 포함 |
| GET | `/api/v1/characters/me/xp-history` | 캐릭터 경험치 이력 조회 | 포함 |

일부러 만들지 않는 API:
- `POST /api/v1/characters`: 사용자가 직접 캐릭터를 생성하지 않는다.
- `DELETE /api/v1/characters/me`: 캐릭터는 사용자 계정에 종속되므로 계정 삭제/탈퇴 흐름에서 함께 제거된다.
- `PATCH /api/v1/characters/me/xp`: 클라이언트가 경험치를 직접 수정하면 보상 조작 위험이 생긴다.
- `GET /api/v1/characters/me/status`: `GET /characters/me`와 중복되고, 오늘 칼로리/건강 점수 같은 홈 화면 조합 값을 캐릭터 도메인에 섞게 되므로 만들지 않는다.

캐릭터 생성과 경험치 증가는 내부 서비스에서 처리한다.
- 회원가입 또는 온보딩 완료 시 캐릭터가 없으면 자동 생성한다.
- 식단 기록, 보스 기여, 관리자 지급 같은 도메인 이벤트가 발생하면 내부 `CharacterGrowthService`가 경험치와 이력을 기록한다.

## 현재 스키마 기준

이미 존재하는 `characters` 컬럼:
- `character_id`
- `user_id`
- `name`
- `level`
- `xp`
- `stage`
- `mood`
- `appearance_type`
- `streak_days`
- `best_streak_days`
- `created_at`
- `updated_at`

이미 존재하는 `xp_histories` 컬럼:
- `xp_history_id`
- `user_id`
- `character_id`
- `source_type`
- `source_id` (`NOT NULL`)
- `xp_amount`
- `reason`
- `created_at`

이번 MVP에서는 아래 스키마 변경을 포함한다.

```sql
ALTER TABLE characters
  ADD COLUMN best_streak_days INT NOT NULL DEFAULT 0;

ALTER TABLE xp_histories
  MODIFY source_id BIGINT NOT NULL;

ALTER TABLE xp_histories
  ADD UNIQUE KEY uq_xp_source (character_id, source_type, source_id);
```

이유:
- `best_streak_days`는 계정 정보가 아니라 캐릭터 활동 지표이므로 `characters`에 둔다.
- `source_id NOT NULL`과 unique constraint를 같이 사용해야 같은 보상 이벤트 XP가 DB 레벨에서 중복 지급되지 않는다.
- 모든 XP 지급자는 멱등성 기준이 되는 `source_id`를 반드시 제공한다.

추가 검토 컬럼:
- `characters.total_xp`: 전체 누적 경험치가 필요하면 추가한다. 현재 `xp`를 "현재 레벨 내 경험치"로 쓸지 "총 경험치"로 쓸지 먼저 정해야 한다.
- `xp_histories.balance_after`: 이력 화면에서 변경 후 경험치를 정확히 보여주려면 추가할 수 있다. MVP에서는 현재 이력 금액과 발생 이유만 보여준다.

결정:
- MVP에서는 `characters.xp`를 현재 레벨 내 경험치로 사용한다.
- 레벨업에 필요한 경험치는 서버 코드에서 계산한다.
- 누적 경험치는 필요할 때 `xp_histories` 합계로 계산하거나 별도 컬럼을 추가한다.
- `source_type`은 MVP에서 `DIET`, `BOSS`, `ADMIN`만 사용한다.
- `ADMIN` 지급도 `source_id`를 `NULL`로 두지 않는다. 관리자 지급 이력 ID 또는 요청 ID처럼 매번 고유한 값을 넣는다.

## API 상세 설계

### 1. 내 캐릭터 조회

```http
GET /api/v1/characters/me
Authorization: Bearer {accessToken}
```

용도:
- 캐릭터 상세 화면 또는 마이페이지의 캐릭터 요약에서 사용한다.
- 캐릭터의 정적/기본 상태를 반환한다.

Response:

```json
{
  "success": true,
  "data": {
    "characterId": 1,
    "userId": 1,
    "name": "냠냠이",
    "level": 7,
    "xp": 950,
    "requiredXp": 1200,
    "xpProgressRate": 79.2,
    "stage": "LEVEL_1",
    "mood": "HAPPY",
    "appearanceType": "NORMAL",
    "streakDays": 15,
    "bestStreakDays": 21,
    "createdAt": "2026-06-11T09:00:00",
    "updatedAt": "2026-06-11T09:10:00"
  },
  "message": "캐릭터 조회에 성공했습니다."
}
```

### 2. 캐릭터 이름 수정

```http
PATCH /api/v1/characters/me/name
Authorization: Bearer {accessToken}
Content-Type: application/json
```

Request:

```json
{
  "name": "냠냠왕"
}
```

Validation:
- 필수
- 공백만 입력 불가
- 2자 이상 20자 이하
- MVP에서는 사용자별 캐릭터가 1개이므로 이름 중복 검사는 하지 않는다.

Response:

```json
{
  "success": true,
  "data": {
    "characterId": 1,
    "name": "냠냠왕",
    "updatedAt": "2026-06-11T09:20:00"
  },
  "message": "캐릭터 이름이 수정되었습니다."
}
```

### 3. 캐릭터 경험치 이력 조회

```http
GET /api/v1/characters/me/xp-history?page=0&size=20&sourceType=DIET
Authorization: Bearer {accessToken}
```

Query:
- `page`: 기본 0
- `size`: 기본 20, 최대 100
- `sourceType`: 선택 필터. 예: `DIET`, `BOSS`, `ADMIN`

Response:

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "xpHistoryId": 10,
        "sourceType": "DIET",
        "sourceId": 31,
        "xpAmount": 120,
        "reason": "채소 2종 이상 기록",
        "createdAt": "2026-06-11T08:30:00"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "hasNext": false
  },
  "message": "경험치 이력 조회에 성공했습니다."
}
```

## DTO 계획

Request:
- `UpdateCharacterNameRequest`

Response:
- `CharacterResponse`
- `UpdateCharacterNameResponse`
- `XpHistoryResponse`
- `XpHistoryListResponse`

Entity:
- `Character`
- `XpHistory`

Enum:
- `CharacterStage`
  - `LEVEL_1`
  - `LEVEL_2`
  - `LEVEL_3`
  - `LEVEL_4`
- `CharacterMood`
  - `NORMAL`
  - `HAPPY`
  - `SAD`
  - `TIRED`
- `CharacterAppearanceType`
  - `NORMAL`
- `XpSourceType`
  - `DIET`
  - `BOSS`
  - `ADMIN`

처음에는 DB 값과 Java enum 이름을 동일하게 맞춘다. 화면 문구는 프론트에서 직접 매핑하거나, 필요하면 서버가 별도 `displayName`을 내려준다.

## Repository / Mapper 계획

추가 파일:
- `character/entity/Character.java`
- `character/entity/XpHistory.java`
- `character/repository/CharacterRepository.java`
- `character/repository/XpHistoryRepository.java`
- `resources/mappers/character/CharacterMapper.xml`
- `resources/mappers/character/XpHistoryMapper.xml`

`CharacterRepository` 메서드:
- `Optional<Character> findByUserId(Long userId)`
- `Optional<Character> findByIdAndUserId(Long characterId, Long userId)`
- `int save(Character character)`
- `int updateName(Long userId, String name)`
- `int updateGrowth(Long characterId, int level, int xp, String stage, String mood, String appearanceType, int streakDays, int bestStreakDays)`
- `boolean existsByUserId(Long userId)`

`XpHistoryRepository` 메서드:
- `int save(XpHistory xpHistory)`
- `List<XpHistory> findByUserId(Long userId, String sourceType, int offset, int size)`
- `long countByUserId(Long userId, String sourceType)`

## Service 계획

추가 파일:
- `character/service/CharacterService.java`
- 필요하면 `character/service/CharacterGrowthService.java`

`CharacterService` 공개 메서드:
- `CharacterResponse getMyCharacter(Long userId)`
- `UpdateCharacterNameResponse updateName(Long userId, UpdateCharacterNameRequest request)`
- `XpHistoryListResponse getXpHistory(Long userId, int page, int size, String sourceType)`

`CharacterGrowthService` 내부 메서드:
- `void createDefaultCharacterIfMissing(Long userId, String nickname)`
- `void addXp(Long userId, XpSourceType sourceType, Long sourceId, int xpAmount, String reason)`

자동 생성 규칙:
- 기본 이름은 사용자 닉네임이 있으면 `{nickname}의 냠냠이` 또는 `냠냠이` 중 팀이 정한다.
- 이번 계획에서는 화면과 단순성을 우선해서 기본값을 `냠냠이`로 둔다.
- 생성 시 `level=1`, `xp=0`, `stage=LEVEL_1`, `mood=NORMAL`, `appearanceType=NORMAL`, `streakDays=0`.

레벨 계산 규칙:
- MVP 기본식: `requiredXp = level * 150 + 150`
- 예: Lv.1 -> 300, Lv.7 -> 1200
- 화면 예시의 `Lv.7 -> Lv.8`, `950 / 1,200 XP`와 맞는다.

경험치 증가 처리:
- `addXp`는 트랜잭션으로 묶는다.
- 순서:
  1. 사용자 캐릭터 조회
  2. `xp_histories` insert
  3. `DuplicateKeyException`이 발생하면 이미 지급된 XP로 보고 멱등하게 무시
  4. 현재 xp에 획득 xp 더하기
  5. 필요 경험치 이상이면 level up 반복
  6. stage/mood/streak/bestStreak 계산 후 `characters` update

중복 지급 방지:

```sql
UNIQUE KEY uq_xp_source (character_id, source_type, source_id)
```

```java
try {
    xpHistoryRepository.save(xpHistory);
} catch (DuplicateKeyException e) {
    log.warn("XP already granted: sourceType={}, sourceId={}", sourceType, sourceId);
    return;
}
```

`source_id`는 `NOT NULL`이므로 `DIET`, `BOSS`, `ADMIN` 모두 지급 원인을 식별할 수 있는 ID를 넘겨야 한다.

## Controller / Swagger 계획

추가 파일:
- `character/controller/CharacterController.java`
- `character/controller/CharacterApiDocs.java`

매핑:

```java
@RequestMapping("/v1/characters")
```

```java
@GetMapping("/me")
@PatchMapping("/me/name")
@GetMapping("/me/xp-history")
```

인증:
- 3개 API 모두 로그인 필요.
- `Authentication.getName()`을 `Long userId`로 변환하는 기존 컨트롤러 패턴을 따른다.

응답:
- 성공 응답은 `ResponseEntity<ApiResponse<...>>`를 사용한다.
- 실패 응답은 기존 `BusinessException + ErrorCode + GlobalExceptionHandler` 흐름을 따른다.

## 예외 처리

기존:
- `CharacterErrorCode.CHARACTER_NOT_FOUND`
- `CharacterErrorCode.INVALID_CHARACTER_STATE`
- `CommonErrorCode.VALIDATION_FAILED`

추가 검토:
- `INVALID_CHARACTER_NAME`: 이름 정책을 validation annotation만으로 표현하기 어려우면 추가한다.
- `INVALID_XP_SOURCE_TYPE`: `sourceType` query를 enum으로 받을 경우 공통 validation으로 충분할 수 있다.

MVP 오류:
- 캐릭터 없음: 404 `CHARACTER_NOT_FOUND`
- 이름 검증 실패: 400 `VALIDATION_FAILED`
- 인증 없음/토큰 오류: 기존 Spring Security 401 응답

## 다른 도메인과의 연결점

User/Auth:
- 회원가입 직후 캐릭터를 만들지, 온보딩 완료 시 만들지 결정해야 한다.
- 권장: 회원가입 직후 생성.
- 이유: 로그인 후 메인/마이페이지에서 캐릭터 요약을 바로 조회할 수 있고, 온보딩 완료 여부와 캐릭터 존재 여부가 얽히지 않는다.

Diet/Nutrition:
- 식단 기록 생성/수정/삭제가 들어오면 나중에 경험치와 mood/streak를 갱신한다.
- 이번 Character API 구현에서 diet 기능을 같이 만들지는 않는다.
- 오늘 칼로리, 건강 점수, 서버 기준 날짜, 식단 기반 상태 문구는 캐릭터 단일 조회 API에 넣지 않는다.
- 메인 화면에서 캐릭터 + 식단 + 영양 + 퀘스트/보스 요약이 함께 필요하면 별도 홈 조합 API를 만든다.

Home:
- 후보 API: `GET /api/v1/home/me`
- 이 API는 같은 서버의 `/characters/me` 같은 HTTP API를 다시 호출하지 않는다.
- `HomeController -> HomeService -> CharacterService/NutritionService/DietService/...` 흐름으로 내부 서비스 또는 repository를 조합한다.
- `HomeService`는 화면 응답 조립자 역할만 하고, 캐릭터 성장/영양 점수/식단 집계 규칙은 각 도메인 서비스가 담당한다.
- 홈 응답에서만 `statusMessage`, `healthScore`, `todayCalories`, `targetCalories`, `date` 같은 화면 조합 값을 내려준다.

Boss/Admin:
- 보스 기여 보상과 관리자 지급은 나중에 `CharacterGrowthService.addXp(...)`를 호출한다.
- 클라이언트가 보상 XP를 직접 보내지 않는다.
- `BOSS`, `ADMIN` 모두 중복 방지를 위한 `source_id`를 반드시 제공한다.

## 구현 순서

1. 테스트 스키마 정리
   - `src/test/resources/test-auth-schema.sql`에 `xp_histories`가 없으면 추가한다.
   - `characters` 컬럼이 `01-init.sql`과 같은지 맞춘다.
   - `characters.best_streak_days`를 추가한다.
   - `xp_histories.source_id`를 `NOT NULL`로 맞춘다.
   - `xp_histories(character_id, source_type, source_id)` unique constraint를 추가한다.

2. Entity/Enum 추가
   - `Character`
   - `XpHistory`
   - `CharacterStage`
   - `CharacterMood`
   - `CharacterAppearanceType`
   - `XpSourceType`

3. Repository/Mapper 추가
   - `CharacterRepository`
   - `XpHistoryRepository`
   - `CharacterMapper.xml`
   - `XpHistoryMapper.xml`

4. DTO 추가
   - request/response DTO 작성
   - 이름 수정 request validation 작성

5. Service 구현
   - 내 캐릭터 조회
   - 이름 수정
   - 경험치 이력 조회
   - 기본 캐릭터 자동 생성 내부 메서드

6. Controller/Swagger 구현
   - `CharacterController`
   - `CharacterApiDocs`

7. User/Auth 연결
   - 회원가입 성공 후 `createDefaultCharacterIfMissing` 호출
   - 기존 Auth 테스트가 깨지지 않도록 캐릭터 의존성 주입과 mock을 함께 정리

8. 테스트 작성
   - Controller 테스트
   - Service 테스트
   - Repository 테스트
   - SecurityConfig 테스트에서 `/v1/characters/me/**` 인증 필요 여부 확인

## 테스트 계획

Controller:
- `GET /v1/characters/me` 성공
- `PATCH /v1/characters/me/name` 성공
- `PATCH /v1/characters/me/name` blank 실패
- `GET /v1/characters/me/xp-history` 성공
- 인증 없을 때 401

Service:
- 캐릭터 조회 성공
- 캐릭터 없을 때 `CHARACTER_NOT_FOUND`
- 이름 수정 시 trim 적용
- 캐릭터 조회 시 requiredXp/xpProgressRate 계산
- 경험치 이력 pagination 계산
- XP 중복 지급 시 `DuplicateKeyException`을 멱등하게 무시

Repository:
- `characters` 저장/조회
- 이름 수정
- 성장 상태 수정
- `xp_histories` 저장
- `xp_histories` 중복 source unique constraint 검증
- 사용자별 이력 조회
- 사용자별 이력 count

실행:

```powershell
cd BackEnd
mvn test
```

## 완료 기준

- 위 3개 API가 Controller에 매핑된다.
- 모든 API가 인증된 사용자 ID 기준으로만 동작한다.
- 성공 응답은 공통 `ApiResponse` 형식을 따른다.
- 캐릭터가 없을 때 404가 반환된다.
- 이름 수정 validation 실패 시 공통 validation error shape가 유지된다.
- `characters.best_streak_days`가 `streak_days`와 함께 갱신된다.
- `xp_histories.source_id`는 `NOT NULL`이고, `(character_id, source_type, source_id)` 중복 지급이 차단된다.
- `XpSourceType`은 `DIET`, `BOSS`, `ADMIN`만 허용한다.
- `characters`, `xp_histories` mapper namespace와 repository interface가 일치한다.
- `BackEnd/`에서 `mvn test`가 통과한다.
