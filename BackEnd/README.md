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

## Verification

```powershell
mvn test
```
