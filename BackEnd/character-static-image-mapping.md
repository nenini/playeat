# 캐릭터 이미지 참고사항(26.06.11)

@민수 강

캐릭터 이미지는 프론트 정적 매핑으로 표시

```
GET /api/v1/characters/me 응답의 stage, mood, appearanceType 값 사용

예시:
stage=LEVEL_1 -> /images/characters/level-1-normal.png
stage=LEVEL_2 -> /images/characters/level-2-normal.png
stage=LEVEL_3 -> /images/characters/level-3-normal.png
stage=LEVEL_4 -> /images/characters/level-4-normal.png
```
