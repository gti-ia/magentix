
#----------------------------------------------------#
#			MySql			     #
#----------------------------------------------------#
#Dar contraseña de superusuario
sudo bin/mysqladmin -u magentix password 'mecongratula'
hname=`hostname`
sudo bin/mysqladmin -h $hname -u magentix password 'mecongratula'

#Cargamos el esquema thomas
sudo bin/mysql --user=root --password=mypassword  < $MYSQL_HOME/bin/MySQL/dbThomasv2.sql

#Creamos el usuario thomas con password thomas y le damos permisos sobra la base de datos thomas
sudo bin/mysql --user=root --password=mypassword thomas < $MYSQL_HOME/bin/MySQL/mysqldata



