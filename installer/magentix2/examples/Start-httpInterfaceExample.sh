LIBS=$LIBS:../lib/magentix2-2.0.3-jar-with-dependencies.zip
LIBS=$LIBS:../lib/MagentixExamples.jar
#libs for security
LIBS=$LIBS:../lib/security/bcprov-jdk15-140.jar
LIBS=$LIBS:../lib/security/rampart-core-1.4.jar
LIBS=$LIBS:../lib/security/rampart-policy-1.4.jar

java -cp "$LIBS" httpInterfaceExample.Main


