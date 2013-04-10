
#Comprobar primero si existen los repositorios necesarios para añadir las librerias requeridas

#hacemos una copia de seguridad
sudo cp /etc/apt/sources.list /etc/apt/sources.list.orig

#-------deb http://extras.ubuntu.com/ubuntu natty main--------
count=`grep "deb http://extras.ubuntu.com/ubuntu natty main" /etc/apt/sources.list -c`

if [ $count = 0 ]
then
sudo echo "deb http://extras.ubuntu.com/ubuntu natty main" >> /etc/apt/sources.list
fi

#-------deb-src http://extras.ubuntu.com/ubuntu natty main--------
count=`grep "deb-src http://extras.ubuntu.com/ubuntu natty main" /etc/apt/sources.list -c`

if [ $count = 0 ]
then
sudo echo "deb-src http://extras.ubuntu.com/ubuntu natty main" >> /etc/apt/sources.list
fi

#-------deb http://ubuntu.mirror.cambrium.nl/ubuntu/ lucid main-----
count=`grep "deb http://ubuntu.mirror.cambrium.nl/ubuntu/ lucid main" /etc/apt/sources.list -c`

if [ $count = 0 ]
then
sudo echo "deb http://ubuntu.mirror.cambrium.nl/ubuntu/ lucid main" >> /etc/apt/sources.list
fi

#-------deb-src http://ubuntu.mirror.cambrium.nl/ubuntu/ lucid main-----
count=`grep "deb-src http://ubuntu.mirror.cambrium.nl/ubuntu/ lucid main" /etc/apt/sources.list -c`

if [ $count = 0 ]
then
sudo echo "deb-src http://ubuntu.mirror.cambrium.nl/ubuntu/ lucid main" >> /etc/apt/sources.list
fi


#actualizo

sudo apt-get update



cd $QPID_PATH

sudo dpkg -i qpid-0.7.deb

sudo apt-get install -f -y










