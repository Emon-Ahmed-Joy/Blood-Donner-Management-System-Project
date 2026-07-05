@echo off
echo Recompiling project...
if not exist bin mkdir bin
javac -d bin -cp "lib/*" src/database/*.java src/model/*.java src/ui/*.java
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b %errorlevel%
)

echo Starting Blood Donor Management System...
java -cp "bin;lib/*" ui.LoginPage
pause
