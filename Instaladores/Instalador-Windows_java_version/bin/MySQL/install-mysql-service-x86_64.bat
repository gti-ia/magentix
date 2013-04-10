@echo off
cd %MYSQL_HOME%
start "MySQL" bin\mysqld --console
> null ping -n 5 localhost 

bin\mysql -u root < "%MAGENTIX_HOME%\bin\MySQL\pass.sql"

bin\mysql --user=root --password=mypassword  < "%MAGENTIX_HOME%\bin\MySQL\dbThomasv2.sql"

bin\mysql --user=root --password=mypassword thomas < "%MAGENTIX_HOME%\bin\MySQL\mysqldata"

bin\mysqladmin -u root --password=mypassword shutdown



