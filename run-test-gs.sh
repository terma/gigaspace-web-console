#!/bin/sh

echo "Clear logs..."
rm ./gs10.log
#rm ./gs95.log
#rm ./gs97.log

echo "Kill running spaces..."
pkill -f GSA
sleep 5

#echo "Starting GS-9.5..."
#export LOOKUPLOCATORS=127.0.0.1:4300
#export EXT_JAVA_OPTIONS="-Dcom.gs.multicast.discoveryPort=4300"
#nohup /Users/terma/Downloads/gigaspaces-xap-premium-9.5.0-m7/bin/gs-agent.sh > ./gs95.log &
#sleep 30

#echo "Create gs95 space on GS-9.5..."
#/Users/terma/Downloads/gigaspaces-xap-premium-9.5.0-m7/bin/gs.sh deploy-space gs95 || exit $?

#echo "Create gs95-1 space on GS-9.5..."
#/Users/terma/Downloads/gigaspaces-xap-premium-9.5.0-m7/bin/gs.sh deploy-space gs95-1 || exit $?

#/Users/terma/Downloads/gigaspaces-xap-premium-9.7.0-m6/bin/gs-agent.sh > ./gs97.log || exit $?

echo "Starting GS-10..."
export LOOKUPLOCATORS=127.0.0.1:4700
export EXT_JAVA_OPTIONS="-Dcom.gs.multicast.discoveryPort=4700"
nohup /Users/terma/Downloads/gigaspaces-xap-premium-10.0.1-ga/bin/gs-agent.sh > ./gs10.log &
sleep 30

echo "Create gs10 space on GS-10..."
/Users/terma/Downloads/gigaspaces-xap-premium-10.0.1-ga/bin/gs.sh deploy-space gs10 || exit $?

echo "Welcome to testing!"