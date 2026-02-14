@echo off
REM 헥사고날 아키텍처 테스트 실행 스크립트

echo ===============================================
echo 헥사고날 아키텍처 전환 후 테스트 실행
echo ===============================================
echo.

REM 모든 테스트 실행
echo [1/3] 모든 테스트 실행 중...
call gradlew test

echo.
echo [2/3] User 도메인 테스트 실행 중...
call gradlew test --tests "com.example.starter.domain.user.application.service.UserServiceTest"

echo.
echo [3/3] Todo 도메인 테스트 실행 중...
call gradlew test --tests "com.example.starter.domain.todo.application.service.TodoServiceTest"

echo.
echo ===============================================
echo 테스트 실행 완료
echo ===============================================
