
@echo off

cd Qpid


call Start-Qpid.bat

cd ..\bin\MySQL

call Start-MySQL.bat

cd ..\bin\Tomcat

call Start-Catalina.bat



cd ..

call Launch-MagentixAgents.bat


