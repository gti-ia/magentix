
@echo off
if not "%MAGENTIX_HOME%"=="" goto gotHome
:gotHome
cd %MAGENTIX_HOME%

set MAGENTIX_JAR=lib\magentix2-%VERSION%-jar-with-dependencies.zip
set LIBS=%LIBS%;%MAGENTIX_JAR%
set LIBS=%LIBS%;bin\StartMagentix.jar

call bin\setJavaHome.bat

if not "%QPID_HOME%" == "" goto end else goto gotHome
:goHome
set QPID_HOME=bin\qpid-broker-0.20
goto end

:end
start "Qpid" "%QPID_HOME%\bin\qpid-server.bat"

::wait 6 seconds until qpid is started
> null  ping -n 10 localhost
del null

start "Launching platform agents" java -cp "%LIBS%" es.upv.dsic.gti_ia.StartMagentixDesktop.Run
start "Launching HttpInterface agent" java -cp "%LIBS%" es.upv.dsic.gti_ia.StartMagentixDesktop.RunHttpInterface
start "Launching Trace Manager agent" java -cp "%LIBS%" es.upv.dsic.gti_ia.StartMagentixDesktop.RunTM
