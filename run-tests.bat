@echo off
REM Gradle 테스트 실행 스크립트 - Gradle Wrapper 자동 초기화

setlocal enabledelayedexpansion
cd /d "%~dp0"

echo.
echo ════════════════════════════════════════════════════════════
echo   Spring Boot Gradle 테스트 자동 실행
echo ════════════════════════════════════════════════════════════
echo.

echo [1/4] 프로젝트 경로: %cd%
echo.

REM Java 설치 확인
echo [2/4] Java 설치 확인 중...
java -version >nul 2>&1
if errorlevel 1 (
    echo.
    echo ❌ Java가 설치되지 않았습니다!
    echo Java 21을 다음에서 설치해주세요:
    echo https://www.oracle.com/java/technologies/downloads/#java21
    echo.
    pause
    exit /b 1
)
echo ✅ Java 설치됨
echo.

REM gradle-wrapper.jar 확인 및 다운로드
echo [3/4] Gradle Wrapper 확인 중...
if exist "%cd%\gradle\wrapper\gradle-wrapper.jar" (
    for /F %%A in ('powershell -Command "(Get-Item "%cd%\gradle\wrapper\gradle-wrapper.jar").Length"') do set JAR_SIZE=%%A
    if !JAR_SIZE! gtr 1000000 (
        echo ✅ gradle-wrapper.jar 발견 ^(!JAR_SIZE! bytes^)
        goto test_start
    )
)

echo ⚠️  gradle-wrapper.jar을 찾을 수 없거나 손상되었습니다.
echo Gradle 8.4 다운로드 중... ^(약 200MB, 시간이 걸릴 수 있습니다^)
echo.

if not exist "%cd%\gradle\wrapper" mkdir "%cd%\gradle\wrapper"

REM PowerShell을 사용하여 ZIP 다운로드 및 추출
powershell -Command "& { [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; $ProgressPreference = 'SilentlyContinue'; try { Write-Host 'Gradle 다운로드 중...'; Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-8.4-bin.zip' -OutFile '%cd%\gradle-8.4.zip' -TimeoutSec 300; Write-Host 'ZIP 파일 추출 중...'; Expand-Archive -Path '%cd%\gradle-8.4.zip' -DestinationPath '%cd%' -Force; if (Test-Path '%cd%\gradle-8.4\gradle\wrapper\gradle-wrapper.jar') { Copy-Item -Path '%cd%\gradle-8.4\gradle\wrapper\gradle-wrapper.jar' -Destination '%cd%\gradle\wrapper\gradle-wrapper.jar' -Force; Write-Host 'gradle-wrapper.jar 설치 완료'; Remove-Item '%cd%\gradle-8.4' -Recurse -Force; Remove-Item '%cd%\gradle-8.4.zip' -Force } } catch { Write-Host 'Error: $_'; exit 1 } }"

if errorlevel 1 (
    echo.
    echo ❌ Gradle Wrapper 다운로드 실패
    pause
    exit /b 1
)

echo ✅ Gradle Wrapper 준비 완료
echo.

:test_start
echo [4/4] 테스트 실행 중...
echo.

REM 모든 테스트 실행
call .\gradlew.bat test

set TEST_EXIT_CODE=%errorlevel%

echo.
echo ════════════════════════════════════════════════════════════
if %TEST_EXIT_CODE% equ 0 (
    echo   ✅ 테스트 완료 성공!
) else (
    echo   ❌ 테스트 실패 ^(Exit Code: %TEST_EXIT_CODE%^)
)
echo ════════════════════════════════════════════════════════════
echo.

pause
exit /b %TEST_EXIT_CODE%
