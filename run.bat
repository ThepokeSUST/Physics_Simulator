@echo off
setlocal

:: Set the JavaFX SDK path
set JAVAFX_PATH=lib\javafx\lib

:: Create output directory
if not exist bin mkdir bin

:: Compile the application source tree
javac ^
    --module-path "%JAVAFX_PATH%" ^
    --add-modules javafx.controls,javafx.graphics ^
    -d bin ^
    src/com/physicssim/app/*.java ^
    src/com/physicssim/components/*.java ^
    src/com/physicssim/model/*.java ^
    src/com/physicssim/theme/*.java ^
    src/com/physicssim/views/*.java ^
    src/com/physicssim/navigation/*.java ^
    src/com/physicssim/features/pendulum/*.java ^
    src/com/physicssim/features/mechanics/*.java ^
    src/com/physicssim/features/simulations/*.java

if %errorlevel% neq 0 (
    echo Compilation failed.
    exit /b 1
)

:: Copy resources into bin
xcopy src\resources bin /E /I /Y >nul

if %errorlevel% neq 0 (
    echo Failed to copy resources.
    exit /b 1
)

:: Run the Java program
java ^
    --module-path "%JAVAFX_PATH%" ^
    --add-modules javafx.controls,javafx.graphics ^
    -cp bin ^
    com.physicssim.app.PhysicsSimulatorApp

endlocal