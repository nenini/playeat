# AI Quest, Report, Coach Implementation Plan

## 1. 현재 기준

현재 브랜치는 `feature/#26-gms-ai`에 `feature/#28-quest-condition-verification`을 merge한 상태다.

#28에서 퀘스트 도메인이 `com.nyamnyam.coach.quest` 패키지로 분리되었고, 퀘스트 검증은 조건 필드 기반으로 동작한다. 따라서 AI 퀘스트 생성도 예전 `boss` 패키지 기준 구현이 아니라 #28의 `quest` 패키지와 검증 구조 위에 붙인다.

## 2. #28에서 반영된 퀘스트 변경

#28의 주요 변경은 다음과 같다.

- 개인 퀘스트 검증 로직을 `questType` 중심에서 조건 필드 기반 검증으로 변경
- 식단 생성, 수정, 삭제 시 개인 퀘스트 및 공통 격파 조건 자동 검증 이벤트 추가
- 공통 격파 조건 수동 검증 API 추가
- `boss_condition_templates` 테이블 추가
- `boss_common_conditions`, `boss_battle_conditions`에 조건 검증 필드 및 템플릿 ID 추가
- `SUGAR_UNDER_LIMIT` 공통 조건을 템플릿 기반으로 연결
- `PROCESSED_DRINK_ZERO`, `VEGETABLE_VARIETY`는 미지원 조건으로 처리해 보스 격파를 막지 않도록 수정
- Swagger 테스트용 당분 드래곤 EASY, NORMAL, HARD seed 추가
- 개인 퀘스트 데미지를 템플릿 고정값이 아니라 보스 HP 비중 기준으로 계산
- 퀘스트 및 공통 조건 완료 시 보스 HP 감소, 데미지 로그, 점수 로그, 시스템 메시지 생성

추가/변경된 주요 API:

| Method | URL | 설명 |
| --- | --- | --- |
| `POST` | `/api/v1/quests/{questId}/verify` | 개인 퀘스트 조건 검증 |
| `POST` | `/api/v1/boss-battles/{battleId}/common-conditions/verify` | 보스전 공통 격파 조건 수동 검증 |
| `POST` | `/api/v1/boss-battles/{battleId}/quests/generate` | 참여자별 개인 퀘스트 생성 및 데미지 계산 |
| `GET` | `/api/v1/boss-battles/{battleId}` | 보스전 상세 및 공통 조건 상태 조회 |
| `GET` | `/api/v1/boss-battles/{battleId}/quests` | 보스전 참여자별 퀘스트 목록 조회 |

## 3. AI 퀘스트 구현 목표

AI 퀘스트 생성은 새로운 검증 조건을 만들지 않는다.

목표:

- 기존 `/boss-battles/{battleId}/quests/generate` 흐름을 유지한다.
- `QuestGenerator` 구현체를 placeholder에서 AI 기반 generator로 교체 가능하게 한다.
- AI는 `quest_templates` 중 하나를 선택하고 제목/설명만 다듬는다.
- 저장되는 검증 조건과 데미지는 #28의 백엔드 로직이 결정한다.
- 조건 검증, 자동 검증 이벤트, 보스 HP 감소, 로그 생성은 #28의 기존 서비스가 그대로 처리한다.

## 4. AI가 할 수 있는 것과 하면 안 되는 것

AI가 할 수 있는 것:

- 활성 `quest_templates` 목록 중 하나의 `selectedTemplateId` 선택
- 사용자에게 보이는 `customTitle` 작성
- 사용자에게 보이는 `customDescription` 작성

AI가 하면 안 되는 것:

- `condition_category` 생성 또는 변경
- `metric_type` 생성 또는 변경
- `comparison_type` 생성 또는 변경
- `aggregation_type` 생성 또는 변경
- `evaluation_scope` 생성 또는 변경
- threshold, target, unit, damage, reward 값 변경

AI 응답 JSON:

```json
{
  "selectedTemplateId": 1,
  "customTitle": "오늘은 당류 줄이기에 도전해요",
  "customDescription": "당분 드래곤에게 효과적인 당류 관리 퀘스트예요."
}
```

## 5. 구현 방향

### 5.1 `AiTextGenerator` 확장

현재 `AiTextGenerator`는 report/coach 중심이다.

AI 퀘스트 생성을 위해 다음 메서드를 다시 추가한다.

```java
String generateDailyQuest(AiQuestPrompt prompt);
```

단, 이 메서드는 quest 조건 생성이 아니라 template 선택용이다.

### 5.2 prompt DTO 추가

추가할 DTO:

- `ai/service/prompt/AiQuestPrompt.java`
- `ai/service/prompt/QuestTemplatePrompt.java`

`AiQuestPrompt` 입력:

- 보스전 난이도
- 활성 참여자 수
- 현재 퀘스트를 받을 참여자 순번
- 참여자 닉네임
- 선택 가능한 quest template 목록

`QuestTemplatePrompt` 입력:

- `templateId`
- `title`
- `description`
- `questType`
- `conditionCategory`
- `metricType`
- `comparisonType`
- `aggregationType`
- `evaluationScope`
- `thresholdValue`
- `thresholdMinValue`
- `thresholdMaxValue`
- `thresholdUnit`
- `targetValue`
- `unit`

### 5.3 응답 parser 추가

추가할 record:

- `ai/service/parser/AiQuestContent.java`

`AiJsonResponseParser`에 추가:

```java
AiQuestContent parseQuest(String rawText);
```

### 5.4 AI QuestGenerator 추가

추가할 클래스:

- `quest/service/AiQuestGenerator.java`

`AiQuestGenerator`는 #28의 `QuestGenerator` 인터페이스를 구현한다.

흐름:

```text
QuestService.generateQuests
-> QuestGenerator.generatePersonalQuest(...)
-> AiQuestGenerator.generatePersonalQuest(...)
-> QuestTemplateRepository.findActiveTemplatesByDifficulty(...)
-> 없으면 QuestTemplateRepository.findActiveTemplates()
-> AI에게 template 목록 전달
-> selectedTemplateId 검증
-> 없거나 잘못된 id면 안전한 fallback template 사용
-> template 조건을 Quest에 복사
-> title/description만 AI custom 값으로 교체
-> damage는 #28의 보스 HP 비중 계산식을 사용
-> sourceType은 AI
```

### 5.5 기존 PlaceholderQuestGenerator 유지

`PlaceholderQuestGenerator`는 fallback 또는 GMS disabled 환경에서 사용할 수 있게 유지한다.

Bean 전환:

| 설정 | QuestGenerator |
| --- | --- |
| `gms.enabled=true` | `AiQuestGenerator` |
| `gms.enabled=false` 또는 미설정 | `PlaceholderQuestGenerator` |

이를 위해 `@ConditionalOnProperty`를 사용한다.

## 6. #28 데미지 계산 유지

개인 퀘스트 damage는 template의 고정 damage를 그대로 쓰지 않는다.

#28 기준 계산:

| 난이도 | 개인 퀘스트 총 데미지 비중 |
| --- | --- |
| EASY | 보스 maxHp의 80% |
| NORMAL | 보스 maxHp의 70% |
| HARD | 보스 maxHp의 60% |

참여자 수로 나누고 나머지는 앞 순번 참여자에게 1씩 배분한다.

AI generator도 이 계산식을 그대로 사용한다.

## 7. API 변경 여부

새 API는 추가하지 않는다.

AI 퀘스트 생성은 기존 API에 자연스럽게 반영한다.

```text
POST /api/v1/boss-battles/{battleId}/quests/generate
```

`gms.enabled=true`이면 AI가 template을 선택한 퀘스트가 생성되고, `gms.enabled=false`이면 기존 placeholder generator가 동작한다.

## 8. GMS prompt 정책

AI quest prompt 규칙:

- 반드시 JSON만 반환
- markdown 금지
- 제공된 `availableQuestTemplates` 중 하나만 선택
- template id를 새로 만들지 않음
- threshold, target, unit 변경 금지
- 검증 조건을 새로 제안하지 않음
- `customTitle`, `customDescription`만 한국어로 자연스럽게 작성
- 적절한 template이 애매하면 가장 안전한 식단 기록 횟수 template 선택

## 9. 에러와 fallback

정책:

- template 목록이 비어 있으면 `QUEST_TEMPLATE_NOT_FOUND`
- AI 응답 JSON 파싱 실패는 `AI_RESPONSE_PARSE_FAILED`
- AI가 없는 `selectedTemplateId`를 반환하면 첫 번째 template으로 fallback
- AI title/description이 비어 있으면 template 기본 title/description 사용
- title/description은 줄바꿈 제거 및 최대 길이 제한
- GMS provider raw error는 사용자에게 노출하지 않음

## 10. 테스트 계획

추가/수정할 테스트:

- `AiJsonResponseParserTest`
  - quest JSON 파싱 성공
  - markdown 포함 응답에서 JSON object 추출
- `AiQuestGeneratorTest`
  - AI가 선택한 template 기준으로 Quest 생성
  - 없는 template id면 fallback template 사용
  - template 조건 필드가 Quest에 snapshot으로 복사됨
  - damage가 #28 보스 HP 비중 기준으로 계산됨

기본 검증:

```powershell
cd BackEnd
mvn "-DskipTests" test
mvn "-Dtest=AiJsonResponseParserTest,AiQuestGeneratorTest" test
```

## 11. 구현 순서

1. 문서에 #28 변경사항과 AI 퀘스트 구현 방향 반영
2. `AiQuestPrompt`, `QuestTemplatePrompt`, `AiQuestContent` 추가
3. `AiTextGenerator`, `GmsAiTextGenerator`, `PlaceholderAiTextGenerator`, `AiJsonResponseParser` 수정
4. `AiQuestGenerator` 추가
5. `PlaceholderQuestGenerator`에 `gms.enabled=false` 조건 추가
6. 테스트 추가/수정
7. 컴파일 및 좁은 테스트 실행
