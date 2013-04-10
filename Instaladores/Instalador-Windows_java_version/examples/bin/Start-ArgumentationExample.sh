



cd ..

LIBS=$LIBS:../lib/magentix2-2.01-jar-with-dependencies.zip
LIBS=$LIBS:MagentixExamples.jar
#libs for security
LIBS=$LIBS:../lib/security/bcprov-jdk15-140.jar
LIBS=$LIBS:../lib/security/rampart-core-1.4.jar
LIBS=$LIBS:../lib/security/rampart-policy-1.4.jar

java -cp "$LIBS" Argumentation_Example.TestArgCAgent



