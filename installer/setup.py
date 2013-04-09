#! python

from distutils.core import setup
import py2exe
import sys
import os

sys.path.append("magentix2"+os.sep+"bin")
  
setup(
	name="Magentix2",
	console=['magentix-setup.py'],
	options = {'py2exe': {'bundle_files': 1, 'compressed': True}},
	zipfile = None,
	package_dir={"magentix2":"magentix2"},
	packages=['mysql','mysql.connector','mysql.connector.locales']
	)