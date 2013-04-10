
#Killing agents

pid=`sudo cat /tmp/pid_magentix1`

kill -9 $pid

pid=`sudo cat /tmp/pid_magentix2`

kill -9 $pid

pid=`sudo cat /tmp/pid_magentix3`

kill -9 $pid

echo "Agents Magentix stopped"
