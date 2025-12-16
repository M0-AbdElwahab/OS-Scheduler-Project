@echo off
echo Compiling Java files...
javac -cp "lib/gson-2.10.1.jar" -d target/classes src/main/java/org/example/*.java

if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Running all test cases...
java -cp "lib/gson-2.10.1.jar;target/classes" org.example.TestRunner
pause
