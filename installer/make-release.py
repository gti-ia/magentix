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
    import os
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
if which("doxygen")!=None:
	os.chdir("..")
	os.system("doxygen Doxyfile")
	os.chdir("installer")
else:
	print "WARNING: Could not generate api documentation. Missing doxygen."

#zip sources
srcfile = "magentix2-"+str(release)+"-src"
zipdir("src", srcfile)
os.mkdir(releasedir + os.sep + "src")
shutil.move(srcfile + ".zip", releasedir +os.sep+ "src")

#generate exe
if sys.platform=="win32" and which("python")!=None:
	os.system("python setup.py py2exe")
	shutil.copy("dist"+os.sep+"magentix-setup.exe", "magentix2")

if which("svn") != None:
	os.system("svn export magentix2 magentix2-export")
	orig = "magentix2-export"
else:
	orig = "magentix2"
	print "WARNING: could not export svn!"
#copy files
shutil.copytree(orig+os.sep+"bin", releasedir+os.sep+"bin")
shutil.copytree(orig+os.sep+"doc", releasedir+os.sep+"doc")
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

os.mkdir(releasedir+os.sep+"doc"+os.sep+"manual")
if sys.platform!="win32" and which("pdflatex")!=None:
	os.chdir(".."+os.sep+"doc"+os.sep+"manual")
	os.system("make")
	os.system("make clean")
	os.chdir(".."+os.sep+".."+os.sep+"installer")
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

#zip release
zipdir(releasedir, releasedir)
#clean
#shutil.rmtree(releasedir,ignore_errors=True)
shutil.rmtree("magentix2-export",ignore_errors=True)

print "Done."
print "Your new release is magentix2-"+release+".zip"
