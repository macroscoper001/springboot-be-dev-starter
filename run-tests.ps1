# 헥사고날 아키텍처 테스트 실행 스크립트 (PowerShell)

Write-Host "===============================================" -ForegroundColor Cyan
Write-Host "헥사고날 아키텍처 전환 후 테스트 실행" -ForegroundColor Cyan
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host ""

# 현재 디렉토리 확인
$projectDir = Get-Location
Write-Host "프로젝트 디렉토리: $projectDir" -ForegroundColor Yellow
Write-Host ""

# 모든 테스트 실행
Write-Host "[1/4] 모든 테스트 실행 중..." -ForegroundColor Green
Write-Host "명령어: gradlew test" -ForegroundColor Gray
& .\gradlew test

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ 일부 테스트가 실패했습니다." -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "[2/4] User 도메인 테스트 실행 중..." -ForegroundColor Green
Write-Host "명령어: gradlew test --tests UserServiceTest" -ForegroundColor Gray
& .\gradlew test --tests "com.example.starter.domain.user.application.service.UserServiceTest"

Write-Host ""
Write-Host "[3/4] Todo 도메인 테스트 실행 중..." -ForegroundColor Green
Write-Host "명령어: gradlew test --tests TodoServiceTest" -ForegroundColor Gray
& .\gradlew test --tests "com.example.starter.domain.todo.application.service.TodoServiceTest"

Write-Host ""
Write-Host "[4/4] 컨트롤러 테스트 실행 중..." -ForegroundColor Green
Write-Host "명령어: gradlew test --tests UserControllerTest" -ForegroundColor Gray
& .\gradlew test --tests "com.example.starter.domain.user.adapter.in.web.UserControllerTest"

Write-Host ""
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host "✅ 테스트 실행 완료!" -ForegroundColor Green
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "📊 검증 보고서: HEXAGONAL_ARCHITECTURE_VERIFICATION.md" -ForegroundColor Yellow
