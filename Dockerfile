# 멀티스테이지 빌드: 빌드 스테이지
FROM gradle:8.4-jdk21 AS builder

WORKDIR /build

# 소스 코드 복사
COPY . .

# 애플리케이션 빌드
RUN gradle build -x test --no-daemon

# 멀티스테이지 빌드: 런타임 스테이지
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=builder /build/build/libs/*.jar app.jar

# 포트 노출
EXPOSE 8080

# 헬스체크
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# 애플리케이션 시작
ENTRYPOINT ["java", "-jar", "app.jar"]
