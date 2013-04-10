#!/bin/bash


#$PASSWORD_CA contraseña de la base de datos CA.
#$PASSWORD_BROKER contraseña de la base de datos del broker.
#$PASWORD_MMS contraseña del MMS.

usage()
{

	echo "\n\n\033[1mUsage:\033[0m sh MagentixPKI.sh PASSWORD_CA PASSWORD_BROKER PASSWORD_MMS\n\n
\033[1mDescription:\033[0m This command will create the Magentix2 Public Key Infraestructure. This infrastructure will contain the root certificate authority, and all certificates for MMS and QPID broker.\n
\033[1mPASSWORD_CA (Required):\033[0m Specify a password for CA.\n
\033[1mPASSORD_BROKER (Required):\033[0m Specify a password for Broker.\n
\033[1mPASSORD_MMS (Required):\033[0m Specify a password for MMS keystore.
		
"
}
case $# in
  	
		3 ) #Se asume que ha puesto los dos ficheros con las contraseñas

		#cd $SECURITY_PATH
		

	
		echo $1 > pass_root
		echo $2 > pass_broker
		
		

		root_directory=`pwd`
		root_password=${root_directory}/pass_root 

		broker_directory=`pwd` 
             	broker_password=${broker_directory}/pass_broker

		echo $2 > pfile


		#echo "***Creating the directory security***"
		#mkdir security
		#cd security

		echo "***Creating the directory CA_db***"
		mkdir CA_db
	
		echo "***Creating new CA certificate and key databases.***"
		certutil -N -b "301230245959" -f $root_password -d CA_db 
	
		echo "***Creating an MagentixCA certificate and adding it to a certificate database. ***"
	




		certutil -S -f $root_password -d CA_db -n "MagentixCA" -s "CN=MagentixCA, O=Magentix" -t "CT,," -x -z /etc/passwd
	

		echo "***Exporting CA certificate in CA_db***"
		certutil -L -f $root_password -d CA_db -n "MagentixCA" -a -o CA_db/rootca.crt

		echo "***Displays the CA_db content***"
		certutil -L -f $root_password -d CA_db

		#Parte para los certificados del broker

		sleep 2
		echo "***Creating the directory broker_db***"
		mkdir broker_db
	
		echo "***Creating new broker certificate and key databases. ***"
		certutil -N -b "301230245959" -f $broker_password -d broker_db
		echo "***Adding an existing certificate to a certificate database***"
		certutil -A -f $broker_password -d broker_db -n "MagentixCA" -t "TC,," -a -i CA_db/rootca.crt
		echo "***Creating a certificate-request file that can be submitted to a Certificate Authority (CA) for processing into a finished certificate.***"
		echo tail -3 /etc/passwd > prandom
		certutil -R -f $broker_password -d broker_db -s "CN=broker,O=Magentix" -a -o broker_db/server.req -z prandom
		rm prandom
		echo "***Creating a new binary certificate file from a binary certificate-request file***"
		certutil -C -f $root_password -d CA_db -c "MagentixCA" -a -i broker_db/server.req -o broker_db/server.crt 

		echo "***Adding server certificate to a certificate database***"
		certutil -A -f $broker_password -d broker_db -n broker -a -i broker_db/server.crt -t ",,"

		echo "***Checks the validity of a certificate and its attributes***"
		certutil -V -f $broker_password -d broker_db -u V -n broker

		echo "         "
		echo "         "		
		#Parte del MMS

		echo "***Imports a root certificate into MMS keystore***"
		keytool -import -trustcacerts -alias MagentixCA -noprompt -file CA_db/rootca.crt -storepass $3 -keypass $3 -keystore MMSkeystore.jks
		echo "***Imports a root certificate into MMS truststore***"
		keytool -import -trustcacerts -alias MagentixCA -noprompt -file CA_db/rootca.crt -storepass $3 -keypass $3 -keystore MMStruststore.jks
		echo "***Generates a key pair with CN=MMS and O=Magentix***"
		keytool -genkey -validity 5000 -alias MMS -keyalg RSA -dname "CN=MMS,O=Magentix" -storepass $3 -keypass $3 -keystore MMSkeystore.jks
		echo "***Creates request certificate with name mms.csr***"
		keytool -certreq -alias MMS -storepass $3 -keypass $3 -keystore MMSkeystore.jks -file mms.csr
		echo "***Creates a signed certificate with name mms.crt***"
		certutil -C -f $root_password -d CA_db/ -c "MagentixCA" -a -i mms.csr -o mms.crt
		echo "***Imports a mms.crt in MMS keystore***"
		keytool -import -trustcacerts -alias MMS -file mms.crt -storepass $3 -keypass $3 -keystore MMSkeystore.jks
		echo "***Exports certificate***"
		keytool -export -alias MMS -file mms.crt -storepass $3 -keypass $3 -keystore MMSkeystore.jks

               #Añadir certificado del usuari
               keytool -noprompt -import -trustcacerts -alias magentix_user -file certs/user.crt -keystore MMSkeystore.jks -storepass $3 

               #Añadir el certificado de tm, el root.cat y el mms.crt

               
               keytool -delete -alias mms -storepass password -keystore certs/keystore.jks
               keytool -delete -alias MagentixCA -storepass password -keystore certs/keystore.jks
               keytool -delete -alias MagentixCA -storepass password -keystore certs/truststore.jks 
               keytool -noprompt -import -trustcacerts -alias mms -file mms.crt -storepass password -keystore certs/keystore.jks
               keytool -noprompt -import -trustcacerts -alias MagentixCA -file CA_db/rootca.crt -storepass password -keystore certs/truststore.jks
               keytool -noprompt -import -trustcacerts -alias MagentixCA -file CA_db/rootca.crt -storepass password -keystore certs/keystore.jks

             

               keytool -certreq -alias tm -storepass password -keypass password -keystore certs/keystore.jks -file certs/tm.csr

               certutil -C -d CA_db/ -c "MagentixCA" -a -i certs/tm.csr -o certs/tm.crt -f $root_password

               keytool -noprompt -import -trustcacerts -alias tm -file certs/tm.crt -storepass password -keypass password -keystore certs/keystore.jks 
		rm $root_password
		rm $broker_password;;


		#Configuramos el servicio MMS
		#Acceder a la ubicación del services.xml y modificar solomente el password, la direccion del MMS la dejamos ya en el directorio donde esta security

	* )	
		usage
		exit 0;;


	
esac





