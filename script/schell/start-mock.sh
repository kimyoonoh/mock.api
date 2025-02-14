#!/usr/bin/env bash

#if [ $# -le 4 ]; then
#	echo "Not enough parameters!!"
#	echo "start-mock.sh [server-id] [application-id] [application-port] [auto run mode] [Schedule Sleep mode] [profile]"
#	echo "ex) ./start-mock.sh BS001 Ldc001 8080 true false dev"
	
#	exit -1
#fi

Today=`date +"%G%m%d%H%M%S"`

if [ "$JAVA_HOME" = "" ]; then
	export JAVA_HOME=/usr/lib/jvm/jre-11-openjdk-11.0.20.0.8-1.el8_6.x86_64
fi

MOCK_ROOT=/aidt
BIN_PATH=$MOCK_ROOT/app/mock
LOG_PATH=$MOCK_ROOT/log/mock

applcationPort=8080
profileId=dev

jarFile=$BIN_PATH/aidt-adv-lms-mock-server-0.0.1.jar
jvmOptions="-Xms1024m -Xmx4096m"
appOptions="-Dfile.encoding=UTF-8 -Ddatasource.bind=runtime -Ddebug.mode=true -Dserver.port=$applcationPort -Dclient=false -Dexcel.file.path=/aidt/app/mock/LMS-MOCKAPI.xlsx -Dspring.profiles.active=$profileId"

commandLine="$JAVA_HOME/bin/java $jvmOptions $appOptions -jar $jarFile"

echo ==========================================================================================
echo JAVA_HOME           : $JAVA_HOME
echo LDC Root path       : $LDC_ROOT
echo APP BIN Path        : $BIN_PATH
echo APP Jar File        : $jarFile
echo JVM Options         : $jvmOptions
echo App Command Options : $appOptions
echo Log file name       : $LOG_FILE
echo Command Line        : $commandLine
echo ==========================================================================================

nohup $commandLine &

echo "[START] Mockup API Application ${serverId}-${applicationId}"