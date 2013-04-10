@echo off

cd ..\..\mysql-5.5.21-win32

bin\mysqladmin -u root --password=mypassword shutdown
