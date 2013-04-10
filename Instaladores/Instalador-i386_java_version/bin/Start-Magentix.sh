


#Start the MySQL daemon
cd MySQL

sh Start-MySQL.sh

cd ..

#Launch Tomcat

cd Tomcat

sh Start-Catalina.sh

cd ../Qpid
#Start Qpid

sh Start-Qpid.sh &

cd ..
#Launch agents OMS, SF and others -.--

sh Launch-MagentixAgents.sh
