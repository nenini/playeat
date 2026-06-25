# ── Stage 1: Build ──────────────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# 의존성 캐시 레이어 (소스 변경 시에도 재다운로드 방지)
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .

RUN chmod +x mvnw && \
    ./mvnw dependency:go-offline -B --no-transfer-progress

# 소스 복사 및 빌드
COPY src ./src
RUN ./mvnw clean package -DskipTests -B --no-transfer-progress

# ── Stage 2: Runtime ─────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 타임존 설정 (KST)
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone && \
    apk del tzdata

# 비루트 사용자 생성 (보안)
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# 빌드 결과물 복사
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

# JVM 옵션 (컨테이너 메모리 인식)
ENTRYPOINT ["java", \
            "-XX:+UseContainerSupport", \
            "-XX:MaxRAMPercentage=75.0", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-jar", "app.jar"]
