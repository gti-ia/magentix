


#Stop the MySQL daemon
echo "Stopping MySQL daemon"
cd MySQL


sh Stop-MySQL.sh

cd ..

#Stop Tomcat
echo "Stopping Magentix Tomcat"
cd Tomcat

sh Stop-Catalina.sh

cd ..
#Stop Magentix agents
echo "Stopping Magentix Agents (OMS, SF,...)"
sh Stop-MagentixAgents.sh
#Stop Qpid 
cd Qpid
echo "Stopping Qpid broker"
sh Stop-Qpid.sh

cd ..


