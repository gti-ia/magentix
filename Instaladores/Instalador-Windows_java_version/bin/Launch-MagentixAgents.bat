@echo off
if not "%MAGENTIX_HOME%"=="" goto gotHome
:gotHome
cd %MAGENTIX_HOME%

set LIBS=%LIBS%;..\lib\magentix2-2.03-jar-with-dependencies.zip
set LIBS=%LIBS%;StartMagentix.jar



> null  ping -n 15 localhost

start "Launching platform agents" java -cp "%LIBS%" es.upv.dsic.gti_ia.StartMagentixDesktop.Run

start "Launching platform agents" java -cp "%LIBS%" es.upv.dsic.gti_ia.StartMagentixDesktop.RunHttpInterface

start "Launching platform agents" java -cp "%LIBS%" es.upv.dsic.gti_ia.StartMagentixDesktop.RunTM

