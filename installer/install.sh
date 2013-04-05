#!/bin/bash

if [ "$(id -u)" != "0" ]; then
   echo "This script must be run as root" 1>&2
   exit 1
fi

DBPASSWD=""

function check_dependencies
{
		if [ ! -f `which mysql` ] ; then
    			echo "You need to install mysql-client in order to install Magentix2"
			exit 1; 
		fi

		if [ ! -f `which mysqladmin` ] ; then
    			echo "You need to install mysql-server in order to install Magentix2"
			exit 1; 
		fi

		#Ask for the DB admin password
		read -s -p "Password for mysql root user: " DBPASSWD
		echo
		mysqladmin -u root --password=$DBPASSWD status >/dev/null 2>/dev/null
		if [ ! $? -eq 0 ] ; then
			echo "You need to run mysql-server in order to install Magentix2";
			exit 1;
		fi

		#Try to locate $CATALINA_BASE
		if [ -z $CATALINA_BASE ]; then
			CATALINA_BASE="/var/lib/tomcat7";
			echo "Tomcat path not setted. Trying $CATALINA_BASE"
		fi;

		if [ ! -e $CATALINA_BASE/webapps ]; then
			echo "Could not find Tomcat7 installation. Please make sure it is properly installed or set the CATALINA_BASE variable to its path.";
			exit 1;
		fi
}

function setup_sql
{
		echo "Installing Magentix Database"

		#Load the magentix schema 
		mysql --user=root  --password=$DBPASSWD < bin/sql/db-schema.sql;

		#Create the thomas user and grant permissions for the thomas DB
		mysql --user=root --password=$DBPASSWD < bin/sql/grants.sql;
}

function install_webapps
{
		cp -v webapps/sfservices.war $CATALINA_BASE/webapps;
		cp -v webapps/testSFservices.war $CATALINA_BASE/webapps;
		mkdir $CATALINA_BASE/webapps/ontologies;
		cp -v webapps/ontologies/provider.owl $CATALINA_BASE/webapps/ontologies;
		cp -v webapps/omsservices.war $CATALINA_BASE/webapps;
}

check_dependencies
setup_sql
install_webapps
