# Gradle 테스트 실행 스크립트 (PowerShell)
# Gradle Wrapper 자동 초기화 및 테스트 실행

param(
    [string]$TestClass = "",
    [switch]$Clean = $false,
    [switch]$Verbose = $false
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Continue"

# 스크립트 디렉토리로 이동
$scriptPath = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptPath

Write-Host ""
Write-Host "════════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "  Spring Boot Gradle 테스트 자동 실행" -ForegroundColor Cyan
Write-Host "════════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""

# 현재 디렉토리 확인
$projectDir = Get-Location
Write-Host "📁 프로젝트 경로: $projectDir" -ForegroundColor Green

# Java 설치 확인
Write-Host ""
Write-Host "🔍 Java 설치 확인 중..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1
    Write-Host "✅ Java 설치됨" -ForegroundColor Green
} catch {
    Write-Host "❌ Java가 설치되지 않았습니다!" -ForegroundColor Red
    Write-Host "Java 21을 다음에서 설치해주세요:" -ForegroundColor Yellow
    Write-Host "https://www.oracle.com/java/technologies/downloads/#java21" -ForegroundColor Yellow
    Read-Host "엔터를 누르면 종료됩니다"
    exit 1
}

# Gradle Wrapper JAR 확인 및 다운로드
Write-Host ""
Write-Host "🔍 Gradle Wrapper 확인 중..." -ForegroundColor Yellow
$wrapperJar = Join-Path $projectDir "gradle\wrapper\gradle-wrapper.jar"
$jarExists = Test-Path $wrapperJar
$jarSize = if ($jarExists) { (Get-Item $wrapperJar).Length } else { 0 }

if (-not $jarExists -or $jarSize -lt 1000000) {
    Write-Host "⚠️  gradle-wrapper.jar을 찾을 수 없거나 손상되었습니다." -ForegroundColor Yellow
    Write-Host "Gradle 8.4 다운로드 중... (약 200MB, 시간이 걸릴 수 있습니다)" -ForegroundColor Cyan
    Write-Host ""

    $wrapperDir = Join-Path $projectDir "gradle\wrapper"
    if (-not (Test-Path $wrapperDir)) {
        New-Item -ItemType Directory -Path $wrapperDir -Force | Out-Null
    }

    try {
        [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
        $zipUrl = "https://services.gradle.org/distributions/gradle-8.4-bin.zip"
        $zipPath = Join-Path $projectDir "gradle-8.4.zip"

        Write-Host "   Gradle ZIP 다운로드 중..."
        Invoke-WebRequest -Uri $zipUrl -OutFile $zipPath -TimeoutSec 300 | Out-Null

        Write-Host "   ZIP 파일 추출 중..."
        Expand-Archive -Path $zipPath -DestinationPath $projectDir -Force

        $sourceJar = Join-Path $projectDir "gradle-8.4\gradle\wrapper\gradle-wrapper.jar"
        if (Test-Path $sourceJar) {
            Copy-Item -Path $sourceJar -Destination $wrapperJar -Force
            Write-Host "   ✅ gradle-wrapper.jar 설치 완료" -ForegroundColor Green

            # 정리
            Remove-Item (Join-Path $projectDir "gradle-8.4") -Recurse -Force -ErrorAction SilentlyContinue
            Remove-Item $zipPath -Force -ErrorAction SilentlyContinue
        } else {
            throw "gradle-wrapper.jar을 찾을 수 없습니다"
        }
    } catch {
        Write-Host "❌ 다운로드 실패: $_" -ForegroundColor Red
        Read-Host "엔터를 누르면 종료됩니다"
        exit 1
    }
} else {
    Write-Host "✅ gradle-wrapper.jar 발견" -ForegroundColor Green
}

# 테스트 실행
Write-Host ""
Write-Host "🚀 테스트 실행 중..." -ForegroundColor Cyan
Write-Host ""

$gradleArgs = @("test")

if ($Clean) {
    $gradleArgs = @("clean", "test")
    Write-Host "📝 모드: Clean 빌드 + 테스트" -ForegroundColor Yellow
}

if ($TestClass) {
    $gradleArgs += "--tests"
    $gradleArgs += $TestClass
    Write-Host "📝 테스트 클래스: $TestClass" -ForegroundColor Yellow
}

if ($Verbose) {
    $gradleArgs += "--info"
}

Write-Host ""

# Gradle 실행
& .\gradlew.bat $gradleArgs
$exitCode = $LASTEXITCODE

# 결과 표시
Write-Host ""
Write-Host "════════════════════════════════════════════════════════════" -ForegroundColor Cyan
if ($exitCode -eq 0) {
    Write-Host "  ✅ 테스트 완료 성공!" -ForegroundColor Green
} else {
    Write-Host "  ❌ 테스트 실패 (Exit Code: $exitCode)" -ForegroundColor Red
}
Write-Host "════════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""

Read-Host "엔터를 누르면 종료됩니다"
exit $exitCode
