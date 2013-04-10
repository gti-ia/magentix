




if [ "$QPID_PATH" != "" ]
then
	sudo $QPID_PATH/qpid-broker-0.14/bin/qpid-server &
else
	sudo ../../qpid-broker-0.14/bin/qpid-server &
fi
