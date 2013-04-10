



echo "Stopping MySQL daemon"
cd MySQL

call Stop-MySQL.bat

cd ..\bin

echo "Stopping Magentix Tomcat"
cd Tomcat

call Stop-Catalina.bat

cd ..\bin


call Stop-MagentixAgents.bat
cd Qpid

echo "Stopping Qpid broker"
call Stop-Qpid.bat

cd ..


