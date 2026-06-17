@echo off
setlocal EnableExtensions

echo =========================================================
echo       MIE GACOAN PURE JAVA BUILD SYSTEM
echo =========================================================
echo.

if not exist bin (
    echo [*] Creating bin/ directory...
    mkdir bin
)

if "%JAVA_HOME%"=="" (
    echo [!] JAVA_HOME is not defined. Detecting JDK from javac on PATH...
    for /f "delims=" %%J in ('where javac 2^>nul') do (
        set "JAVA_HOME=%%~dpJ.."
        goto :JAVA_HOME_FOUND
    )
    echo [X] ERROR: javac was not found on PATH. Please install JDK or set JAVA_HOME.
    pause
    exit /b 1
)

:JAVA_HOME_FOUND
for %%D in ("%JAVA_HOME%") do set "JAVA_HOME=%%~fD"

echo [*] Using JDK from: %JAVA_HOME%

if exist bin\GacoanEngine.dll (
    echo [*] Removing old native DLL artifact because this build is pure Java...
    del /f /q bin\GacoanEngine.dll
)

echo [*] [1/2] Compiling Java classes...
javac -d bin gacoan/Menu.java gacoan/ItemPesanan.java gacoan/TransaksiPesanan.java gacoan/GacoanEngine.java gacoan/SistemNotifikasi.java gacoan/PreFlightCheck.java gacoan/UITheme.java gacoan/GacoanApp.java
if %ERRORLEVEL% neq 0 (
    echo [X] ERROR: Java compilation failed!
    pause
    exit /b %ERRORLEVEL%
)
echo [OK] Java classes compiled successfully.

echo [*] [2/2] Running Gacoan Pure Java Application...
echo.
java -cp bin gacoan.GacoanApp
if %ERRORLEVEL% neq 0 (
    echo.
    echo [!] Application exited with code %ERRORLEVEL%.
)

echo.
echo =========================================================
echo                  BUILD PROCESS COMPLETED
echo =========================================================
pause
