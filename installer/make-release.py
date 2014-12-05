#! python
import os
import sys
import zipfile
import shutil
import glob

def zipdir(path, zipname):
    zip = zipfile.ZipFile(zipname+'.zip', 'w')
    for root, dirs, files in os.walk(path):
        for file in files:
            zip.write(os.path.join(root, file))
    zip.close()

def which(program):
    def is_exe(fpath):
        return os.path.isfile(fpath) and os.access(fpath, os.X_OK)

    fpath, fname = os.path.split(program)
    if fpath:
        if is_exe(program):
            return program
    else:
        for path in os.environ["PATH"].split(os.pathsep):
            path = path.strip('"')
            exe_file = os.path.join(path, program)
            if is_exe(exe_file):
                return exe_file

    return None


nodoc = "-nodoc" in sys.argv
nomvn = "-nomvn" in sys.argv
nosrc = "-nosrc" in sys.argv
nozip = "-nozip" in sys.argv

#try:
#    release = sys.argv[1]
#except:
#    sys.exit("Missing parameter. Usage: " + sys.argv[0] + " releasenumber")

from xml.etree.ElementTree import ElementTree
release = ElementTree(file=".."+os.sep+"pom.xml").findtext("{http://maven.apache.org/POM/4.0.0}version")


releasedir = "magentix2-"+str(release)
shutil.rmtree(releasedir,ignore_errors=True)
for f in glob.glob(releasedir+"*.zip"):
	try:
		os.remove(f)
	except: pass
os.mkdir(releasedir)

#generate doc
if not nodoc:
    if which("doxygen")!=None:
    	os.chdir("..")
    	os.system("doxygen Doxyfile")
    	os.chdir("installer")
    elif which("/Applications/Doxygen.app/Contents/Resources/doxygen")!=None: #MacOS style
    	os.chdir("..")
    	os.system("/Applications/Doxygen.app/Contents/Resources/doxygen Doxyfile")
    	os.chdir("installer")
    else:
    	print "WARNING: Could not generate api documentation. Missing doxygen."

#generate exe
if sys.platform=="win32":
	os.system("python setup.py py2exe")
	shutil.copy("dist"+os.sep+"magentix-setup.exe", "magentix2")

orig = "magentix2"

#zip sources
if not nosrc:
    srcfile = "magentix2-"+str(release)+"-src"
    zipdir(".."+os.sep+"src", srcfile)
    os.mkdir(releasedir + os.sep + "src")
    shutil.move(srcfile + ".zip", releasedir +os.sep+ "src")

#copy files
shutil.copytree(orig+os.sep+"bin", releasedir+os.sep+"bin")
shutil.copytree("magentix2"+os.sep+"doc", releasedir+os.sep+"doc")
shutil.copytree(orig+os.sep+"lib", releasedir+os.sep+"lib")
shutil.copytree(orig+os.sep+"webapps", releasedir+os.sep+"webapps")
shutil.copytree(orig+os.sep+"configuration", releasedir+os.sep+"configuration")
shutil.copy(orig+os.sep+"Start-Magentix.bat", releasedir)
shutil.copy(orig+os.sep+"Start-Magentix.sh", releasedir)
shutil.copy(orig+os.sep+"Stop-Magentix.bat", releasedir)
shutil.copy(orig+os.sep+"Stop-Magentix.sh", releasedir)
shutil.copy(orig+os.sep+"magentix-setup.py", releasedir)
shutil.copy(orig+os.sep+"magentix-setup.exe", releasedir)
shutil.copy(".."+os.sep+"LICENSE.txt", releasedir)
shutil.copy(".."+os.sep+"RELEASE_NOTES", releasedir)

if not nodoc:
    os.mkdir(releasedir+os.sep+"doc"+os.sep+"manual")
    if sys.platform!="win32" and which("pdflatex")!=None:

    	if which("pdftk")!=None:
    		os.chdir(".."+os.sep+"doc"+os.sep+"manual")
    		os.system("make")
    		os.system("make clean")
    		os.chdir(".."+os.sep+".."+os.sep+"installer")
    	else:
    		print "WARNING: Could not compile the manual. Missing pdftk. I'll use the last version of the manual."
    else:
    	print "WARNING: Could not compile the manual. Missing pdflatex. I'll use the last version of the manual."

    shutil.copy(".."+os.sep+"doc"+os.sep+"manual"+os.sep+"Magentix2UserManual.pdf", releasedir+os.sep+"doc"+os.sep+"manual")

#attach version to Start-Magentix scripts
def line_prepender(filename,line):
    with open(filename,'r+') as f:
        content = f.read()
        f.seek(0,0)
        f.write(line.rstrip('\r\n') + '\n' + content)

line_prepender(releasedir+os.sep+"Start-Magentix.sh", 'VERSION='+release+'\n\n')
line_prepender(releasedir+os.sep+"Start-Magentix.bat", 'set VERSION='+release+'\n\n')

#Compile everything
pwd = os.getcwd()
if not nomvn:
  if which("javac") != None:
    if which("mvn") != None:
        #compile magentix...
        os.chdir("..")
        os.system("mvn compile package assembly:assembly -Dmaven.test.skip=true")
        shutil.copy("target"+os.sep+"magentix2-"+release+".jar", "installer"+os.sep+releasedir+os.sep+"lib")
        shutil.copy("target"+os.sep+"magentix2-"+release+"-jar-with-dependencies.zip", "installer"+os.sep+releasedir+os.sep+"lib")
        #compile Examples...
        os.chdir("src"+os.sep+"examples")
        os.system("mvn compile package -Dmaven.test.skip=true")
        shutil.copy("target"+os.sep+"MagentixExamples.jar", ".."+os.sep+".."+os.sep+"installer"+os.sep+releasedir+os.sep+"lib")
        #compile StartMagentix...
        os.chdir(pwd)
        os.chdir("StartMagentix")
        os.system("mvn compile package -Dmaven.test.skip=true")
        shutil.copy("target"+os.sep+"StartMagentix.jar", ".."+os.sep+releasedir+os.sep+"bin")
    else:
    	print "WARNING: Could not compile the platform! You MUST install maven2."
  else:
  	print "WARNING: javac not found! You MUST install a java JDK (>=1.7)."

os.chdir(pwd)

#copy example scripts
shutil.copytree("magentix2"+os.sep+"examples", releasedir+os.sep+"examples")

#zip release
if not nozip:
    zipdir(releasedir, releasedir)
    shutil.rmtree(releasedir,ignore_errors=True)


print "Done."

if not nozip:
    print "Your new release is magentix2-"+release+".zip"
else:
    print "Your new release is magentix2-"+release+"/"
