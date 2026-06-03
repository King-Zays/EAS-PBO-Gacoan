@echo off
echo =========================================================
echo      RUNNING QT INSTALLER WITH HIGH-SPEED MIRROR
echo =========================================================
echo.
echo Meluncurkan installer Qt menggunakan mirror regional USTC...
echo.
cd /d "C:\Users\refia\Downloads"
qt-online-installer-windows-x64-4.11.0.exe --mirror https://mirrors.ustc.edu.cn/qtproject
echo.
echo Selesai.
pause
