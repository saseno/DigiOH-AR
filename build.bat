@ECHO OFF 
TITLE DigiOH-AR
ECHO ============================
ECHO build DigiOH-AR
ECHO ============================
ECHO _
call mvn clean
call mvn clean verify
ECHO _
ECHO ============================
IF %ERRORLEVEL% NEQ 0 Echo An error was found
IF %ERRORLEVEL% EQU 0 Echo No error found
ECHO ============================