#!/bin/bash


#En este caso podremos crear o no la ca, si la tenemos creada podemos crear 1 o varios usuarios, facilitando el nombre y el pass.

#Tendremos que diferenciar entre estos numero de argumentos.

#0 Creara una CA y solicitara contraseña por el prompt
#1 Solamente desea crear la CA: incluye pass de la ca.
#3 Desea crear un usuario normal, tambien incluye password de la ca, nombre del usuario, y pass del usuario
#3 o más, password de la ca, y un par de valores por cada usuario que quiera registrar.





usage()
{

		echo "\n\n\033[1mUsage:\033[0m sh UsersPKI.sh PASSWORD_USERS_CA [User_Name User_Password].\n\n
\033[1mDescription:\033[0m This command will create the Users Public Key Infraestructure. This infrastructure will contain the user root certificate authority and all necesaries certificates for each users.\n
\033[1mPASSWORD_USERS_CA (Required):\033[0m Specify a file that will automatically supply the password to include in a certificate or to access a ROOT CA certificate database. This is a plain-text file containing one password. Be sure to prevent unauthorized access to this file.\n
\033[1m[User_Name,User_Password] (Optional):\033[0m  Specify a pair value with a name and password for a user keystore, one for each user."


}

		echo $1 > pass_root
		
		
		root_directory=`pwd`
		root_directory=${root_directory}/pass_root 



case $# in
	0 ) 
		echo "***Creating the directory CA_Users***"
		mkdir CA_Users

		echo "***Creating new CA certificate and key databases.***"
		certutil -N -d CA_Users
	
		echo "***Create an CA_Users certificate and add it to a certificate database. ***"
		certutil -S -d CA_Users -n "CA_Users" -s "CN=CA_Users, O=Magentix" -t "CT,," -x -2

		echo "***Export CA certificate in CA_Users***"
		certutil -L -d CA_Users -n "CA_Users" -a -o CA_Users/rootcausers.crt

		echo "***Display the CA_Users content***"
		certutil -L -d CA_Users;;

	

	1)
		

		echo "***Creating the directory CA_db***"
		mkdir CA_Users

		echo "***Creating new CA certificate and key databases.***"
		certutil -N -f $root_directory -d CA_Users
	
		echo "***Create an CAUsers certificate and add it to a certificate database. ***"
		certutil -S -f $root_directory -d CA_Users -n "CA_Users" -s "CN=CA_Users, O=Magentix" -t "CT,," -x -2
	
		echo "***Export CA certificate in CA_db***"
		certutil -L -f $root_directory -d CA_Users -n "CA_Users" -a -o CA_Users/rootcausers.crt
	
		echo "***Display the CA_Users content***"
		certutil -L -f $root_directory -d CA_Users;;


	2 )	
		usage
		exit 0;;		

	 *)

		#Comprobar que el número de argumentos no sea un número par.
		
		es_par=`expr $# % 2`

		if  [ ${es_par} -eq 0 ] 		
		then
			usage
			exit 0
		else
			
			num_users=0
			#Se asume que ha puesto los dos ficheros con las contraseñas
		
			num_users=`expr $# - 1`
			num_users=`expr ${num_users} / 2`
			echo "Users number" ${num_users}
		

			#Mirar si no existe ya la CA en el mismo directorio 
			if ls CA_Users
			then
				echo "The directory CA_Users already"
			else
				echo "***Creating the directory CA_Users***"
				mkdir CA_Users

				echo "***Creating new CA certificate and key databases.***"
				certutil -N -f $root_directory -d CA_Users
	
				echo "***Create an CAUsers certificate and add it to a certificate database. ***"
				certutil -S -f $root_directory -d CA_Users -n "CA_Users" -s "CN=CA_Users, O=Magentix" -t "CT,," -x -2
	
				echo "***Export CA certificate in CA_db***"
				certutil -L -f $root_directory -d CA_Users -n "CA_Users" -a -o CA_Users/rootcausers.crt
	
				echo "***Display the CA_Users content***"
				certutil -L -f $root_directory -d CA_Users
			fi
		
			i=1
			#while [ $i -le ${num_users} ]
		
			for argument in "$@"		
			do
				es_par=`expr ${i} % 2`

				if [ $es_par -eq 0 ]  #cuando sea par, que guarde la variable.
				then 
					keystore=$argument"_keystore.jks"
					truststore=$argument"_truststore.jks"
					name=$argument
				else 
					if [ $i != 1 ]
					then


						echo "***Import a ca_users certificate into User keystore***"
						keytool -import -trustcacerts -noprompt -alias CA_Users -file CA_Users/rootcausers.crt -storepass $argument -keypass $argument -keystore ${keystore}

						echo "***Import a mms certificate into User keystore***"
						keytool -import -trustcacerts -noprompt -alias MMS -file mms.crt -storepass $argument -keypass $argument -keystore ${keystore}

						echo "***Import a MagentixCA certificate into User truststore***"
						keytool -import -trustcacerts -noprompt -alias MagentixCA -file rootca.crt -storepass $argument -keypass $argument -keystore ${truststore}

						echo "***Generate a key pair with CN=User_Name and O=Magentix***"
						cn_name="CN="$name",O=Magentix"
						echo "CN_NAME " $cn_name
						keytool -genkey -alias ${name} -keyalg RSA -dname ${cn_name} -storepass $argument -keypass $argument -keystore ${keystore}
	
						echo "***Create request certificate with name mms.csr***"
						keytool -certreq -alias ${name} -storepass $argument -keypass $argument -keystore ${keystore} -file ${name}.csr

						echo "***Create a signed certificate with name mms.crt***"
						certutil -C -f $root_directory -d CA_Users/ -c "CA_Users" -a -i ${name}.csr -o ${name}.crt

						echo "***Import a mms.crt in MMS keystore***"
						keytool -import -trustcacerts -alias ${name}  -file ${name}.crt -storepass $argument -keypass $argument -keystore ${keystore}
						
						#limpiamos los certificados que no utilizaremos

						rm ${name}.crt
						rm ${name}.csr
					
						
					fi
				fi
				i=`expr $i + 1`
			done
		fi
	esac

rm ${root_directory}





