if [ "$MAGENTIX_HOME" != "" ]
then
	cd $MAGENTIX_HOME
fi

LIBS=$LIBS:../lib/magentix2-2.03-jar-with-dependencies.zip
LIBS=$LIBS:StartMagentix.jar
LIBS=$LIBS:../lib/security/bcprov-jdk15-140.jar
LIBS=$LIBS:../lib/security/rampart-core-1.4.jar
LIBS=$LIBS:../lib/security/rampart-policy-1.4.jar

sleep 15
java -cp "$LIBS" es.upv.dsic.gti_ia.StartMagentixDesktop.Run  &

PIDMAGENTIX=`ps x | grep "java -cp "$LIBS" es.upv.dsic.gti_ia.StartMagentixDesktop.Run" | awk '{if(NR==1)print $1}'`

echo $PIDMAGENTIX > /tmp/pid_magentix1


java -cp "$LIBS" es.upv.dsic.gti_ia.StartMagentixDesktop.RunHttpInterface  &

PIDMAGENTIX=`ps x | grep "java -cp "$LIBS" es.upv.dsic.gti_ia.StartMagentixDesktop.RunHttpInterface" | awk '{if(NR==1)print $1}'`

echo $PIDMAGENTIX > /tmp/pid_magentix2

java -cp "$LIBS" es.upv.dsic.gti_ia.StartMagentixDesktop.RunTM  &

PIDMAGENTIX=`ps x | grep "java -cp "$LIBS" es.upv.dsic.gti_ia.StartMagentixDesktop.RunTM" | awk '{if(NR==1)print $1}'`

echo $PIDMAGENTIX > /tmp/pid_magentix3
