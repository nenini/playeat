# PR11 Boss Battle Participants

## 핵심 정책

- 보스전 참여자는 보스전 시작 시점의 active guild member로 고정한다.
- 보스전 HP는 시작 시점 참여자 수 기준으로 계산하며, 이후 가입/탈퇴로 변경하지 않는다.
- 퀘스트 생성은 `guild_members`가 아니라 `boss_battle_participants` 기준으로 수행한다.
- 보스전 시작 후 새로 가입한 길드원은 진행 중인 보스전의 참여자, 퀘스트 생성 대상, 기여도 집계 대상이 아니다.
- 보스전 중 탈퇴하거나 추방된 참여자는 기록을 삭제하지 않고 `LEFT` 또는 `KICKED` 상태로 유지한다.
- `LEFT`, `KICKED` 참여자는 미완료 퀘스트를 검증할 수 없다.
- 이미 완료한 퀘스트, 데미지 로그, 기여도, 보상 기록은 유지한다.

## 참여자 상태

| status | 의미 |
| --- | --- |
| `ACTIVE` | 현재 보스전에 참여 가능한 상태 |
| `LEFT` | 보스전 도중 길드에서 탈퇴한 상태 |
| `KICKED` | 보스전 도중 길드에서 추방된 상태 |

## 마이그레이션

```bash
docker exec -i nyamnyam-mysql mysql --default-character-set=utf8mb4 -u root -p비밀번호 nyamnyam < scripts/18-boss-battle-participants-pr11-migration.sql
```

실행 순서:

```text
07-boss-pr4-migration.sql
08-boss-sugar-dragon-seed.sql
09-boss-battle-pr5-migration.sql
10-food-domain-migration.sql
11-foods-seed.sql
12-quest-pr6-migration.sql
13-boss-battle-balance-migration.sql
14-guild-chat-pr9-migration.sql
15-coin-shop-item-pr10-migration.sql
16-ranking-dashboard-pr8-migration.sql
17-quest-verification-reward-pr7-migration.sql
18-boss-battle-participants-pr11-migration.sql
```
