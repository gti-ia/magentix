


#Start the MySQL daemon
cd ../MySQL

sh Start-MySQL.sh

sleep 2
# Load database
cd ../security

sh Import-MMSDb.sh mypassword

#Launch Tomcat
cd ../Tomcat

sh Start-Catalina.sh

cd ../Qpid
#Start Qpid

sh Start-QpidWithSecurity.sh &
sleep 3
cd ..
#Launch agents OMS, SF and others -.--

sh Launch-MagentixAgents.sh





