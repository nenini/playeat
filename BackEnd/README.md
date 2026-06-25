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

## DB 초기화 스크립트

`docker-compose up`으로 MySQL 볼륨이 처음 생성될 때 `scripts/` 루트의 SQL 파일이 알파벳 순서대로 실행된다.

실행 대상:

- `scripts/init.sql`: 최종 스키마 생성
- `scripts/seed-data.sql`: 코치, 공식 영양 기준, 아이템, 보스/퀘스트, 음식 샘플 seed

이미 볼륨이 존재하는 경우에는 MySQL `docker-entrypoint-initdb.d`가 재실행되지 않는다. 이때 기존 데이터를 유지한 채 seed만 다시 넣어야 한다면 아래처럼 실행한다.

한글 seed 데이터가 깨지지 않도록 `utf8mb4` 문자셋으로 실행한다.

```bash
docker exec -i nyamnyam-mysql mysql --default-character-set=utf8mb4 -u root -p비밀번호 nyamnyam < scripts/seed-data.sql
```

스키마까지 새로 맞춰야 하는 경우에는 기존 DB 볼륨 삭제가 필요할 수 있다. `docker compose down -v`는 DB 데이터를 삭제하므로, 기존 데이터가 필요 없는 경우에만 사용한다.

## Verification

```powershell
mvn test
```
