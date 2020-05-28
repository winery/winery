#!/bin/bash
mkdir -p .winery
dockerize -template winery.yml.tpl:.winery/winery.yml
if [ -d "${WINERY_REPOSITORY_PATH}" ] && [ "$(ls -A ${WINERY_REPOSITORY_PATH})" ]; then
	echo "Repository at ${WINERY_REPOSITORY_PATH} is already initialized!";
else
	if [ ! "x${WINERY_REPOSITORY_URL}" = "x" ]; then
		git clone ${WINERY_REPOSITORY_URL} ${WINERY_REPOSITORY_PATH};
	else
		git init $WINERY_REPOSITORY_PATH;
	fi
fi
cd ${WINERY_REPOSITORY_PATH}
export CATALINA_OPTS="-Djava.security.egd=file:/dev/./urandom -Xms512m -Xmx${WINERY_HEAP_MAX} -Duser.home=${WINERY_USER_HOME}"
if [ ! "x${WINERY_JMX_ENABLED}" = "x" ]; then
	export CATALINA_OPTS="${CATALINA_OPTS} -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.port=9010 -Dcom.sun.management.jmxremote.rmi.port=9010 -Djava.rmi.server.hostname=0.0.0.0 -Dcom.sun.management.jmxremote.ssl=false"
fi
${CATALINA_HOME}/bin/catalina.sh run
