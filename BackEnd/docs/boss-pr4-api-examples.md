# Boss PR4 API Examples

## Boss Seed

한글 seed 데이터가 깨지지 않도록 `utf8mb4` 문자셋으로 실행한다.

```bash
docker exec -i nyamnyam-mysql mysql --default-character-set=utf8mb4 -u root -p nyamnyam < scripts/08-boss-sugar-dragon-seed.sql
```

비밀번호를 바로 붙여 실행하는 경우:

```bash
docker exec -i nyamnyam-mysql mysql --default-character-set=utf8mb4 -u root -p비밀번호 nyamnyam < scripts/08-boss-sugar-dragon-seed.sql
```

## GET /api/v1/bosses/current

```json
{
  "seasonId": 1,
  "seasonName": "2026년 6월 당분 드래곤 시즌",
  "startsAt": "2026-06-10T00:00:00",
  "endsAt": "2026-06-16T23:59:59",
  "bosses": [
    {
      "bossId": 1,
      "name": "당분 드래곤",
      "description": "최근 7일 당류 섭취가 권장치를 초과해 출현. 난이도를 선택해 길드원과 함께 전투를 시작하세요.",
      "difficulty": "EASY",
      "maxHp": 50,
      "imageUrl": "/images/boss/sugar-dragon.png",
      "rewardExp": 800,
      "rewardCoin": 100,
      "commonConditions": [
        {
          "conditionId": 1,
          "title": "당류 50g 이하 유지",
          "description": "하루 당류 섭취량을 50g 이하로 유지합니다.",
          "targetType": "SUGAR_UNDER_LIMIT",
          "thresholdValue": 50,
          "thresholdUnit": "g",
          "targetValue": 3,
          "requiredDays": 3,
          "unit": "일",
          "sortOrder": 1
        }
      ]
    },
    {
      "bossId": 2,
      "name": "당분 드래곤",
      "description": "최근 7일 당류 섭취가 권장치를 초과해 출현. 난이도를 선택해 길드원과 함께 전투를 시작하세요.",
      "difficulty": "NORMAL",
      "maxHp": 100,
      "imageUrl": "/images/boss/sugar-dragon.png",
      "rewardExp": 1200,
      "rewardCoin": 150,
      "commonConditions": [
        {
          "conditionId": 2,
          "title": "당류 50g 이하 유지",
          "description": "하루 당류 섭취량을 50g 이하로 유지합니다.",
          "targetType": "SUGAR_UNDER_LIMIT",
          "thresholdValue": 50,
          "thresholdUnit": "g",
          "targetValue": 4,
          "requiredDays": 4,
          "unit": "일",
          "sortOrder": 1
        },
        {
          "conditionId": 3,
          "title": "가공음료 0회",
          "description": "가공음료를 마시지 않은 날을 유지합니다.",
          "targetType": "PROCESSED_DRINK_ZERO",
          "thresholdValue": 0,
          "thresholdUnit": "회",
          "targetValue": 4,
          "requiredDays": 4,
          "unit": "일",
          "sortOrder": 2
        }
      ]
    },
    {
      "bossId": 3,
      "name": "당분 드래곤",
      "description": "최근 7일 당류 섭취가 권장치를 초과해 출현. 난이도를 선택해 길드원과 함께 전투를 시작하세요.",
      "difficulty": "HARD",
      "maxHp": 200,
      "imageUrl": "/images/boss/sugar-dragon.png",
      "rewardExp": 2400,
      "rewardCoin": 300,
      "commonConditions": [
        {
          "conditionId": 4,
          "title": "당류 50g 이하 유지",
          "description": "하루 당류 섭취량을 50g 이하로 유지합니다.",
          "targetType": "SUGAR_UNDER_LIMIT",
          "thresholdValue": 50,
          "thresholdUnit": "g",
          "targetValue": 4,
          "requiredDays": 4,
          "unit": "일",
          "sortOrder": 1
        },
        {
          "conditionId": 5,
          "title": "가공음료 0회",
          "description": "가공음료를 마시지 않은 날을 유지합니다.",
          "targetType": "PROCESSED_DRINK_ZERO",
          "thresholdValue": 0,
          "thresholdUnit": "회",
          "targetValue": 4,
          "requiredDays": 4,
          "unit": "일",
          "sortOrder": 2
        },
        {
          "conditionId": 6,
          "title": "채소 하루 2종 이상",
          "description": "하루에 채소를 2종 이상 기록한 날을 유지합니다.",
          "targetType": "VEGETABLE_VARIETY",
          "thresholdValue": 2,
          "thresholdUnit": "종",
          "targetValue": 5,
          "requiredDays": 5,
          "unit": "일",
          "sortOrder": 3
        }
      ]
    }
  ]
}
```

## GET /api/v1/bosses/{bossId}

```json
{
  "bossId": 3,
  "seasonId": 1,
  "name": "당분 드래곤",
  "description": "최근 7일 당류 섭취가 권장치를 초과해 출현. 난이도를 선택해 길드원과 함께 전투를 시작하세요.",
  "difficulty": "HARD",
  "maxHp": 200,
  "imageUrl": "/images/boss/sugar-dragon.png",
  "rewardExp": 2400,
  "rewardCoin": 300,
  "status": "ACTIVE",
  "startsAt": "2026-06-10T00:00:00",
  "endsAt": "2026-06-16T23:59:59",
  "commonConditions": [
    {
      "conditionId": 4,
      "title": "당류 50g 이하 유지",
      "description": "하루 당류 섭취량을 50g 이하로 유지합니다.",
      "targetType": "SUGAR_UNDER_LIMIT",
      "thresholdValue": 50,
      "thresholdUnit": "g",
      "targetValue": 4,
      "requiredDays": 4,
      "unit": "일",
      "sortOrder": 1
    },
    {
      "conditionId": 5,
      "title": "가공음료 0회",
      "description": "가공음료를 마시지 않은 날을 유지합니다.",
      "targetType": "PROCESSED_DRINK_ZERO",
      "thresholdValue": 0,
      "thresholdUnit": "회",
      "targetValue": 4,
      "requiredDays": 4,
      "unit": "일",
      "sortOrder": 2
    },
    {
      "conditionId": 6,
      "title": "채소 하루 2종 이상",
      "description": "하루에 채소를 2종 이상 기록한 날을 유지합니다.",
      "targetType": "VEGETABLE_VARIETY",
      "thresholdValue": 2,
      "thresholdUnit": "종",
      "targetValue": 5,
      "requiredDays": 5,
      "unit": "일",
      "sortOrder": 3
    }
  ]
}
```
