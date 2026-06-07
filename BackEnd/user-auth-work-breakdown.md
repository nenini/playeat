# User & Auth 1차 구현 작업 분해 및 Git 이슈 전략

작성일: 2026-06-07

## 목표

1차 구현 범위는 **인증이 필요한 API들을 만들기 전에 반드시 필요한 User & Auth 기반**만 만든다.

이번 단계의 저장소 기준은 다음과 같다.

- 사용자 기본 정보: MySQL + MyBatis
- Refresh token: Redis

## 1차 구현에 포함할 범위

필수 포함:

- `users` 테이블 작성
- User Entity 작성
- User Repository와 MyBatis Mapper 작성
- RefreshToken Redis Repository 작성
- 회원가입 API
- 로그인 API
- 토큰 재발급 API
- 로그아웃 API
- 비밀번호 BCrypt 암호화
- JWT 발급 및 검증 흐름 연결
- 공개 API와 보호 API 접근 규칙 정리
- 인증 실패 공통 응답 검증

## 1차 구현에서 제외할 범위

이번 단계에서는 아래 작업을 하지 않는다.

- `refresh_tokens` MySQL 테이블
- RefreshToken MyBatis Mapper
- RefreshToken MySQL Entity
- 건강 프로필 등록, 조회, 수정
- 온보딩 완료 처리
- 내 회원 정보 수정
- 마이페이지 상세 정보
- 캐릭터 자동 생성
- 음식 검색
- 식단 기록
- 영양 분석
- AI 코칭
- 길드, 보스, 퀘스트, 상점, 아이템, 코인
- 권한 등급 분리
- 소셜 로그인
- 이메일 인증
- 비밀번호 찾기

내 정보 조회 API는 선택 사항이다. 프론트에서 로그인 직후 사용자 식별이 필요하면 최소 응답을 로그인 응답에 포함하고, `/api/v1/users/me`는 다음 단계로 미룬다.

## 현재 백엔드 기준

- 빌드 도구: Maven
- Java: 17
- Spring Boot: 3.3.5
- 기본 패키지: `com.nyamnyam.coach`
- API context path: `/api`
- API version prefix: `/v1`
- 최종 API prefix: `/api/v1`
- User 저장소: MySQL + MyBatis
- Refresh token 저장소: Redis + `StringRedisTemplate`
- 인증 관련 기존 파일:
  - `BackEnd/src/main/java/com/nyamnyam/coach/auth/jwt/JwtTokenProvider.java`
  - `BackEnd/src/main/java/com/nyamnyam/coach/auth/jwt/JwtAuthenticationFilter.java`
  - `BackEnd/src/main/java/com/nyamnyam/coach/auth/jwt/JwtToken.java`
  - `BackEnd/src/main/java/com/nyamnyam/coach/global/config/SecurityConfig.java`
- 공통 응답 및 예외 기존 파일:
  - `BackEnd/src/main/java/com/nyamnyam/coach/global/response/ApiResponse.java`
  - `BackEnd/src/main/java/com/nyamnyam/coach/global/response/ErrorResponse.java`
  - `BackEnd/src/main/java/com/nyamnyam/coach/global/exception/BusinessException.java`
  - `BackEnd/src/main/java/com/nyamnyam/coach/global/exception/errorcode/AuthErrorCode.java`
  - `BackEnd/src/main/java/com/nyamnyam/coach/global/exception/errorcode/UserErrorCode.java`

## 노션 API 목록 대조 결과

노션 `API 명세 (1)`의 Auth API는 다음 4개다.

| 기능 | 노션 API | 이번 문서 반영 |
|---|---|---|
| 회원가입 | `POST /api/v1/auth/signup` | 포함 |
| 로그인 | `POST /api/v1/auth/login` | 포함 |
| 로그아웃 | `POST /api/v1/auth/logout` | 포함 |
| 토큰 재발급 | `POST /api/v1/auth/refresh` | 포함 |

노션의 User API는 다음 단계 범위로 둔다.

- `GET /api/v1/users/me`
- `PATCH /api/v1/users/me`
- `DELETE /api/v1/users/me`
- `POST /api/v1/users/me/onboarding`
- `GET /api/v1/users/me/health-profile`
- `PATCH /api/v1/users/me/health-profile`

따라서 이번 1차 User & Auth 기반 문서에서는 Auth 4개 API만 구현 대상으로 잡고, User 상세 조회/수정/탈퇴/온보딩/건강 프로필 API는 제외한다.

## 작업 분해 원칙

이번 범위는 작게 유지한다.

작업은 다음 3개 이슈로 나눈다.

1. User MySQL 기반과 RefreshToken Redis 저장소
2. 회원가입, 로그인, 토큰 API
3. Security 접근 규칙과 통합 검증

DTO만 따로 이슈로 만들지 않는다. DTO는 해당 API 이슈 안에 포함한다.

SQL만 따로 오래 끌지 않는다. SQL은 Repository와 Mapper가 동작하는 최소 단위까지 함께 묶는다.

## Git 이슈명 전략

이슈명 형식:

```text
[BE][Auth] 작업 내용
```

이번 1차 구현은 User 작업도 인증 기반에 포함되므로 이슈 라벨은 `[Auth]`로 통일한다. Refresh token은 MySQL 테이블이 아니라 Redis에 저장하므로 별도 `[DB]` 이슈로 분리하지 않는다.

## Git 브랜치명 전략

브랜치명 형식:

```text
feature/{issue-number}-{short-purpose}
```

팀 컨벤션상 `feature/#3-auth-storage`처럼 `#`를 넣어도 된다. PowerShell에서는 반드시 따옴표로 감싸서 사용한다.

```powershell
git switch -c "feature/#3-auth-storage"
```

## 추천 이슈 및 브랜치 목록

| 순서 | Git 이슈명 | 브랜치명 | 핵심 범위 |
|---:|---|---|---|
| 1 | `[BE][Auth] User/Auth 인증 기반 저장소 작성` | `feature/#3-auth-storage` | `users`, User Entity/Repository/Mapper, RefreshToken Redis Repository |
| 2 | `[BE][Auth] 회원가입 로그인 토큰 API 구현` | `feature/#4-auth-api` | 회원가입, 로그인, 토큰 재발급, 로그아웃 |
| 3 | `[BE][Auth] 인증 접근 제어 및 통합 검증` | `feature/#5-auth-security-test` | SecurityConfig 정리, 인증 실패 응답, auth 흐름 테스트 |

## Issue 1. `[BE][Auth] User/Auth 인증 기반 저장소 작성`

브랜치:

```text
feature/#3-auth-storage
```

목적:

- 회원가입, 로그인, 토큰 재발급에 필요한 저장소 기반만 만든다.
- User는 MySQL에 저장한다.
- Refresh token은 Redis에 저장한다.

작업 범위:

- `users` 테이블 작성
- User Entity 작성
- UserRepository 작성
- User MyBatis Mapper XML 작성
- RefreshTokenRepository 작성
- User Repository 테스트 작성
- RefreshToken Redis Repository 테스트 작성

생성 또는 수정할 파일:

- `BackEnd/scripts/init.sql`
- `BackEnd/src/main/resources/schema.sql`
- `BackEnd/src/main/java/com/nyamnyam/coach/user/entity/User.java`
- `BackEnd/src/main/java/com/nyamnyam/coach/user/repository/UserRepository.java`
- `BackEnd/src/main/resources/mappers/user/UserMapper.xml`
- `BackEnd/src/main/java/com/nyamnyam/coach/auth/repository/RefreshTokenRepository.java`
- `BackEnd/src/test/java/com/nyamnyam/coach/user/repository/UserRepositoryTest.java`
- `BackEnd/src/test/java/com/nyamnyam/coach/auth/repository/RefreshTokenRepositoryTest.java`
- `BackEnd/src/test/resources/test-user-schema.sql`

제외 파일:

- `BackEnd/src/main/java/com/nyamnyam/coach/auth/entity/RefreshToken.java`
- `BackEnd/src/main/resources/mappers/auth/RefreshTokenMapper.xml`
- `BackEnd/src/test/resources/test-refresh-token-schema.sql`

Refresh token을 Redis로 저장하므로 위 파일들은 만들지 않는다. `schema.sql`, `init.sql`, 테스트 SQL에도 `refresh_tokens` 테이블을 추가하지 않는다.

권장 `users` SQL:

```sql
CREATE TABLE users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    nickname VARCHAR(50) NOT NULL,
    profile_image_url VARCHAR(500),
    selected_coach_id BIGINT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    onboarding_completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deactivated_at DATETIME,
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_nickname UNIQUE (nickname)
);
```

UserRepository 최소 메서드:

- `save`
- `findById`
- `findByEmail`
- `existsByEmail`
- `existsByNickname`

RefreshTokenRepository 최소 메서드:

- `save(Long userId, String tokenHash, Duration ttl)`
- `findUserIdByTokenHash(String tokenHash)`
- `revokeByTokenHash(String tokenHash)`
- `revokeAllByUserId(Long userId)`

Redis key 설계:

```text
refresh:{tokenHash} -> userId
user-refresh-tokens:{userId} -> tokenHash set
```

Redis 동작:

- 로그인 성공 시 `refresh:{tokenHash}`에 `userId`를 저장한다.
- 저장할 때 refresh token 만료 시간만큼 TTL을 설정한다.
- 사용자별 전체 로그아웃을 위해 `user-refresh-tokens:{userId}` set에 token hash를 추가한다.
- 단일 로그아웃은 `refresh:{tokenHash}`를 삭제한다.
- 전체 로그아웃은 `user-refresh-tokens:{userId}`의 모든 token hash를 조회해 관련 key를 삭제한다.

완료 기준:

- H2 MySQL 모드에서 `users` 테이블 생성이 가능하다.
- 사용자 저장이 가능하다.
- 이메일로 사용자 조회가 가능하다.
- 이메일 중복 조회가 가능하다.
- 닉네임 중복 조회가 가능하다.
- refresh token hash를 Redis에 TTL과 함께 저장할 수 있다.
- refresh token hash로 userId를 조회할 수 있다.
- refresh token 하나를 삭제할 수 있다.
- 사용자의 모든 refresh token을 삭제할 수 있다.

검증 명령:

```powershell
cd BackEnd
mvn test "-Dtest=UserRepositoryTest,RefreshTokenRepositoryTest"
```

## Issue 2. `[BE][Auth] 회원가입 로그인 토큰 API 구현`

브랜치:

```text
feature/#4-auth-api
```

GitLab 이슈 제목:

```text
[BE][Auth] 회원가입 로그인 토큰 API 구현
```

라벨:

```text
backend ~feature
```

### GitLab 이슈 내용

````markdown
## 기능 설명

인증이 필요한 백엔드 API를 구현하기 전에 사용할 회원가입, 로그인, access token 재발급, 로그아웃 API를 구현한다.

사용자 계정 정보는 MySQL의 `users` 테이블에 저장하고, refresh token은 원문이 아니라 hash 값으로 변환해 Redis에 저장한다. 로그인 성공 시 access token과 refresh token을 발급하고, 토큰 재발급 시 기존 refresh token을 폐기한 뒤 새 refresh token을 저장한다. 로그아웃 시 전달받은 refresh token hash에 해당하는 Redis key를 삭제한다.

## 작업 상세 내용

- Entity / Domain 설계
  - 기존 User Entity를 사용한다.
  - 별도 RefreshToken Entity는 만들지 않는다.
  - refresh token은 Redis 기반 `RefreshTokenRepository`로 관리한다.
- DTO 작성
  - `SignupRequest`
  - `LoginRequest`
  - `TokenRefreshRequest`
  - `LogoutRequest`
  - `SignupResponse`
  - `LoginResponse`
  - `TokenRefreshResponse`
- Repository 작성
  - 기존 `UserRepository`를 사용한다.
  - 기존 `RefreshTokenRepository`를 사용한다.
- Service 로직 구현
  - 회원가입 시 이메일, 닉네임 중복을 검증한다.
  - 비밀번호는 BCrypt로 암호화해 저장한다.
  - 로그인 시 이메일과 비밀번호를 검증한다.
  - 로그인 성공 시 access token과 refresh token을 발급한다.
  - refresh token은 hash로 변환해 Redis에 TTL과 함께 저장한다.
  - 토큰 재발급 시 refresh token 유효성을 확인하고 기존 refresh token을 삭제한 뒤 새 refresh token을 저장한다.
  - 로그아웃 시 요청 refresh token에 해당하는 Redis key를 삭제한다.
- Controller 구현
  - Spring `server.servlet.context-path`가 `/api`이므로 컨트롤러 base path는 `/v1/auth`로 작성한다.
  - `/api/v1/auth/signup`
  - `/api/v1/auth/login`
  - `/api/v1/auth/refresh`
  - `/api/v1/auth/logout`
- 예외 처리
  - 중복 이메일: `EMAIL_ALREADY_EXISTS`
  - 중복 닉네임: `NICKNAME_ALREADY_EXISTS`
  - 로그인 실패: `INVALID_CREDENTIALS`
  - refresh token 누락 또는 유효하지 않은 토큰: Auth 관련 ErrorCode 사용
  - 비즈니스 예외는 `BusinessException`으로 처리한다.
- API 문서화
  - Swagger에서 auth API 4개가 보이도록 `AuthController`에 OpenAPI annotation을 작성한다.
  - 요청/응답 DTO에 `@Schema` 설명과 예시 값을 작성한다.
  - 각 API의 성공 응답과 주요 실패 응답을 `@ApiResponse`로 문서화한다.
  - 로그아웃 API는 access token 인증이 필요하다는 점을 Swagger에 표시한다.
  - `BackEnd/README.md`에는 Swagger 접속 경로와 auth API 실행 순서를 짧게 정리한다.
- 테스트
  - `AuthControllerTest`
  - `AuthServiceTest`
  - 회원가입 성공/실패
  - 로그인 성공/실패
  - 토큰 재발급 성공/실패
  - 로그아웃 성공
  - refresh token Redis 저장, TTL 설정, 삭제 검증

## API 명세

| Method | URL | 설명 |
|---|---|---|
| POST | `/api/v1/auth/signup` | 회원가입 |
| POST | `/api/v1/auth/login` | 로그인 |
| POST | `/api/v1/auth/refresh` | access token 재발급 |
| POST | `/api/v1/auth/logout` | 로그아웃 |

### POST `/api/v1/auth/signup`

Request

```json
{
  "email": "user@example.com",
  "password": "password1234",
  "nickname": "냠냠이"
}
```

Response

```json
{
  "success": true,
  "data": {
    "userId": 1,
    "email": "user@example.com",
    "nickname": "냠냠이"
  },
  "message": "회원가입이 완료되었습니다."
}
```

### POST `/api/v1/auth/login`

Request

```json
{
  "email": "user@example.com",
  "password": "password1234"
}
```

Response

```json
{
  "success": true,
  "data": {
    "accessToken": "access-token-value",
    "refreshToken": "refresh-token-value",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "user": {
      "userId": 1,
      "email": "user@example.com",
      "nickname": "냠냠이",
      "onboardingCompleted": false
    }
  },
  "message": "로그인에 성공했습니다."
}
```

### POST `/api/v1/auth/refresh`

Request

```json
{
  "refreshToken": "refresh-token-value"
}
```

Response

```json
{
  "success": true,
  "data": {
    "accessToken": "new-access-token-value",
    "refreshToken": "new-refresh-token-value",
    "tokenType": "Bearer",
    "expiresIn": 3600
  },
  "message": "토큰이 재발급되었습니다."
}
```

### POST `/api/v1/auth/logout`

Request Header

```http
Authorization: Bearer access-token-value
```

Request

```json
{
  "refreshToken": "refresh-token-value"
}
```

Response

```json
{
  "success": true,
  "data": {
    "loggedOutAt": "2026-05-26T10:30:00"
  },
  "message": "로그아웃되었습니다."
}
```

## 참고 사항

- refresh token은 MySQL에 저장하지 않는다.
- refresh token 원문은 저장하지 않고 hash 값만 Redis에 저장한다.
- Redis key 형식은 `refresh:{tokenHash}`를 사용한다.
- refresh token 저장 시 만료 시간과 동일한 TTL을 설정한다.
- 토큰 재발급 성공 시 refresh token rotation을 적용한다.
- 프론트는 로그인 응답의 `tokenType`, `expiresIn`, `accessToken`, `refreshToken`, `user`를 사용한다.
- `/api/v1/auth/logout`은 access token 인증이 필요한 보호 API로 두고, body의 `refreshToken`으로 현재 refresh token 하나를 폐기한다.
- 공통 응답은 기존 `ApiResponse` 형식을 따른다.
- 실패 응답은 기존 `ErrorResponse` 형식을 따른다.
````

목적:

- 인증이 필요한 API를 만들기 전에 사용할 회원가입, 로그인, 토큰 재발급, 로그아웃 API를 완성한다.

작업 범위:

- 요청 DTO 작성
- 응답 DTO 작성
- AuthController 작성
- AuthService 작성
- PasswordEncoder 사용
- JwtTokenProvider 사용
- UserRepository 사용
- RefreshTokenRepository 사용
- API 테스트 작성

생성 또는 수정할 파일:

- `BackEnd/src/main/java/com/nyamnyam/coach/auth/controller/AuthController.java`
- `BackEnd/src/main/java/com/nyamnyam/coach/auth/service/AuthService.java`
- `BackEnd/src/main/java/com/nyamnyam/coach/auth/dto/request/SignupRequest.java`
- `BackEnd/src/main/java/com/nyamnyam/coach/auth/dto/request/LoginRequest.java`
- `BackEnd/src/main/java/com/nyamnyam/coach/auth/dto/request/TokenRefreshRequest.java`
- `BackEnd/src/main/java/com/nyamnyam/coach/auth/dto/request/LogoutRequest.java`
- `BackEnd/src/main/java/com/nyamnyam/coach/auth/dto/response/SignupResponse.java`
- `BackEnd/src/main/java/com/nyamnyam/coach/auth/dto/response/AuthUserResponse.java`
- `BackEnd/src/main/java/com/nyamnyam/coach/auth/dto/response/LoginResponse.java`
- `BackEnd/src/main/java/com/nyamnyam/coach/auth/dto/response/TokenRefreshResponse.java`
- `BackEnd/src/main/java/com/nyamnyam/coach/auth/dto/response/LogoutResponse.java`
- `BackEnd/README.md`
- `BackEnd/src/test/java/com/nyamnyam/coach/auth/controller/AuthControllerTest.java`
- `BackEnd/src/test/java/com/nyamnyam/coach/auth/service/AuthServiceTest.java`

Swagger 문서화:

- `AuthController`에 `@Tag(name = "Auth", description = "회원가입, 로그인, 토큰 재발급, 로그아웃 API")`를 작성한다.
- `AuthController`의 base path는 `/v1/auth`로 작성해 최종 URL이 노션 명세의 `/api/v1/auth/...`와 일치하게 한다.
- 각 API 메서드에 `@Operation(summary = "...", description = "...")`를 작성한다.
- 각 API 메서드에 성공/실패 응답을 `@ApiResponses`로 작성한다.
- 요청 DTO와 응답 DTO 필드에 `@Schema(description = "...", example = "...")`를 작성한다.
- 로그아웃 API에는 access token 인증 필요 여부를 `@SecurityRequirement` 또는 프로젝트의 기존 Swagger 보안 설정 방식에 맞춰 표시한다.
- Swagger에서 확인할 수 있어야 하는 API:
  - `POST /api/v1/auth/signup`
  - `POST /api/v1/auth/login`
  - `POST /api/v1/auth/refresh`
  - `POST /api/v1/auth/logout`
- Swagger 접속 경로:
  - `/api/swagger-ui.html`
  - `/api/swagger-ui/**`
  - `/api/v3/api-docs/**`

API:

| Method | Path | 설명 | 인증 |
|---|---|---|---|
| POST | `/api/v1/auth/signup` | 회원가입 | 불필요 |
| POST | `/api/v1/auth/login` | 로그인 | 불필요 |
| POST | `/api/v1/auth/refresh` | access token 재발급 | refresh token 필요 |
| POST | `/api/v1/auth/logout` | 로그아웃 | access token 필요 |

요청 DTO:

- `SignupRequest`
  - `email`
  - `password`
  - `nickname`
- `LoginRequest`
  - `email`
  - `password`
- `TokenRefreshRequest`
  - `refreshToken`
- `LogoutRequest`
  - `refreshToken`

응답 DTO:

- `SignupResponse`
  - `userId`
  - `email`
  - `nickname`
  - `onboardingCompleted`
  - `createdAt`
- `LoginResponse`
  - `accessToken`
  - `refreshToken`
  - `tokenType`
  - `expiresIn`
  - `user`
- `TokenRefreshResponse`
  - `accessToken`
  - `refreshToken`
  - `tokenType`
  - `expiresIn`
- `LogoutResponse`
  - `loggedOutAt`

필수 검증:

- 회원가입 이메일은 이메일 형식이어야 한다.
- 회원가입 비밀번호는 최소 길이를 검증한다.
- 회원가입 닉네임은 비어 있으면 안 된다.
- 로그인 이메일과 비밀번호는 비어 있으면 안 된다.
- refresh token은 비어 있으면 안 된다.

완료 기준:

- 중복 이메일이면 `EMAIL_ALREADY_EXISTS`를 반환한다.
- 중복 닉네임이면 `NICKNAME_ALREADY_EXISTS`를 반환한다.
- 비밀번호는 BCrypt로 저장한다.
- 로그인 실패 시 `INVALID_CREDENTIALS`를 반환한다.
- 로그인 성공 시 access token과 refresh token을 반환한다.
- refresh token은 원문 대신 hash로 Redis에 저장한다.
- refresh token 저장 시 TTL을 설정한다.
- 재발급 성공 시 기존 refresh token은 삭제하고 새 refresh token을 저장한다.
- 로그아웃 성공 시 refresh token key는 삭제된다.
- 컨트롤러 응답은 `ApiResponse`로 감싼다.
- Swagger에 회원가입, 로그인, 토큰 재발급, 로그아웃 API의 요청/응답 예시가 표시된다.
- Swagger에 주요 실패 응답 코드가 표시된다.
- `BackEnd/README.md`에 Swagger 접속 경로와 auth API 실행 순서가 정리된다.

검증 명령:

```powershell
cd BackEnd
mvn test "-Dtest=AuthControllerTest,AuthServiceTest"
```

## Issue 3. `[BE][Auth] 인증 접근 제어 및 통합 검증`

브랜치:

```text
feature/#5-auth-security-test
```

목적:

- 인증이 필요한 API를 앞으로 만들 수 있도록 Security 접근 규칙과 인증 실패 응답을 정리한다.
- auth 전체 흐름을 통합 테스트로 검증한다.

작업 범위:

- `SecurityConfig` 공개 URL 정리
- JWT 필터 동작 검증
- 인증 실패 응답 검증
- Swagger 접근 허용 확인
- 회원가입 → 로그인 → 토큰 재발급 → 로그아웃 통합 테스트 작성

생성 또는 수정할 파일:

- `BackEnd/src/main/java/com/nyamnyam/coach/global/config/SecurityConfig.java`
- `BackEnd/src/main/java/com/nyamnyam/coach/global/security/CustomAuthenticationEntryPoint.java`
- `BackEnd/src/main/java/com/nyamnyam/coach/global/security/CustomAccessDeniedHandler.java`
- `BackEnd/src/test/java/com/nyamnyam/coach/auth/AuthFlowIntegrationTest.java`
- `BackEnd/src/test/java/com/nyamnyam/coach/global/security/SecurityConfigTest.java`
- `BackEnd/README.md`

공개 API:

- `POST /api/v1/auth/signup`
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`
- `GET /api/swagger-ui.html`
- `GET /api/swagger-ui/**`
- `GET /api/v3/api-docs/**`
- `GET /api/actuator/health`

보호 API:

- `POST /api/v1/auth/logout`
- 앞으로 추가될 모든 사용자, 식단, 분석, 캐릭터 API

통합 테스트 흐름:

1. 회원가입 요청이 성공한다.
2. 로그인 요청이 성공하고 access token과 refresh token이 발급된다.
3. refresh token으로 토큰 재발급이 성공한다.
4. access token 없이 보호 API를 호출하면 인증 실패 응답이 반환된다.
5. 로그아웃 요청이 성공한다.
6. 로그아웃 이후 같은 refresh token으로 재발급하면 실패한다.

완료 기준:

- 인증 없이 보호 API를 호출하면 `success=false` 응답을 반환한다.
- 잘못된 토큰으로 보호 API를 호출하면 `success=false` 응답을 반환한다.
- Swagger 경로는 로그인 없이 접근 가능하다.
- 기존 `SecurityConfig`를 중복 생성하지 않는다.
- `mvn test`에서 auth 관련 테스트가 통과한다.
- `BackEnd/README.md`에 auth API 실행 순서가 짧게 정리된다.

검증 명령:

```powershell
cd BackEnd
mvn test "-Dtest=AuthFlowIntegrationTest,SecurityConfigTest"
```

## 권장 진행 순서

1. `feature/#3-auth-storage`
2. `feature/#4-auth-api`
3. `feature/#5-auth-security-test`

이 순서가 좋은 이유:

- User DB와 Mapper가 먼저 있어야 회원가입과 로그인 구현이 안정적이다.
- RefreshToken Redis Repository가 먼저 있어야 로그인, 재발급, 로그아웃 구현이 단순하다.
- 회원가입과 로그인이 있어야 토큰 재발급과 로그아웃 흐름을 검증할 수 있다.
- Security 정리는 실제 auth API가 생긴 뒤 해야 공개/보호 경로를 검증할 수 있다.

## GitLab 이슈 설명 템플릿

```markdown
## 목적

이 이슈에서 만들 인증 기반 동작을 설명한다.

## 작업 범위

- SQL 또는 Redis 저장소
- DTO
- Service
- Controller
- Test

## API

| Method | Path | 설명 | 인증 |
|---|---|---|---|
| POST | `/api/v1/auth/signup` | 회원가입 | 불필요 |

## 완료 기준

- 성공 케이스가 테스트된다.
- 실패 케이스가 테스트된다.
- 성공 응답은 `ApiResponse` 형식을 따른다.
- 실패 응답은 `ErrorResponse` 형식을 따른다.
- 이슈별 테스트 명령이 통과한다.
```

## 커밋 전략

한 이슈 안에서는 다음 순서로 커밋한다.

1. SQL, Entity, Repository, Mapper 또는 Redis Repository
2. DTO
3. Service
4. Controller
5. Test
6. README 또는 API 실행 순서

커밋 메시지 예시:

```text
feat: add user auth storage
feat: add signup and login api
feat: add refresh and logout api
test: add auth flow tests
docs: add auth api guide
```

## 최종 체크리스트

- `users` 테이블에 실제 비밀번호 원문이 저장되지 않는다.
- refresh token은 원문 저장 대신 hash로 Redis에 저장한다.
- refresh token에는 TTL을 설정한다.
- 로그아웃 시 refresh token key를 삭제한다.
- 모든 성공 응답은 `ApiResponse`로 감싼다.
- 모든 비즈니스 예외는 `BusinessException`과 `AuthErrorCode` 또는 `UserErrorCode`를 사용한다.
- 회원가입, 로그인, 토큰 재발급, 로그아웃이 테스트된다.
- 인증이 필요한 API를 만들기 전에 사용할 access token 발급 흐름이 준비된다.
- 실제 DB 비밀번호, JWT secret, 개인 토큰은 Git에 커밋하지 않는다.
