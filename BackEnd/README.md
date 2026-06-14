# NyamNyam Coach Backend

## Swagger

애플리케이션 실행 후 Swagger UI에서 API를 확인할 수 있다.

- Swagger UI: `/api/swagger-ui.html`
- OpenAPI JSON: `/api/v3/api-docs`

## Auth API 실행 순서

1. `POST /api/v1/auth/signup`
   - `email`, `password`, `nickname`으로 회원가입한다.
2. `POST /api/v1/auth/login`
   - `accessToken`, `refreshToken`, `tokenType`, `expiresIn`, `user`를 받는다.
3. `POST /api/v1/auth/refresh`
   - body에 `refreshToken`을 담아 새 토큰을 발급받는다.
4. `POST /api/v1/auth/logout`
   - `Authorization: Bearer {accessToken}` 헤더와 body의 `refreshToken`을 함께 보내 현재 refresh token을 폐기한다.

## Boss Seed 실행

한글 seed 데이터가 깨지지 않도록 `utf8mb4` 문자셋으로 실행한다.

```bash
docker exec -i nyamnyam-mysql mysql --default-character-set=utf8mb4 -u root -p nyamnyam < scripts/08-boss-sugar-dragon-seed.sql
```

비밀번호를 바로 붙여 실행하는 경우:

```bash
docker exec -i nyamnyam-mysql mysql --default-character-set=utf8mb4 -u root -p비밀번호 nyamnyam < scripts/08-boss-sugar-dragon-seed.sql
```

## Migration 실행 순서

기존 볼륨을 유지한 상태에서 현재 브랜치까지 DB를 갱신하려면 아래 순서로 실행한다.

```bash
docker exec -i nyamnyam-mysql mysql --default-character-set=utf8mb4 -u root -p비밀번호 nyamnyam < scripts/07-boss-pr4-migration.sql
docker exec -i nyamnyam-mysql mysql --default-character-set=utf8mb4 -u root -p비밀번호 nyamnyam < scripts/08-boss-sugar-dragon-seed.sql
docker exec -i nyamnyam-mysql mysql --default-character-set=utf8mb4 -u root -p비밀번호 nyamnyam < scripts/09-boss-battle-pr5-migration.sql
docker exec -i nyamnyam-mysql mysql --default-character-set=utf8mb4 -u root -p비밀번호 nyamnyam < scripts/09-food-domain-migration.sql
docker exec -i nyamnyam-mysql mysql --default-character-set=utf8mb4 -u root -p비밀번호 nyamnyam < scripts/10-foods-seed.sql
docker exec -i nyamnyam-mysql mysql --default-character-set=utf8mb4 -u root -p비밀번호 nyamnyam < scripts/10-quest-pr6-migration.sql
docker exec -i nyamnyam-mysql mysql --default-character-set=utf8mb4 -u root -p비밀번호 nyamnyam < scripts/11-boss-battle-balance-migration.sql
docker exec -i nyamnyam-mysql mysql --default-character-set=utf8mb4 -u root -p비밀번호 nyamnyam < scripts/12-guild-chat-pr9-migration.sql
docker exec -i nyamnyam-mysql mysql --default-character-set=utf8mb4 -u root -p비밀번호 nyamnyam < scripts/13-coin-shop-item-pr10-migration.sql
docker exec -i nyamnyam-mysql mysql --default-character-set=utf8mb4 -u root -p비밀번호 nyamnyam < scripts/14-ranking-dashboard-pr8-migration.sql
docker exec -i nyamnyam-mysql mysql --default-character-set=utf8mb4 -u root -p비밀번호 nyamnyam < scripts/15-quest-verification-reward-pr7-migration.sql
```

## 음식 데이터 초기 적재 (Food Seed)

`scripts/10-foods-seed.sql`이 저장소에 포함되어 있다.
`docker-compose up` 시 `docker-entrypoint-initdb.d`가 숫자 순서로 SQL을 자동 실행하므로 **별도 작업 없이 자동 적재**된다.

이미 볼륨이 존재하는 경우(컨테이너를 재시작하는 경우) initdb.d는 실행되지 않는다.
그럴 때는 아래 명령으로 수동 적재한다.

```bash
docker exec -i nyamnyam-mysql mysql --default-character-set=utf8mb4 -u root -p비밀번호 nyamnyam < scripts/10-foods-seed.sql
```

seed SQL은 `ON DUPLICATE KEY UPDATE`로 작성되어 있으므로 중복 실행해도 안전하다.

## Verification

```powershell
mvn test
```
