
@echo off

taskkill /f /im java.exe /fi "WINDOWTITLE eq Launching*"

taskkill /fi "WINDOWTITLE eq Qpid*"

@echo on
@echo "Magentix Agents stopped"
@echo "Qpid server stopped"