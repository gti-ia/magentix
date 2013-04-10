




export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/opt/qpid/lib 

cd /opt/qpid/sbin

if [ "$QPID_PATH" != "" ]
then
	sudo ./qpidd --auth no 2>> $QPID_PATH/bin/Qpid/Qpid.out &
else
	sudo ./qpidd --auth no &
fi

