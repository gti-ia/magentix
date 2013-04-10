
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/opt/qpid/lib 

cd /opt/qpid/sbin


sudo ./qpidd --auth yes --ssl-cert-db $HOME/Magentix2/bin/security/broker_db/ --ssl-cert-password-file $HOME/Magentix2/bin/security/pfile --ssl-cert-name broker --ssl-require-client-authentication --acl-file $HOME/Magentix2/bin/security/broker.acl &




PIDMAGENTIX=`ps -A | grep "qpidd" | awk '{if(NR==1)print $1}'`

echo $PIDMAGENTIX > /tmp/pid_magentixQpid
