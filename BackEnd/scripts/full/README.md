# 전체 음식 시드

`10-foods-seed-full.sql`은 전체 음식 데이터 시드 파일입니다.

로컬 개발에서 Docker MySQL 초기화 시간이 길어지지 않도록 `BackEnd/scripts` 바로 아래가 아니라 `BackEnd/scripts/full`에 보관합니다.

전체 음식 데이터를 수동으로 넣고 싶을 때는 아래 명령을 사용합니다.

```powershell
docker cp BackEnd\scripts\full\10-foods-seed-full.sql nyamnyam-mysql:/tmp/10-foods-seed-full.sql
docker exec nyamnyam-mysql mysql --default-character-set=utf8mb4 -uroot -proot1234 -D nyamnyam -e "source /tmp/10-foods-seed-full.sql"
```
