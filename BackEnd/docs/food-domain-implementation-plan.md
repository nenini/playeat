# FOOD Domain Implementation Plan

## Scope

이번 이슈에서는 FOOD 도메인의 1차 기능만 구현한다.

구현 대상 API:

| Method | URL | 설명 |
| --- | --- | --- |
| GET | `/api/v1/foods` | 음식 검색 |
| GET | `/api/v1/foods/{foodId}` | 음식 상세 조회 |
| GET | `/api/v1/foods/frequent` | 자주 먹은 음식 조회 |

이번 범위에서 제외:

- 음식 즐겨찾기 목록 조회
- 음식 즐겨찾기 등록
- 음식 즐겨찾기 삭제
- 검색/상세 응답의 `isFavorite`

`food_favorites` 테이블은 기존 `01-init.sql`에 있지만 이번 구현에서는 사용하지 않는다. 즐겨찾기 기능은 별도 컨트롤러/이슈로 분리한다.

## Current Repository Facts

- Backend: Maven, Spring Boot 3.3.5, Java 17
- Base package: `com.nyamnyam.coach`
- API context path: `/api`
- MyBatis mapper location: `classpath:mappers/**/*.xml`
- FOOD Java package exists but currently only has the data-design entity work.
- Existing draft mapper `BackEnd/src/main/resources/mappers/FoodMapper.xml.bak` is only a reference. Its namespace/package/table names do not match the current repository shape.
- Current schema has `foods`, `food_favorites`, `diets`, `diet_items` in `BackEnd/scripts/01-init.sql`.

## Product Requirement

식단 페이지에서 사용자는 음식을 검색하고, 선택한 음식의 영양성분을 확인한 뒤 식단 기록에 추가한다.

프론트 입력 요구사항:

- 기본 입력 단위는 실제 계산 단위인 `g` 또는 `ml`이다.
- 프론트는 `식품중량`을 이용해 `1인분` 버튼을 보여줄 수 있다.
- 이름에 `n개입` 또는 `낱개`가 명확한 음식은 `1개` 버튼을 보여줄 수 있다.
- `1인분`/`1개` 버튼은 UI 편의 기능이고, 백엔드 저장 요청에는 최종 환산된 `g` 또는 `ml` 값을 전달한다.
- 화면에는 kcal, protein, carbs, fat, 기준 단위를 요약 표시한다.

핵심 결정:

- DB는 `unit_type` 같은 입력 UI 상태를 저장하지 않는다.
- `nutrition_basis_unit`이 `g`이면 g 기준 음식, `ml`이면 ml 기준 음식이다.
- `serving_amount`/`serving_unit`은 프론트의 `1인분` 버튼 표시용 참고값이다.
- `gram_per_piece`는 이름에서 개당 g를 신뢰할 수 있을 때만 채운다.

## Data Source

음식 데이터는 제공된 엑셀 파일을 사용한다.

- Source file: `C:/Users/kangm/Downloads/20251229_음식DB 19495건.xlsx`
- Sheet: `20251229_음식DB_19,495건`
- Row count: 19,495

주요 사용 컬럼:

| Excel column | DB/Domain usage |
| --- | --- |
| `식품코드` | `external_food_code` |
| `식품명` | `name` |
| `식품대분류명` | `category` |
| `영양성분함량기준량` | `nutrition_basis_amount`, `nutrition_basis_unit` |
| `에너지(kcal)` | `calories` |
| `단백질(g)` | `protein_g` |
| `탄수화물(g)` | `carbs_g` |
| `지방(g)` | `fat_g` |
| `당류(g)` | `sugar_g` |
| `나트륨(mg)` | `sodium_mg` |
| `식이섬유(g)` | `fiber_g` |
| `철(mg)` | `iron_mg` |
| `인(mg)` | `phosphorus_mg` |
| `칼륨(mg)` | `potassium_mg` |
| `비타민A(μg RAE)` | `vitamin_a_ug_rae` |
| `베타카로틴(μg)` | `beta_carotene_ug` |
| `레티놀(μg)` | `retinol_ug` |
| `식품중량` | `serving_amount`, `serving_unit` |
| `업체명` | `brand` |
| `출처명` | `source` |

확인된 데이터 분포:

| 기준량 단위 | 식품중량 단위 | 건수 |
| --- | --- | ---: |
| `g` | `g` | 13,743 |
| `ml` | `ml` | 5,740 |
| `g` | missing | 12 |

샘플:

| 유형 | 식품명 | 영양성분함량기준량 | 식품중량 |
| --- | --- | --- | --- |
| g/g | 국밥_돼지머리 | 100g | 900g |
| g/g | 김밥 | 100g | 230g |
| ml/ml | 스무디_코코넛 계열 | 100ml | 360ml |
| g/missing | 모듬찰떡 | 100g | missing |
| 개입 후보 | 도넛_달콤한꿀도넛 (3개입) | 100g | 105g |

중요 관찰:

- `영양성분함량기준량`과 `식품중량`은 같은 단위를 사용한다.
- g 음식은 g 기준으로 움직이고, ml 음식은 ml 기준으로 움직인다.
- `1인(회)분량 참고량`은 확인한 파일에서 비어 있으므로 의존하지 않는다.
- 런타임에서 xlsx를 직접 읽지 않는다. import 스크립트로 DB seed SQL을 생성한다.

## Data Model Plan

### `foods` table

`foods`는 음식 원본과 영양 계산 기준을 저장한다. 프론트 UI의 기본 입력 타입은 저장하지 않는다.

```sql
CREATE TABLE foods (
    food_id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    external_food_code     VARCHAR(100),
    name                   VARCHAR(200) NOT NULL,
    brand                  VARCHAR(100),
    category               VARCHAR(100),

    nutrition_basis_amount DECIMAL(8,2) NOT NULL DEFAULT 100,
    nutrition_basis_unit   VARCHAR(10) NOT NULL DEFAULT 'g',

    serving_amount         DECIMAL(8,2),
    serving_unit           VARCHAR(10),
    gram_per_piece         DECIMAL(8,4),

    calories               DECIMAL(10,2) DEFAULT 0,
    protein_g              DECIMAL(10,2) DEFAULT 0,
    carbs_g                DECIMAL(10,2) DEFAULT 0,
    fat_g                  DECIMAL(10,2) DEFAULT 0,
    sugar_g                DECIMAL(10,2) DEFAULT 0,
    sodium_mg              DECIMAL(10,2) DEFAULT 0,
    fiber_g                DECIMAL(10,2) DEFAULT 0,
    iron_mg                DECIMAL(10,2) DEFAULT 0,
    phosphorus_mg          DECIMAL(10,2) DEFAULT 0,
    potassium_mg           DECIMAL(10,2) DEFAULT 0,
    vitamin_a_ug_rae       DECIMAL(10,2) DEFAULT 0,
    beta_carotene_ug       DECIMAL(10,2) DEFAULT 0,
    retinol_ug             DECIMAL(10,2) DEFAULT 0,

    source                 VARCHAR(50),
    created_at             DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_foods_external_food_code UNIQUE (external_food_code),
    INDEX idx_foods_name (name),
    INDEX idx_foods_category (category)
) ENGINE=InnoDB;
```

컬럼 역할:

| Column | 역할 |
| --- | --- |
| `nutrition_basis_amount` | 영양성분 기준량. 대부분 `100` |
| `nutrition_basis_unit` | 기준 단위. `g` 또는 `ml` |
| `serving_amount` | 원본 `식품중량`. 프론트의 `1인분` 버튼 표시용 |
| `serving_unit` | `serving_amount`의 단위. 원본 기준으로 `g` 또는 `ml` |
| `gram_per_piece` | 이름에서 `n개입`/`낱개`를 신뢰할 수 있을 때의 개당 g |

### `diet_items` table

식단 기록은 사용자가 입력한 값과 계산용 환산값을 함께 보존한다.

```sql
CREATE TABLE diet_items (
    diet_item_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    diet_id            BIGINT NOT NULL,
    food_id            BIGINT NOT NULL,

    input_amount       DECIMAL(8,2) NOT NULL,
    input_unit         VARCHAR(10) NOT NULL,
    amount_g           DECIMAL(8,2),
    amount_ml          DECIMAL(8,2),

    calories           DECIMAL(10,2) DEFAULT 0,
    protein_g          DECIMAL(10,2) DEFAULT 0,
    carbs_g            DECIMAL(10,2) DEFAULT 0,
    fat_g              DECIMAL(10,2) DEFAULT 0,
    sugar_g            DECIMAL(10,2) DEFAULT 0,
    sodium_mg          DECIMAL(10,2) DEFAULT 0,
    fiber_g            DECIMAL(10,2) DEFAULT 0,
    iron_mg            DECIMAL(10,2) DEFAULT 0,
    phosphorus_mg      DECIMAL(10,2) DEFAULT 0,
    potassium_mg       DECIMAL(10,2) DEFAULT 0,
    vitamin_a_ug_rae   DECIMAL(10,2) DEFAULT 0,
    beta_carotene_ug   DECIMAL(10,2) DEFAULT 0,
    retinol_ug         DECIMAL(10,2) DEFAULT 0,

    CONSTRAINT fk_diet_items_diet
        FOREIGN KEY (diet_id) REFERENCES diets(diet_id) ON DELETE CASCADE,
    CONSTRAINT fk_diet_items_food
        FOREIGN KEY (food_id) REFERENCES foods(food_id) ON DELETE RESTRICT,
    INDEX idx_diet_items_diet (diet_id),
    INDEX idx_diet_items_food (food_id)
) ENGINE=InnoDB;
```

저장 규칙:

- g 기준 음식: `amount_g`를 채우고 `amount_ml`은 `NULL`.
- ml 기준 음식: `amount_ml`을 채우고 `amount_g`는 `NULL`.
- `input_amount`/`input_unit`은 사용자가 실제로 선택한 최종 입력값을 보존한다.
- 계산된 영양성분은 스냅샷으로 저장해 이후 `foods` 변경의 영향을 받지 않게 한다.

### `food_favorites` table

기존 테이블은 유지하되 이번 이슈에서는 repository/service/controller를 구현하지 않는다.

### Frequent foods

`GET /api/v1/foods/frequent`는 다음 관계를 사용한다.

- `diets.user_id`
- `diet_items.food_id`
- `COUNT(diet_items.food_id)` as `recordCount`
- `MAX(diets.eaten_at)` as `lastRecordedAt`

정렬:

1. `recordCount DESC`
2. `lastRecordedAt DESC`

## Excel Import Plan

추가 스크립트:

```text
BackEnd/scripts/import_foods_from_xlsx.py
```

입력:

- xlsx path argument

출력:

- 기본값: `BackEnd/scripts/generated/foods-seed.sql`

정책:

- MySQL upsert SQL을 생성한다.
- `external_food_code`를 unique key로 사용한다.
- 생성 결과물은 크므로 `scripts/generated/`에 두고 git에는 포함하지 않는다.
- 필수값인 `식품코드` 또는 `식품명`이 없으면 해당 row는 skip한다.
- 숫자 영양값이 비어 있거나 파싱 실패하면 현재 스키마 기본 정책에 맞춰 `0`으로 저장한다.
- `업체명 = 해당없음`은 `NULL`로 저장한다.
- `식품중량` 파싱 실패는 전체 import 실패로 처리하지 않는다. `serving_amount = NULL`, `serving_unit = NULL`로 둔다.
- 이름에 `n개입`이 있고 `식품중량`이 g 단위이면 `gram_per_piece = 식품중량 / n`으로 계산한다.
- 이름에 `낱개` 또는 `1개입`이 있고 `식품중량`이 g 단위이면 `gram_per_piece = 식품중량`으로 저장한다.

Parsing examples:

| Raw value | Parsed |
| --- | --- |
| `100g` | amount `100`, unit `g` |
| `200ml` | amount `200`, unit `ml` |
| `도넛_달콤한꿀도넛 (3개입)`, `105g` | `gram_per_piece = 35` |
| `기타빵_코코넛로쉐(낱개)`, `23g` | `gram_per_piece = 23` |

## Package Plan

Create files under:

```text
BackEnd/src/main/java/com/nyamnyam/coach/food
  controller/
  dto/response/
  entity/
  repository/
  service/
```

Create mapper:

```text
BackEnd/src/main/resources/mappers/food/FoodMapper.xml
```

Do not reuse `mappers/FoodMapper.xml.bak` directly. Its namespace points to old package assumptions.

## Entity / Domain

Suggested classes:

- `Food`
- `FrequentFoodRow` or mapper row DTO

`Food` should include only DB-backed fields and simple domain helpers.

Suggested helper methods:

- `isLiquidBasis()`
- `hasServingAmount()`
- `hasPieceAmount()`

Avoid putting search pagination or API response shape inside the entity.

## DTO Plan

### Search response

`FoodSearchResponse`

```json
{
  "foodId": 1,
  "name": "김밥",
  "brand": null,
  "category": "밥류",
  "nutritionBasisAmount": 100,
  "nutritionBasisUnit": "g",
  "servingAmount": 230,
  "servingUnit": "g",
  "gramPerPiece": null,
  "calories": 135,
  "protein": 7.17,
  "carbs": 5.36,
  "fat": 9.49,
  "sugar": 3.25,
  "sodium": 88
}
```

`FoodSearchPageResponse`

```json
{
  "content": [],
  "page": 0,
  "size": 20,
  "totalElements": 0,
  "totalPages": 0
}
```

### Detail response

`FoodDetailResponse`

```json
{
  "foodId": 1,
  "externalFoodCode": "D114-640080000-0001",
  "name": "김밥",
  "brand": null,
  "category": "밥류",
  "nutritionBasisAmount": 100,
  "nutritionBasisUnit": "g",
  "servingAmount": 230,
  "servingUnit": "g",
  "gramPerPiece": null,
  "calories": 135,
  "protein": 7.17,
  "carbs": 5.36,
  "fat": 9.49,
  "sugar": 3.25,
  "sodium": 88,
  "source": "식품의약품안전처"
}
```

### Frequent response

`FrequentFoodResponse`

```json
{
  "foodId": 1,
  "name": "김밥",
  "recordCount": 7,
  "lastRecordedAt": "2026-05-26T12:30:00"
}
```

`FrequentFoodListResponse`

```json
{
  "foods": []
}
```

## Repository Plan

`FoodRepository` mapper methods:

```java
List<Food> searchByKeyword(String keyword, int limit, int offset);
long countByKeyword(String keyword);
Optional<Food> findById(Long foodId);
List<FrequentFoodRow> findFrequentFoods(Long userId, int limit);
```

SQL notes:

- Search should use `name LIKE CONCAT('%', #{keyword}, '%')`.
- Consider searching `brand`, `category`, `representative_food_name` later only if those columns are stored.
- Pagination should use `LIMIT #{limit} OFFSET #{offset}`.
- Frequent foods should join `diet_items -> diets -> foods`.
- Order frequent foods by `recordCount DESC`, then `lastRecordedAt DESC`.

## Service Plan

`FoodService` responsibilities:

- Validate keyword.
- Normalize page/size.
- Load food detail or throw `FOOD_NOT_FOUND`.
- Calculate page metadata.
- Load frequent foods for authenticated user.
- Clamp frequent-food `limit` to a reasonable range.

Validation defaults:

- `keyword`: required, trimmed length >= 1
- `page`: default `0`, min `0`
- `size`: default `20`, min `1`, max `100`
- `limit`: default `10`, min `1`, max `50`

Do not implement favorite logic in this issue.

## Controller Plan

`FoodController`:

```text
GET /v1/foods
GET /v1/foods/{foodId}
GET /v1/foods/frequent
```

Because `server.servlet.context-path` is `/api`, controller mappings should use `/v1/...`, while docs and issue text should show public URLs as `/api/v1/...`.

Authentication policy:

- `GET /api/v1/foods`: allow unauthenticated access.
- `GET /api/v1/foods/{foodId}`: allow unauthenticated access.
- `GET /api/v1/foods/frequent`: require authentication.

Check `SecurityConfig` before implementation. Current config already permits `GET /foods/**`, but the actual controller path should be checked carefully because routes are likely `/v1/foods/**` under context path `/api`.

## Exception Plan

Add food-specific error codes under the existing global error-code pattern.

| Code | HTTP | Trigger |
| --- | --- | --- |
| `FOOD_NOT_FOUND` | 404 | `foodId` does not exist |
| `INVALID_KEYWORD` | 400 | blank search keyword |
| `INVALID_PAGE_REQUEST` | 400 | invalid page/size, if not normalized |
| `INVALID_LIMIT` | 400 | invalid frequent limit, if not normalized |

Prefer existing `BusinessException` and existing `ErrorCode` enum structure.

## Swagger Plan

Follow existing split-doc pattern:

```text
BackEnd/src/main/java/com/nyamnyam/coach/food/controller/FoodApiDocs.java
BackEnd/src/main/java/com/nyamnyam/coach/food/controller/FoodController.java
```

Swagger text should be Korean.

Document:

- 음식 검색
- 음식 상세 조회
- 자주 먹은 음식 조회
- Auth requirement:
  - search/detail: no bearer requirement if public
  - frequent: `@SecurityRequirement(name = "bearerAuth")`

## Test Plan

Minimum useful tests:

### Repository

- search returns foods by keyword.
- count returns total.
- findById returns food.
- findById returns empty for missing food.
- frequent query aggregates by user and orders by count/date.

### Service

- blank keyword throws `INVALID_KEYWORD`.
- missing food throws `FOOD_NOT_FOUND`.
- page response calculates `totalPages`.
- frequent limit default/clamp behavior.

### Controller

- `GET /v1/foods?keyword=김밥` returns common success envelope.
- `GET /v1/foods/{foodId}` returns detail response.
- missing food returns common error envelope.
- `GET /v1/foods/frequent` requires authentication.

Use the narrowest useful tests first. If `@WebMvcTest` becomes broad due to unrelated controllers, narrow it to `FoodController.class`.

## API Examples

### GET `/api/v1/foods`

Request:

```http
GET /api/v1/foods?keyword=김밥&page=0&size=20
```

Response:

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "foodId": 1,
        "name": "김밥",
        "brand": null,
        "category": "밥류",
        "nutritionBasisAmount": 100,
        "nutritionBasisUnit": "g",
        "servingAmount": 230,
        "servingUnit": "g",
        "gramPerPiece": null,
        "calories": 135,
        "protein": 7.17,
        "carbs": 5.36,
        "fat": 9.49,
        "sugar": 3.25,
        "sodium": 88
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1
  },
  "message": "음식 검색에 성공했습니다."
}
```

### GET `/api/v1/foods/{foodId}`

Request:

```http
GET /api/v1/foods/1
```

Response:

```json
{
  "success": true,
  "data": {
    "foodId": 1,
    "externalFoodCode": "D114-640080000-0001",
    "name": "김밥",
    "brand": null,
    "category": "밥류",
    "nutritionBasisAmount": 100,
    "nutritionBasisUnit": "g",
    "servingAmount": 230,
    "servingUnit": "g",
    "gramPerPiece": null,
    "calories": 135,
    "protein": 7.17,
    "carbs": 5.36,
    "fat": 9.49,
    "sugar": 3.25,
    "sodium": 88,
    "source": "식품의약품안전처"
  },
  "message": "음식 상세 조회에 성공했습니다."
}
```

### GET `/api/v1/foods/frequent`

Request:

```http
GET /api/v1/foods/frequent?limit=10
Authorization: Bearer {accessToken}
```

Response:

```json
{
  "success": true,
  "data": {
    "foods": [
      {
        "foodId": 1,
        "name": "김밥",
        "recordCount": 7,
        "lastRecordedAt": "2026-05-26T12:30:00"
      }
    ]
  },
  "message": "자주 먹은 음식 조회에 성공했습니다."
}
```

## Suggested Commit Split

One issue is enough. Split commits by reviewable implementation boundary:

1. `feat: food 데이터 스키마 및 import 스크립트 추가`
2. `feat: 음식 검색 및 상세 조회 API 구현`
3. `feat: 자주 먹은 음식 조회 API 구현`
4. `docs: food Swagger 문서 추가`
5. `test: food 도메인 테스트 추가`

If the schema/import commit becomes too large, split it:

1. schema migration
2. import script
3. API implementation

## Open Decisions Before Coding

1. `GET /api/v1/foods` and `GET /api/v1/foods/{foodId}` should stay public?
2. Generated seed data should be committed, or should only the import script be committed?
3. Unknown nutrition values should be stored as `0` or `NULL`?
