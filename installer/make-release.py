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


try:
    release = sys.argv[1]
except:
    sys.exit("Missing parameter. Usage: " + sys.argv[0] + " releasenumber")


releasedir = "magentix2-"+str(release)
#os.system("rm -rf " + releasedir+"*")
shutil.rmtree(releasedir,ignore_errors=True)
for f in glob.glob(releasedir+"*.zip"):
	try:
		os.remove(f)
	except: pass
os.mkdir(releasedir)

#generate doc
if sys.platform!="win32":
	os.chdir("..")
	os.system("doxygen Doxyfile")
	os.chdir("installer")

#zip sources
srcfile = "magentix2-"+str(release)+"-src"
zipdir("src", srcfile)
os.mkdir(releasedir + os.sep + "src")
#os.system("mv ../"+srcfile + ".zip " +releasedir + "/src")
shutil.move(srcfile + ".zip", releasedir +os.sep+ "src")

#generate exe
if sys.platform=="win32":
	#os.system("python.exe setup.py py2exe")
	import setup
	#os.system("mv dist"+os.sep+"magentix-setup.exe magentix2")
	shutil.move("dist"+os.sep+"magentix-setup.exe", "magentix2")

#copy files
#os.system("cp -r magentix2/bin "+ releasedir)
#os.system("cp -r magentix2/doc "+ releasedir)
#os.system("cp -r magentix2/lib "+ releasedir)
#os.system("cp -r magentix2/webapps "+ releasedir)
shutil.copytree("magentix"+os.sep+"doc", releasedir)
shutil.copytree("magentix"+os.sep+"lib", releasedir)
shutil.copytree("magentix"+os.sep+"webapps", releasedir)
#os.system("cp -r magentix2/magentix-setup.py "+ releasedir)
shutil.copy("magentix2"+os.sep+"magentix-setup.py", releasedir)

#zip release
zipdir(releasedir, releasedir)
#clean
#os.system("rm -rf " + releasedir)
shutil.rmtree(releasedir,ignore_errors=True)
