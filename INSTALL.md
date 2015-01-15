# Installation Instructions

## Prerequisites
These prerequisites are *only* for installing the platform. They are not needed for running  agents.

### Linux
* mysql-server
* tomcat7
* tomcat7-admin

### Windows
* [mysql](https://dev.mysql.com/downloads/installer/)
* [tomcat7](https://tomcat.apache.org/download-70.cgi)

## Magentix2 Platform installation
If you are running your own magentix2 platform you need to follow these steps:


### Linux
1. Download the magentix2 zip file from the [magentix downloads page](http://www.gti-ia.upv.es/sma/tools/magentix2/downloads.php).
2. Unzip it.
3. Run magentix-setup.py to configure the platform. You will be asked for a mysql root password and a tomcat user.
4. Run Start-Magentix.sh to start the platform.
5. To stop the platform run Stop-Magentix.sh

### Windows
1. Download the magentix2 zip file from the [magentix home page](http://www.gti-ia.upv.es/sma/tools/magentix2/downloads.php).
2. Unzip it.
3. Run magentix-setup.exe to configure the platform. You will be asked for a mysql root password and a tomcat user.
4. Run Start-Magentix.bat to start the platform.
5. To stop the platform run Stop-Magentix.bat

## Magentix2 Agent Library installation

The magentix2 agent library **jar** file is available in the following sources:

* Included in the zip you downloaded there is a `lib/` folder where you can find `magentix2-2.1.0.jar` and  `magentix2-2.1.0-jar-with-dependencies.zip`.
* You can download both files (`magentix2-2.1.0.jar` and  `magentix2-2.1.0-jar-with-dependencies.zip`) from the [magentix downloads page](http://www.gti-ia.upv.es/sma/tools/magentix2/downloads.php).
* You can use **maven** and include the magentix dependency including in your `pom.xml` file the following code:

```xml
<dependency>
    <groupId>es.upv.dsic.gti-ia</groupId>
        <artifactId>magentix2</artifactId>
    <version>2.1.0</version>
</dependency>

<repositories>
        <repository>
                <id>Magentix2</id>
                <url>http://gti-ia.dsic.upv.es:8081/artifactory/remote-repos/</url>
        </repository>
</repositories>
```



