#!/bin/bash


#$PASSWORD_CA contraseña de la base de datos CA.
#$PASSWORD_BROKER contraseña de la base de datos del broker.
#$PASWORD_MMS contraseña del MMS.


case $# in
	0 )	
		echo "\n\n\033[1mUsage:\033[0m sh MagentixPKI.sh [FULL_PATH/FILE_PASSWORD_CA] [FULL_PATH/FILE_PASSWORD_BROKER] PASSWORD_MMS\n\n
\033[1mDescription:\033[0m This command will create the Magentix2 Public Key Infraestructure. This infrastructure will contain the root certificate authority, and all certificates for MMS and QPID broker.\n
\033[1mFULL_PATH/FILE_PASSWORD_CA (Optional):\033[0m Specify a file that will automatically supply the password to include in a certificate or to access a ROOT CA certificate database. This is a plain-text file containing one password. Be sure to prevent unauthorized access to this file.\n
\033[1mFULL_PATH/FILE_PASSORD_BROKER (Optional):\033[0m Specify a file that will automatically supply the password to include in a certificate or to access a BROKER certificate database. This is a plain-text file containing one password. Be sure to prevent unauthorized access to this file.\n
\033[1mPASSORD_MMS (Required):\033[0m Specify a password for MMS keystore.
		
"
		exit 0;;
	1 ) #Se asume que solamente tiene el PASSWORD_MMS
		echo "***Creating the directory security***"
		mkdir security
		cd security

		echo "***Creating the directory CA_db***"
		mkdir CA_db

		echo "***Creating new CA certificate and key databases.***"
		certutil -N -d CA_db 
	
		echo "***Create an MagentixCA certificate and add it to a certificate database. ***"
		certutil -S -d CA_db -n "MagentixCA" -s "CN=MagentixCA, O=Magentix" -t "CT,," -x

		echo "***Export CA certificate in CA_db***"
		certutil -L -d CA_db -n "MagentixCA" -a -o CA_db/rootca.crt

		echo "***Display the CA_db content***"
		certutil -L -d CA_db

		#Parte para los certificados del broker

		echo "***Creating the directory broker_db***"
		mkdir broker_db
		echo "***Creating new broker certificate and key databases. ***"
		certutil -N -d broker_db

		echo "***Adding an existing certificate to a certificate database***"
		certutil -A -d broker_db -n "MagentixCA" -t "TC,," -a -i CA_db/rootca.crt

		echo "***Creating a certificate-request file that can be submitted to a Certificate Authority (CA) for processing into a finished certificate.***"
		certutil -R -d broker_db -s "CN=broker,O=Magentix" -a -o broker_db/server.req

		echo "***Creating a new binary certificate file from a binary certificate-request file***"
		certutil -C -d CA_db -c "MagentixCA" -a -i broker_db/server.req -o broker_db/server.crt 

		echo "***Adding server certificate to a certificate database***"
		certutil -A -d broker_db -n broker -a -i broker_db/server.crt -t ",,"

		echo "***Check the validity of a certificate and its attributes***"
		certutil -V -d broker_db -u V -n broker

		#Parte del MMS

		echo "***Import a root certificate into MMS keystore***"
		keytool -import -trustcacerts -noprompt -alias MagentixCA -file CA_db/rootca.crt -storepass $3 -keypass $3 -keystore MMSkeystore.jks
		echo "***Import a root certificate into MMS truststore***"
		keytool -import -trustcacerts -noprompt -alias MagentixCA -file CA_db/rootca.crt -storepass $3 -keypass $3 -keystore MMStruststore.jks
		echo "***Generate a key pair with CN=MMS and O=Magentix***"
		keytool -genkey -alias MMS -keyalg RSA -dname "CN=MMS,O=Magentix" -storepass $3 -keypass $3 -keystore MMSkeystore.jks
		echo "***Create request certificate with name mms.csr***"
		keytool -certreq -alias MMS -storepass $3 -keypass $3 -keystore MMSkeystore.jks -file mms.csr
		echo "***Create a signed certificate with name mms.crt***"
		certutil -C -d CA_db/ -c "MagentixCA" -a -i mms.csr -o mms.crt
		echo "***Import a mms.crt in MMS keystore***"
		keytool -import -trustcacerts -alias MMS -file mms.crt -storepass $3 -keypass $3 -keystore MMSkeystore.jks
		echo "***Export certificate***"
		keytool -export -alias MMS -file mms.crt -storepass $3 -keypass $3 -keystore MMSkeystore.jks;;

	

		
		* ) #Se asume que ha puesto los dos ficheros con las contraseñas

		echo "***Creating the directory security***"
		mkdir security
		cd security

		echo "***Creating the directory CA_db***"
		mkdir CA_db

		echo "***Creating new CA certificate and key databases.***"
		certutil -N -f $1 -d CA_db 
	
		echo "***Create an MagentixCA certificate and add it to a certificate database. ***"
		certutil -S -f $1 -d CA_db -n "MagentixCA" -s "CN=MagentixCA, O=Magentix" -t "CT,," -x

		echo "***Export CA certificate in CA_db***"
		certutil -L -f $1 -d CA_db -n "MagentixCA" -a -o CA_db/rootca.crt

		echo "***Display the CA_db content***"
		certutil -L -f $1 -d CA_db

		#Parte para los certificados del broker

	
		echo "***Creating the directory broker_db***"
		mkdir broker_db
		echo "***Creating new broker certificate and key databases. ***"
		certutil -N -f $2 -d broker_db
		echo "***Adding an existing certificate to a certificate database***"
		certutil -A -f $1 -d broker_db -n "MagentixCA" -t "TC,," -a -i CA_db/rootca.crt
		echo "***Creating a certificate-request file that can be submitted to a Certificate Authority (CA) for processing into a finished certificate.***"
		certutil -R -f $2 -d broker_db -s "CN=broker,O=Magentix" -a -o broker_db/server.req
		echo "***Creating a new binary certificate file from a binary certificate-request file***"
		certutil -C -f $1 -d CA_db -c "MagentixCA" -a -i broker_db/server.req -o broker_db/server.crt 
		echo "***Adding server certificate to a certificate database***"
		certutil -A -f $2 -d broker_db -n broker -a -i broker_db/server.crt -t ",,"
		echo "***Check the validity of a certificate and its attributes***"
		certutil -V -f $2 -d broker_db -u V -n broker

		
		#Parte del MMS

		echo "***Import a root certificate into MMS keystore***"
		keytool -import -trustcacerts -alias MagentixCA -noprompt -file CA_db/rootca.crt -storepass $3 -keypass $3 -keystore MMSkeystore.jks
		echo "***Import a root certificate into MMS truststore***"
		keytool -import -trustcacerts -alias MagentixCA -noprompt -file CA_db/rootca.crt -storepass $3 -keypass $3 -keystore MMStruststore.jks
		echo "***Generate a key pair with CN=MMS and O=Magentix***"
		keytool -genkey -alias MMS -keyalg RSA -dname "CN=MMS,O=Magentix" -storepass $3 -keypass $3 -keystore MMSkeystore.jks
		echo "***Create request certificate with name mms.csr***"
		keytool -certreq -alias MMS -storepass $3 -keypass $3 -keystore MMSkeystore.jks -file mms.csr
		echo "***Create a signed certificate with name mms.crt***"
		certutil -C -f $1 -d CA_db/ -c "MagentixCA" -a -i mms.csr -o mms.crt
		echo "***Import a mms.crt in MMS keystore***"
		keytool -import -trustcacerts -alias MMS -file mms.crt -storepass $3 -keypass $3 -keystore MMSkeystore.jks
		echo "***Export certificate***"
		keytool -export -alias MMS -file mms.crt -storepass $3 -keypass $3 -keystore MMSkeystore.jks;;

	
esac





