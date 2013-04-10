all:

	pdflatex Magentix2UserManual.tex
	pdflatex Magentix2UserManual.tex
	bibtex Magentix2UserManual
	pdflatex Magentix2UserManual.tex
	gs -q -sPAPERSIZE=letter -dNOPAUSE -dBATCH -sDEVICE=pdfwrite -sOutputFile=output.pdf Cover/Cover.pdf Magentix2UserManual.pdf
	mv output.pdf Magentix2UserManual.pdf

clean:
	rm -f *.mtc* *.bbl *.maf *.aux *.blg *.toc *.tps *.tcp *.out *.maf *.log *.lot *.lof
	for i in `find . -name *.aux`; do rm -f $$i; done
	for i in `find . -name *.log`; do rm -f $$i; done
