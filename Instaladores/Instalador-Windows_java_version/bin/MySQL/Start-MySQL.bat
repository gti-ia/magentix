@echo off
echo "Start MySQL"

cd ..\..\mysql-5.5.21-win32

start "MySQL" bin\mysqld --console
