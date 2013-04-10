@echo off
echo "Launching Qpid java"

if not "%QPID_HOME%" == "" goto end else goto gotHome
:goHome
set QPID_HOME=..\..\qpid-broker-0.14
goto end

:end
start "Qpid" "%QPID_HOME%\bin\qpid-server.bat"

echo "Qpid java launched"