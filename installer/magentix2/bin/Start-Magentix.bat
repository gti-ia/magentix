@echo off
if not "%MAGENTIX_HOME%"=="" goto gotHome
:gotHome
cd %MAGENTIX_HOME%

set LIBS=%LIBS%;..\lib\magentix2-2.03-jar-with-dependencies.zip
set LIBS=%LIBS%;StartMagentix.jar

call qpid-broker-0.20\bin\qpid-server.bat

start "Launching platform agents" java -cp "%LIBS%" es.upv.dsic.gti_ia.StartMagentixDesktop.Run

start "Launching platform agents" java -cp "%LIBS%" es.upv.dsic.gti_ia.StartMagentixDesktop.RunHttpInterface

start "Launching platform agents" java -cp "%LIBS%" es.upv.dsic.gti_ia.StartMagentixDesktop.RunTM

