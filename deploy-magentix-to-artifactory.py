#! python

import sys
import os

try:
    release = sys.argv[1]
except:
    sys.exit("Missing parameter. Usage: " + sys.argv[0] + " releasenumber")

os.system("mvn -e deploy:deploy-file -DgroupId=es.upv.dsic.gti-ia -DartifactId=magentix2 -Dversion="+release+" -Dpackaging=jar -Dfile=target/magentix2-"+release+".jar -DrepositoryId=urano -Durl=http://urano.dsic.upv.es:8081/artifactory/gti-repo -DgeneratePom=true")
