# 데이터베이스 초기화 스크립트

`BackEnd/docker-compose.yml`은 `BackEnd/scripts`를 MySQL 컨테이너의 `/docker-entrypoint-initdb.d`로 마운트합니다.

따라서 새 DB 볼륨으로 재빌드하면 `BackEnd/scripts` 바로 아래의 SQL 파일들이 이름순으로 자동 실행됩니다.

## 실행 대상

- `init.sql`: 스키마 생성
- `seed-data.sql`: 코치, 영양 기준, 아이템, 보스, 퀘스트 등 서비스 기본 데이터
- `seed-foods-full.sql`: 실제 전체 음식 데이터

## 보관용 데이터

- `sample/seed-foods-sample.sql`: 테스트 시간을 줄이기 위해 사용했던 임시 음식 데이터입니다. scripts 하위 디렉터리에 있어 Docker 초기화 때 자동 실행되지 않습니다.

## 실행 중인 컨테이너에 전체 음식 데이터 다시 넣기

이미 실행 중인 컨테이너에 전체 음식 데이터를 수동으로 다시 넣고 싶을 때는 아래 명령을 사용합니다.

```powershell
docker cp BackEnd\scripts\seed-foods-full.sql nyamnyam-mysql:/tmp/seed-foods-full.sql
docker exec nyamnyam-mysql mysql --default-character-set=utf8mb4 -uroot -proot1234 -D nyamnyam -e "source /tmp/seed-foods-full.sql"
```
