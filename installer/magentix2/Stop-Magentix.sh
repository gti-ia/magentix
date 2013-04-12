
#Killing agents

if [ -f /tmp/pid_magentix1 ];
then
	pid=`cat /tmp/pid_magentix1`
	kill -9 $pid 2>/dev/null
	rm /tmp/pid_magentix1
fi
if [ -f /tmp/pid_magentix2 ];
then
	pid=`cat /tmp/pid_magentix2`
	kill -9 $pid 2>/dev/null
	rm /tmp/pid_magentix2
fi
if [ -f /tmp/pid_magentix3 ];
then
	pid=`cat /tmp/pid_magentix3`
	kill -9 $pid 2>/dev/null
	rm /tmp/pid_magentix3
fi

#./bin/qpid-broker-0.20/bin/qpid.stop `ps -A | grep -m1 qpid-broker-0.20 | awk '{print $1}'`
kill -9  `ps -A | grep -m1 qpid-broker-0.20 | awk '{print $1}'`

echo "Magentix Agents stopped"
echo "Qpid server stopped"
