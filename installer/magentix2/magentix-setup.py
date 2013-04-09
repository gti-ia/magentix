#! python

import os
import sys
import getpass
import urllib2
import base64

sys.path.append("bin")

import mysql

#Try to connect to MySQL
from mysql.connector import connection

#if os.name == "posix":
#	if not os.geteuid()==0:
#		sys.exit("\nThis script must be run as root\n")

dbpasswd = None

def exit(msg):
	print msg
	raw_input("Press [ENTER] to exit.")
	sys.exit()

def get_mysql_password():
	dbpasswd = getpass.getpass(prompt="Password for mysql root user: ")
	return dbpasswd


def check_dependencies():

	dbpasswd = get_mysql_password()

	try:
		dbh = connection.MySQLConnection(host="localhost", user="root", password=dbpasswd)
		dbh.close()
	except:
		exit("""Could not connect to MySQL server at localhost\n
You need to have mysql-server installed and running.""")


	tomcat_user = raw_input("Username for Tomcat admin [default=tomcat]:")
	if tomcat_user=="": tomcat_user="tomcat"
	tomcat_passwd = getpass.getpass(prompt="Password for Tomcat admin user ("+tomcat_user+"): ")

	try:
		opener = urllib2.build_opener(urllib2.HTTPHandler)
		request = urllib2.Request('http://localhost:8080/manager/text/status')

		base64string =  base64.encodestring('%s:%s' % (tomcat_user, tomcat_passwd))[:-1]
		authheader =  "Basic %s" % base64string
		request.add_header('Content-Type', 'application/octet-stream')
		request.add_header("Authorization", authheader)

		request.get_method = lambda: 'PUT'
		url = opener.open(request)
	except urllib2.HTTPError, e:
		exit("Could not auth to Tomcat server: "+str(e.reason)+ " ("+str(e.code)+")"
			+ "\nTIP: Has the tomcat user the role manager-script assigned in tomcat-users.xml?\n"
			+ '''Eg: <role rolename="manager-script"/>
    <user username="tomcat" password="tomcat" roles="manager-script"/>'''
			)
	
	except urllib2.URLError, e:
		exit("Could not connect to Tomcat server: "+str(e.reason) + "\n"+ """You need to have tomcat7 installed and running.""")

	return dbpasswd, tomcat_user, tomcat_passwd

def create_msql_schema(dbpasswd):

	try:
		dbh = connection.MySQLConnection(host="localhost", user="root", password=dbpasswd)
		cursor = dbh.cursor()
		
		with open("bin"+os.sep+"sql"+os.sep+"db-schema.sql") as f:
			sql_text = f.read()
			sql_stmts = sql_text.split(";")
			for stmt in sql_stmts:
				if stmt != "":
					cursor.execute(stmt.strip())
		with open("bin"+os.sep+"sql"+os.sep+"grants.sql") as f:
			sql_text = f.read()
			sql_stmts = sql_text.split(";")
			for stmt in sql_stmts:
				if stmt != "":
					cursor.execute(stmt.strip())


		dbh.commit()
		cursor.close()
		dbh.close()

	except Exception, e:
		exit("Could not connect to MySQL server at localhost" + str(e))


def install_webapps(tomcat_user, tomcat_passwd):
	username=tomcat_user
	password=tomcat_passwd

	base64string =  base64.encodestring('%s:%s' % (username, password))[:-1]
	authheader =  "Basic %s" % base64string

	deploy_war("omsservices", authheader)
	deploy_war("sfservices",authheader)
	deploy_war("testSFservices",authheader)
	deploy_war("ontologies",authheader)

def deploy_war(war_filename, authheader):

	war_file_contents = open("webapps" + os.sep + war_filename+".war",'rb').read()

	opener = urllib2.build_opener(urllib2.HTTPHandler)
	request = urllib2.Request('http://localhost:8080/manager/text/deploy?path=/'+war_filename, data=war_file_contents)

	request.add_header('Content-Type', 'application/octet-stream')
	request.add_header("Authorization", authheader)

	request.get_method = lambda: 'PUT'
	url = opener.open(request)


dbpasswd, tomcat_user, tomcat_passwd = check_dependencies()
print "Installing Magentix Database..."
create_msql_schema(dbpasswd)
print "Deploying THOMAS webapps..."
install_webapps(tomcat_user, tomcat_passwd)
print "Magentix succesfully installed."

