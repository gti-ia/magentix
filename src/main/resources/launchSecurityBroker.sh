#!/bin/bash

sudo ./qpidd --auth yes --ssl-cert-db ./security/broker_db/ --ssl-cert-name broker --ssl-require-client-authentication  --ssl-cert-password-file /home/joabelfa/Escritorio/pruebas_seguridad/qpidd/pfile_broker --acl-file ./security/broker.acl
