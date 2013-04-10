
@echo off

if "%EXAMPLES_HOME%" == "" goto notgotHome else goto gotHome
:gotHome
cd %EXAMPLES_HOME%
goto end
:notgotHome
cd  ..
goto end

:end

> null ping -n 5 localhost

set LIBS=%LIBS%;../lib/magentix2-2.03-jar-with-dependencies.zip
set LIBS=%LIBS%;MagentixExamples.jar

java -cp "%LIBS%" SingleAgent_Example.Run

cd bin
