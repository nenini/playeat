# AGENT.md

## Project Context

This repository implements **NyamNyam Coach**, a diet tracking and AI coaching service where daily meal records drive nutrition analysis, character growth, quests, bosses, and guild motivation.

Primary product goal:
- Lower the effort of meal logging.
- Convert calorie and nutrient data into understandable feedback.
- Use character growth and rewards to keep users engaged.
- Provide AI coaching for the next meal or habit.
- Expand later into guild, boss, quest, shop, and item systems.

Source of truth:
- Notion project hub: https://app.notion.com/p/35fe3d80af7b803bb013fb9150f7822e
- Final planning page: "냠냠코치 최종 기획서"
- Folder convention page: "폴더 구조"
- Git convention page: "깃 컨벤션"

When Notion and the local repository disagree, prefer the local repository for concrete build/package decisions and keep the Notion intent for product/domain decisions.

## Current Repository Shape

Top-level folders:
- `BackEnd/`: Spring Boot backend. This is the currently implemented code area.
- `FrontEnd/`: Frontend placeholder. Vue implementation is expected but not scaffolded yet.
- `Readme/`: planning and project notes.
- `images/`: UI/reference images.

Backend reality as of this file:
- Build tool: Maven (`BackEnd/pom.xml`).
- Java: 17.
- Spring Boot: 3.3.5.
- Base package: `com.nyamnyam.coach`.
- Main class: `BackEnd/src/main/java/com/nyamnyam/coach/NyamNyamCoachApplication.java`.
- API context path: `/api`.
- MyBatis mapper location: `classpath:mappers/**/*.xml`.

Notion examples may mention Gradle or `com.yumyumcoach`; do not switch the repository to those names unless the team explicitly decides to migrate.

## Product Priority

Implement MVP first:
- Signup, login, logout.
- User health profile registration and update.
- Automatic NyamNyam character creation.
- Food database search.
- Meal record create, read, update, delete.
- Daily meal list.
- Nutrition analysis for calories, protein, carbs, fat, sodium, and related targets.
- Goal achievement rates.
- Character experience, level, and mood changes from meal records.
- AI coach one-line feedback.
- My page for member and character information.

Treat these as later expansion unless the current task explicitly targets them:
- Daily/weekly reports.
- Badges.
- Boss and quest systems.
- Guild creation, invite code, member contribution, and shared boss.
- Coach character/personality selection.
- Coins.
- Shop and equipped items.

## Backend Development Rules

Use the domain-based package structure under `com.nyamnyam.coach`:
- `auth`
- `user`
- `food`
- `diet`
- `nutrition`
- `character`
- `guild`
- `boss`
- `quest`
- `shop`
- `item`
- `coin`
- `ai`
- `coach`
- `global`

Inside a domain, keep the existing layer naming:
- `controller`
- `service`
- `repository`
- `entity`
- `dto/request`
- `dto/response`

Rules:
- Controllers should return `ResponseEntity<ApiResponse<...>>` for successful API responses.
- Business errors should use `BusinessException` and domain-specific `ErrorCode` enums under `global/exception/errorcode`.
- Validation failures should keep the common error response shape with `errors` only when validation actually fails.
- Spring Security authentication/authorization failures should keep using `CustomAuthenticationEntryPoint` and `CustomAccessDeniedHandler`.
- Do not duplicate `SecurityConfig`; merge new security behavior into the existing file.
- Repository interfaces should match MyBatis XML mapper namespaces and statement IDs.
- Keep secrets out of committed config. Use `application-local.example.yml` for examples and ignored local config files for real values.

Common success response shape:

```json
{
  "success": true,
  "data": {},
  "message": "요청이 성공했습니다."
}
```

Common error response shape:

```json
{
  "success": false,
  "code": "USER_NOT_FOUND",
  "message": "사용자를 찾을 수 없습니다."
}
```

Validation error response shape:

```json
{
  "success": false,
  "code": "VALIDATION_FAILED",
  "message": "입력값 검증에 실패했습니다.",
  "errors": [
    {
      "field": "email",
      "reason": "이메일 형식이 올바르지 않습니다."
    }
  ]
}
```

## Frontend Development Rules

The frontend is expected to use:
- Vue.js.
- Vue Router.
- Pinia.
- Axios.

When scaffolding or implementing `FrontEnd/`:
- Build actual app screens, not a marketing landing page.
- Match the domain screens from the planning docs: splash/login/signup, onboarding, home, diet, analysis, boss/guild, my page.
- Keep API calls in focused service/store modules instead of scattering fetch logic across components.
- Use the backend response envelope (`success`, `data`, `message`) consistently.
- Keep authentication token handling centralized.
- Keep UI copy and behavior aligned with meal logging, character growth, and coaching.

## AI Feature Rules

AI features should be practical and bounded:
- AI feedback must be short, understandable, and action-oriented.
- Feedback should be based on actual recorded meals, health goals, and nutrition targets.
- Do not expose prompt text, API keys, or raw provider errors to users.
- Keep AI integration under the `ai` and/or `coach` domains.
- Store generated results only when the API contract or product flow requires it.

## Environment And Secrets

Do not commit real secrets:
- DB passwords.
- JWT secrets.
- OpenAI or other AI provider keys.
- Personal `.env` files.
- Real local Spring profile files.

Files that should remain ignored:
- `BackEnd/src/main/resources/application-local.yml`
- `BackEnd/src/main/resources/application-local.yaml`
- `BackEnd/src/main/resources/application-dev.yml`
- `BackEnd/src/main/resources/application-dev.yaml`
- `.env`
- `.env.local`
- `FrontEnd/.env`
- `FrontEnd/.env.local`

Example files such as `application-local.example.yml` or `.env.example` should be committed when they contain placeholders only.

## Git Convention

Use the team's Notion convention:
- Keep `master` stable.
- Create feature branches from `develop`.
- Merge feature branches back into `develop`.
- Merge `develop` into `master` only after the team verifies the result.
- Pull before pushing and during long-running work.
- If a merge conflict is ambiguous, stop and ask the team.

Commit types:
- `feat`: new feature.
- `fix`: bug fix.
- `docs`: documentation change.
- `style`: formatting only.
- `refactor`: refactor.
- `test`: test code.
- `chore`: build, dependency, or tooling change.

Branch examples:
- `feature/#1-login-api`
- `bugfix/#12-token-refresh`
- `hotfix/#20-production-secret`
- `release/#30-final-demo`

## Verification

Before claiming backend work is complete, run the narrowest useful verification from `BackEnd/`:

```powershell
mvn test
```

For application startup checks:

```powershell
mvn spring-boot:run
```

If local MySQL, Redis, or secrets are missing, state exactly which prerequisite blocked verification.

For frontend work, add the package manager commands to `FrontEnd/README.md` when the frontend is scaffolded, then run the relevant build/test command before completion.