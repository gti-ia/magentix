#!/bin/bash


sudo groupadd mysql
sudo useradd -r -g mysql mysql

cd $MYSQL_HOME


sudo ln -s mysql-5.1.53-linux-i686-glibc23 mysql 

cd mysql

#############Edit mysqlaccess##############
variable=$(echo $MYSQL_HOME | sed  's/\//\\\//g')
variable="s/\/usr\/local/"$variable"/g"


sudo sed $variable bin/mysqlaccess > bin/mysqlaccess_aux
sudo mv bin/mysqlaccess_aux bin/mysqlaccess
###########################################

sudo chown -R mysql .
sudo chgrp -R mysql .

#Install db
sudo scripts/mysql_install_db --user=mysql


sudo chown -R root .
sudo chown -R mysql data




#Start the MySQL daemon
sudo bin/mysqld_safe --user=mysql &


#Importante para que se espere a que cree la base de datos.
sleep 5


#Dar contraseña de superusuario
sudo bin/mysqladmin -u root password 'mypassword'
hname=`hostname`
sudo bin/mysqladmin -h $hname -u root password 'mypassword'

#Cargamos el schema thomas.sql, creara el schema thomas
sudo bin/mysql --user=root --password=mypassword  < $MYSQL_HOME/bin/MySQL/dbThomasv2.sql

#Creamos el usuario thomas thomas y le damos permisos sobra la base de datos thomas
sudo bin/mysql --user=root --password=mypassword thomas < $MYSQL_HOME/bin/MySQL/mysqldata

sudo bin/mysqladmin --user=root --password=mypassword shutdown



