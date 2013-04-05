
@echo off

taskkill /f /im java.exe /fi "WINDOWTITLE eq Launching*"
echo "Agents Magentix stopped"
