# 목표 영양소 산출 로직

NyamNyam Coach는 사용자의 건강 프로필을 바탕으로 하루 목표 영양소를 계산한다.
목표값은 사용자가 직접 입력하는 값이 아니라, 서버가 건강 프로필 저장 시점에 계산해 `health_profiles` 테이블에 저장한다.

## 기준

현재 로직은 `2020 한국인 영양소 섭취기준(KDRI)`을 최대한 기준으로 삼는다.

- 에너지: KDRI 성인 에너지필요추정량(EER) 계산식 계열
- 탄수화물, 단백질, 지방: KDRI 에너지적정비율 범위 안의 대표값
- 나트륨, 식이섬유: `nutrition_reference_standards` 테이블의 `KDRI_2020` 기준값 우선 사용

단, 감량/증량 목표에 따른 칼로리 보정은 KDRI 자체 공식이 아니라 서비스 정책이다.
즉, 기본 유지 필요 열량은 KDRI 기준으로 계산하고, 그 뒤에 앱의 체중 목표 정책을 적용한다.

## 입력값

목표 영양소 계산에 쓰이는 건강 프로필 입력값은 다음과 같다.

| 입력값 | 의미 | 사용 여부 |
| --- | --- | --- |
| `heightCm` | 키(cm) | 에너지 계산 |
| `weightKg` | 현재 체중(kg) | 에너지 계산 |
| `birthDate` | 생년월일 | 나이 계산 |
| `gender` | 성별 | 에너지 계산, 기준표 조회 |
| `activityLevel` | 활동량 | 에너지 계산 |
| `healthGoal` | 건강 목표 | 감량/증량 보정 |
| `targetWeightKg` | 목표 체중 | 현재 계산식에는 직접 미사용 |
| `dietStyles` | 식단 스타일 | 저장만 함 |
| `restrictedFoods` | 제한 음식 | 저장만 함 |
| `allergies` | 알레르기 | 저장만 함 |

## 전체 흐름

1. 생년월일로 현재 나이를 계산한다.
2. 성별을 `MALE`, `FEMALE`, `ALL` 중 하나로 정규화한다.
3. 성별과 나이로 `nutrition_reference_standards`에서 KDRI 기준값을 조회한다.
4. 키, 체중, 나이, 성별, 활동량으로 유지 목적의 에너지필요추정량(EER)을 계산한다.
5. 감량/증량 목표가 있으면 서비스 정책에 따라 칼로리를 보정한다.
6. 목표 칼로리를 기준으로 탄수화물, 단백질, 지방 g 목표를 계산한다.
7. 나트륨과 식이섬유는 KDRI 기준표 값을 우선 사용한다.
8. 계산 결과를 건강 프로필 목표 컬럼에 저장한다.

## 나이와 성별 처리

나이는 `birthDate`와 현재 날짜의 차이로 계산한다.
생년월일이 없으면 기본 나이 `30`을 사용한다.

성별은 다음처럼 정리한다.

| 입력값 | 처리값 |
| --- | --- |
| `MALE` | `MALE` |
| `FEMALE` | `FEMALE` |
| null, 빈 값, 기타 값 | `ALL` |

에너지 계산에서는 `FEMALE`만 여성식으로 계산하고, 나머지는 남성식으로 계산한다.
기준표 조회에서는 `ALL`이 fallback 기준으로 사용된다.

## 에너지필요추정량 계산

키, 체중, 생년월일 중 하나라도 없으면 목표 칼로리는 기본값 `2000 kcal`이 된다.

입력값이 모두 있으면 성인 EER 식을 사용한다.

### 여성

```text
EER = 354 - 6.91 * 나이 + 활동계수 * (9.36 * 체중kg + 726 * 키m)
```

### 남성

```text
EER = 662 - 9.53 * 나이 + 활동계수 * (15.91 * 체중kg + 539.6 * 키m)
```

## 활동량 계수

활동량은 성별에 따라 다른 계수를 사용한다.

| `activityLevel` | 남성 | 여성 |
| --- | ---: | ---: |
| null 또는 기타 값 | 1.00 | 1.00 |
| `LIGHT` | 1.11 | 1.12 |
| `MODERATE` | 1.25 | 1.27 |
| `ACTIVE` | 1.48 | 1.45 |
| `VERY_ACTIVE` | 1.48 | 1.45 |

현재 입력 단계에는 `ACTIVE`와 `VERY_ACTIVE`가 모두 있지만, KDRI EER 계수에 맞춰 둘 다 높은 활동 수준으로 묶어 같은 계수를 사용한다.

## 건강 목표 보정

KDRI의 EER은 기본적으로 현재 신체 조건에서의 유지 필요 열량이다.
따라서 감량/증량 목표는 서비스 정책으로 별도 적용한다.

| `healthGoal` | 보정 |
| --- | ---: |
| `LOSE_WEIGHT` | `-min(EER * 0.15, 500 kcal)` |
| `GAIN_WEIGHT` | `+min(EER * 0.10, 500 kcal)` |
| 그 외 | 보정 없음 |

보정 후 목표 칼로리는 최소 `1200 kcal`보다 낮아지지 않게 제한한다.

```text
감량 목표 칼로리 = max(EER - min(EER * 0.15, 500), 1200)
증량 목표 칼로리 = max(EER + min(EER * 0.10, 500), 1200)
유지 목표 칼로리 = max(EER, 1200)
```

## 탄수화물, 단백질, 지방 계산

KDRI는 탄수화물, 단백질, 지방을 하나의 고정값이 아니라 에너지적정비율 범위로 제시한다.
현재 DB 구조는 단일 목표값만 저장하므로, 서비스에서는 범위 안의 대표값을 사용한다.

| 영양소 | 대표 에너지 비율 | 환산 |
| --- | ---: | --- |
| 탄수화물 | 60% | 1g = 4kcal |
| 단백질 | 15% | 1g = 4kcal |
| 지방 | 25% | 1g = 9kcal |

계산식은 다음과 같다.

```text
탄수화물(g) = 목표 칼로리 * 0.60 / 4
단백질(g) = 목표 칼로리 * 0.15 / 4
지방(g) = 목표 칼로리 * 0.25 / 9
```

각 결과는 g 단위로 반올림한 뒤 소수점 둘째 자리 형식으로 저장한다.

## 나트륨과 식이섬유

나트륨과 식이섬유는 칼로리 비율로 계산하지 않는다.
먼저 `nutrition_reference_standards` 테이블에서 성별과 나이에 맞는 기준값을 조회한다.

조회 조건은 다음과 같다.

```sql
WHERE (gender = #{gender} OR gender = 'ALL')
  AND #{age} BETWEEN age_min AND age_max
ORDER BY CASE WHEN gender = #{gender} THEN 0 ELSE 1 END
LIMIT 1
```

즉, 정확한 성별 기준이 있으면 그것을 우선 사용하고, 없으면 `ALL` 기준을 사용한다.

기준표 값이 없을 때의 fallback은 다음과 같다.

| 영양소 | fallback |
| --- | ---: |
| 나트륨 | 2000mg |
| 식이섬유 | 25g |
| 기준 버전 | `KDRI_2020` |

## 저장되는 값

계산이 끝나면 다음 컬럼에 저장된다.

| 컬럼 | 의미 |
| --- | --- |
| `target_calories` | 목표 칼로리 |
| `target_protein_g` | 목표 단백질 |
| `target_carbs_g` | 목표 탄수화물 |
| `target_fat_g` | 목표 지방 |
| `target_sodium_mg` | 목표 나트륨 |
| `target_fiber_g` | 목표 식이섬유 |
| `nutrition_standard_version` | 기준 버전 |
| `nutrition_target_calculated_at` | 계산 시각 |

이 값들은 온보딩 완료 또는 건강 프로필 수정 시 다시 계산된다.

## 예시

입력값이 다음과 같다고 가정한다.

| 항목 | 값 |
| --- | --- |
| 성별 | 여성 |
| 나이 | 25세 |
| 키 | 162cm |
| 체중 | 54kg |
| 활동량 | `LIGHT` |
| 건강 목표 | `LOSE_WEIGHT` |

1. 키를 m 단위로 변환한다.

```text
162cm = 1.62m
```

2. 여성 EER 식을 적용한다.

```text
EER = 354 - 6.91 * 25 + 1.12 * (9.36 * 54 + 726 * 1.62)
```

3. 계산 결과는 약 `2065 kcal`이다.

4. 감량 목표이므로 EER의 15%를 빼되, 최대 `500 kcal`까지만 뺀다.

```text
감량 보정 = min(2065 * 0.15, 500) = 310 kcal
목표 칼로리 = 2065 - 310 = 1755 kcal
```

5. 탄단지를 계산한다.

```text
탄수화물 = 1755 * 0.60 / 4 = 263.25g -> 263g
단백질 = 1755 * 0.15 / 4 = 65.81g -> 66g
지방 = 1755 * 0.25 / 9 = 48.75g -> 49g
```

6. 나트륨과 식이섬유는 기준표 값을 사용한다.

```text
나트륨 = 2000mg
식이섬유 = 25g
```

최종 저장값은 다음과 같다.

| 영양소 | 목표 |
| --- | ---: |
| 칼로리 | 1755 kcal |
| 탄수화물 | 263g |
| 단백질 | 66g |
| 지방 | 49g |
| 나트륨 | 2000mg |
| 식이섬유 | 25g |

## 주의할 점

- KDRI는 개인별 다이어트 처방 시스템이 아니라 일반적인 기준 섭취량 체계다.
- 체중 감량/증량 보정 비율과 최대 `500 kcal` 제한은 KDRI 공식이 아니라 앱의 서비스 정책이다.
- 탄수화물, 단백질, 지방은 KDRI 범위 안의 대표값을 저장하는 구조다.
- 더 정밀하게 하려면 DB에 단일 목표값뿐 아니라 권장 범위의 최소/최대값도 저장하는 구조가 필요하다.

## 관련 코드

- `BackEnd/src/main/java/com/nyamnyam/coach/nutrition/service/NutritionTargetCalculator.java`
- `BackEnd/src/main/java/com/nyamnyam/coach/nutrition/service/NutritionTargetValues.java`
- `BackEnd/src/main/resources/mappers/nutrition/NutritionReferenceMapper.xml`
- `BackEnd/src/main/resources/mappers/user/HealthProfileMapper.xml`
