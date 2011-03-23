
usage()
{

	echo "\n\n\033[1mUsage:\033[0m sh Import-ThomasDB.sh MYSQL_ROOT_PASSWORD\n\n
\033[1mDescription:\033[0m This command imports thomas schema and create user thomas.\n
\033[1mPASSWORD_ROOT (Required):\033[0m Specify a password for mysql user root.\n		
"
}

case $# in
	1)
		#Cargamos el schema MMS.sql, creara el schema security
		sudo ../../mysql/bin/mysql --user=root --password=$1  < MMS.sql

		#Creamos el usuario mms mms y le damos permisos sobre la base de datos security
		sudo ../../mysql/bin/mysql --user=root --password=$1 < mysqldata;;

	*)usage
	  exit 0;;

esac

