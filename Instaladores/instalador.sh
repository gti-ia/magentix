

cd Instalador-86_64
rm Magentix2Desktop-86_64.jar
/home/joabelfa/IzPack/bin/compile install.xml -o Magentix2Desktop-x64.jar

cd ..
cd Instalador-86_64_java_version
rm Magentix2Desktop-86_64_java_version.jar
/home/joabelfa/IzPack/bin/compile install.xml -o Magentix2Desktop-x64_java_version.jar

cd ..
cd Instalador-i386
rm Magentix2Desktop-i386.jar
/home/joabelfa/IzPack/bin/compile install.xml -o Magentix2Desktop-i386.jar

cd ..
cd  Instalador-i386_java_version
rm Magentix2Desktop-i386_java_version.jar
/home/joabelfa/IzPack/bin/compile install.xml -o Magentix2Desktop-i386_java_version.jar

cd ..
cd Instalador-Windows_java_version
rm Magentix2Desktop-windows_version.jar
/home/joabelfa/IzPack/bin/compile install_linux.xml -o Magentix2Desktop-windows_version.jar


