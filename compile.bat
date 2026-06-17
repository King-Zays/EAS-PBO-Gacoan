@echo off
setlocal EnableExtensions EnableDelayedExpansion
echo =========================================================
echo       MIE GACOAN JNI COMPILER ^& BUILD SYSTEM (WIN64)
echo =========================================================
echo.

REM 1. Create bin directory if it doesn't exist
if not exist bin (
    echo [*] Creating bin/ directory...
    mkdir bin
)

REM 2. Check for JAVA_HOME env variable
if "%JAVA_HOME%"=="" (
    echo [!] WARNING: JAVA_HOME environment variable is not defined!
    echo Attempting to detect JDK from javac on PATH...
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

REM 3. Compile Java classes and generate JNI Headers in the root
echo [*] [1/3] Compiling Java classes and generating JNI Headers...
javac -d bin -h . gacoan/Menu.java gacoan/ItemPesanan.java gacoan/TransaksiPesanan.java gacoan/GacoanEngine.java gacoan/SistemNotifikasi.java gacoan/PreFlightCheck.java gacoan/UITheme.java gacoan/GacoanApp.java
if %ERRORLEVEL% neq 0 (
    echo [X] ERROR: Java compilation failed!
    pause
    exit /b %ERRORLEVEL%
)
echo [✓] Java classes compiled successfully.

REM 4. Compile C++ GacoanEngine.dll
echo [*] [2/3] Compiling C++ JNI Dynamic Library (GacoanEngine.dll)...
set "CPP_COMPILER=C:\Program Files (x86)\Dev-Cpp\MinGW64\bin\g++.exe"

if not exist "!CPP_COMPILER!" (
    echo [X] ERROR: 64-bit C++ compiler not found at:
    echo     !CPP_COMPILER!
    pause
    exit /b 1
)

echo [*] Using C++ compiler: !CPP_COMPILER!
echo [*] Compiler target machine:
"!CPP_COMPILER!" -dumpmachine
if %ERRORLEVEL% neq 0 (
    echo [X] ERROR: Failed to run compiler dumpmachine check.
    pause
    exit /b %ERRORLEVEL%
)

if exist bin\GacoanEngine.dll (
    echo [*] Removing old bin\GacoanEngine.dll...
    del /f /q bin\GacoanEngine.dll
)

"C:\Program Files (x86)\Dev-Cpp\MinGW64\bin\g++.exe" -std=c++11 -shared -O2 -I"%JAVA_HOME%\include" -I"%JAVA_HOME%\include\win32" -I. GacoanEngine.cpp -o bin\GacoanEngine.dll -lole32 -loleaut32 -luuid -lwinmm
if %ERRORLEVEL% neq 0 (
    echo [X] ERROR: C++ Compilation failed!
    pause
    exit /b %ERRORLEVEL%
)
echo [✓] GacoanEngine.dll compiled successfully in bin/.

REM 5. Run the application
echo [*] [3/3] Running Gacoan Self-Ordering Application...
echo.
java -Djava.library.path=bin -cp bin gacoan.GacoanApp
if %ERRORLEVEL% neq 0 (
    echo.
    echo [!] Application exited with code %ERRORLEVEL%.
)
echo.
echo =========================================================
echo                  BUILD PROCESS COMPLETED
echo =========================================================
pause
