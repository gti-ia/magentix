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
shutil.rmtree(releasedir,ignore_errors=True)
for f in glob.glob(releasedir+"*.zip"):
	try:
		os.remove(f)
	except: pass
os.mkdir(releasedir)

#generate doc
os.chdir("..")
#os.system("doxygen Doxyfile")
os.chdir("installer")

#zip sources
srcfile = "magentix2-"+str(release)+"-src"
zipdir("src", srcfile)
os.mkdir(releasedir + os.sep + "src")
shutil.move(srcfile + ".zip", releasedir +os.sep+ "src")

#generate exe
if sys.platform=="win32":
	os.system("python setup.py py2exe")
	shutil.copy("dist"+os.sep+"magentix-setup.exe", "magentix2")

#copy files
shutil.copytree("magentix2"+os.sep+"bin", releasedir+os.sep+"bin")
shutil.copytree("magentix2"+os.sep+"doc", releasedir+os.sep+"doc")
shutil.copytree("magentix2"+os.sep+"lib", releasedir+os.sep+"lib")
shutil.copytree("magentix2"+os.sep+"webapps", releasedir+os.sep+"webapps")
shutil.copytree("magentix2"+os.sep+"configuration", releasedir+os.sep+"configuration")
shutil.copy("magentix2"+os.sep+"Start-Magentix.bat", releasedir)
shutil.copy("magentix2"+os.sep+"Start-Magentix.sh", releasedir)
shutil.copy("magentix2"+os.sep+"Stop-Magentix.bat", releasedir)
shutil.copy("magentix2"+os.sep+"Stop-Magentix.sh", releasedir)
shutil.copy("magentix2"+os.sep+"magentix-setup.py", releasedir)
shutil.copy("magentix2"+os.sep+"magentix-setup.exe", releasedir)

#attach version to Start-Magentix scripts
# read the current contents of the file
f = open(releasedir+os.sep+"Start-Magentix.bat")
text = f.read()
f.close()
# open the file again for writing
f = open(releasedir+os.sep+"Start-Magentix.bat", 'w')
f.write('set VERSION='+release+'\n\n')
# write the original contents
f.write(text)
f.close()
# read the current contents of the file
f = open(releasedir+os.sep+"Start-Magentix.sh")
text = f.read()
f.close()
# open the file again for writing
f = open(releasedir+os.sep+"Start-Magentix.sh", 'w')
f.write('VERSION='+release+'\n\n')
# write the original contents
f.write(text)
f.close()


#zip release
#zipdir(releasedir, releasedir)
#clean
#shutil.rmtree(releasedir,ignore_errors=True)
