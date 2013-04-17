#!/bin/bash
# Script para cambiar el nombre de la máquina que aparece en los documentos de los servicios
# $1 - directorio donde están los documentos owl-s
# $2 - cadena_actual (nombre de la máquina anterior )
# $3 - cadena_reemplazar (nombre de la nueva máquina)

cd $1;
templates=$(ls *.owl);

for tpl in $templates; 
do

cat $tpl | sed "s@$2@$3@g" > $tpl.prueba;
mv $tpl.prueba $tpl;

done
