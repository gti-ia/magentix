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

		echo $1 > pass_root
		echo $2 > pass_broker
		
		root_directory=`pwd`
		root_directory=${root_directory}/pass_root 

		broker_directory=`pwd` 
             	broker_directory=${broker_directory}/pass_broker

		echo "***Creating the directory security***"
		mkdir security
		cd security

		echo "***Creating the directory CA_db***"
		mkdir CA_db
	
		echo "***Creating new CA certificate and key databases.***"
		certutil -N -f $root_directory -d CA_db 
	
		echo "***Creating an MagentixCA certificate and adding it to a certificate database. ***"
		certutil -S -f $root_directory -d CA_db -n "MagentixCA" -s "CN=MagentixCA, O=Magentix" -t "CT,," -x

		echo "***Exporting CA certificate in CA_db***"
		certutil -L -f $root_directory -d CA_db -n "MagentixCA" -a -o CA_db/rootca.crt

		echo "***Displays the CA_db content***"
		certutil -L -f $root_directory -d CA_db

		#Parte para los certificados del broker

	
		echo "***Creating the directory broker_db***"
		mkdir broker_db
	
		echo "***Creating new broker certificate and key databases. ***"
		certutil -N -f $broker_directory -d broker_db
		echo "***Adding an existing certificate to a certificate database***"
		certutil -A -f $root_directory -d broker_db -n "MagentixCA" -t "TC,," -a -i CA_db/rootca.crt
		echo "***Creating a certificate-request file that can be submitted to a Certificate Authority (CA) for processing into a finished certificate.***"
		certutil -R -f $broker_directory -d broker_db -s "CN=broker,O=Magentix" -a -o broker_db/server.req
		echo "***Creating a new binary certificate file from a binary certificate-request file***"
		certutil -C -f $root_directory -d CA_db -c "MagentixCA" -a -i broker_db/server.req -o broker_db/server.crt 
		echo "***Adding server certificate to a certificate database***"
		certutil -A -f $broker_directory -d broker_db -n broker -a -i broker_db/server.crt -t ",,"
		echo "***Checks the validity of a certificate and its attributes***"
		certutil -V -f $broker_directory -d broker_db -u V -n broker

		
		#Parte del MMS

		echo "***Imports a root certificate into MMS keystore***"
		keytool -import -trustcacerts -alias MagentixCA -noprompt -file CA_db/rootca.crt -storepass $3 -keypass $3 -keystore MMSkeystore.jks
		echo "***Imports a root certificate into MMS truststore***"
		keytool -import -trustcacerts -alias MagentixCA -noprompt -file CA_db/rootca.crt -storepass $3 -keypass $3 -keystore MMStruststore.jks
		echo "***Generates a key pair with CN=MMS and O=Magentix***"
		keytool -genkey -alias MMS -keyalg RSA -dname "CN=MMS,O=Magentix" -storepass $3 -keypass $3 -keystore MMSkeystore.jks
		echo "***Creates request certificate with name mms.csr***"
		keytool -certreq -alias MMS -storepass $3 -keypass $3 -keystore MMSkeystore.jks -file mms.csr
		echo "***Creates a signed certificate with name mms.crt***"
		certutil -C -f $broker_directory -d CA_db/ -c "MagentixCA" -a -i mms.csr -o mms.crt
		echo "***Imports a mms.crt in MMS keystore***"
		keytool -import -trustcacerts -alias MMS -file mms.crt -storepass $3 -keypass $3 -keystore MMSkeystore.jks
		echo "***Exports certificate***"
		keytool -export -alias MMS -file mms.crt -storepass $3 -keypass $3 -keystore MMSkeystore.jks

		rm $root_directory
		rm $broker_directory;;

	* )	
		usage
		exit 0;;


	
esac





