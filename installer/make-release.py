#! python
import os
import sys
import zipfile

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

#generate doc
os.chdir("..")
#os.system("doxygen Doxyfile")
srcfile = "magentix2-"+str(release)+"-src"
zipdir("src", srcfile)
os.chdir("installer")

releasedir = "magentix2-"+str(release)

os.system("rm -rf " + releasedir+"*")

os.mkdir(releasedir)
os.system("cp -r magentix2/bin "+ releasedir)
os.system("cp -r magentix2/doc "+ releasedir)
os.system("cp -r magentix2/lib "+ releasedir)
os.system("cp -r magentix2/webapps "+ releasedir)
os.system("cp -r magentix2/magentix-setup.py "+ releasedir)
os.mkdir(releasedir + os.sep + "src")
os.system("mv ../"+srcfile + ".zip " +releasedir + "/src")

zipdir(releasedir, releasedir)

os.system("rm -rf " + releasedir)
