
#Killing agents

pid=`cat /tmp/pid_magentix1`
kill -9 $pid 2>/dev/null
rm /tmp/pid_magentix1

pid=`cat /tmp/pid_magentix2`
kill -9 $pid 2>/dev/null
rm /tmp/pid_magentix2

pid=`cat /tmp/pid_magentix3`
kill -9 $pid 2>/dev/null
rm /tmp/pid_magentix3

./qpid-broker-0.20/bin/qpid.stop

echo "Magentix stopped"
