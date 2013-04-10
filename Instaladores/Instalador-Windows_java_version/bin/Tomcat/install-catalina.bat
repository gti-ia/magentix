@echo off

cd %THOMAS_PATH%

move "%THOMAS_PATH%\*" "%CATALINA_HOME%\webapps\"
mkdir "%CATALINA_HOME%\webapps\ontologies\"
copy "%THOMAS_PATH%\ontologies\*" "%CATALINA_HOME%\webapps\ontologies\"


echo "||			Magentix 2 successfully installed                      	    ||"


