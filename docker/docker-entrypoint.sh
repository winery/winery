#!/bin/bash

# detect if eclipse che environment is present
if [ ! -z "${CHE_API}" ]; then
    cheBaseUrl=$(echo "$CHE_API" | cut -d"/" -f3)
    cheUrl=${cheBaseUrl}/dashboard/\#/ide/${CHE_WORKSPACE_NAMESPACE}/${CHE_WORKSPACE_NAME}
    export CHE_URL=$cheUrl
fi

mkdir -p .winery
dockerize -template winery.yml.tpl:.winery/winery.yml
if [ -d "${WINERY_REPOSITORY_PATH}" ] && [ "$(ls -A ${WINERY_REPOSITORY_PATH})" ]; then
	echo "Repository at ${WINERY_REPOSITORY_PATH} is already initialized!";
else
	if [ ! "x${WINERY_REPOSITORY_URL}" = "x" ]; then
		git clone ${WINERY_REPOSITORY_URL} ${WINERY_REPOSITORY_PATH};
	else
		git init ${WINERY_REPOSITORY_PATH};
	fi
fi
if [ ! "x${WINERY_DEPENDENT_REPOSITORIES}" = "x" ]; then
  FILE=${WINERY_REPOSITORY_PATH}/repositories.json
  if [ -f "${FILE}" ]; then
    mv ${WINERY_REPOSITORY_PATH} workspace   
    mkdir -p ${WINERY_REPOSITORY_PATH}
    mv workspace ${WINERY_REPOSITORY_PATH}      
  fi
  echo ${WINERY_DEPENDENT_REPOSITORIES} > ${FILE}
fi
cd ${WINERY_REPOSITORY_PATH}
export CATALINA_OPTS="-Djava.security.egd=file:/dev/./urandom -Xms512m -Xmx${WINERY_HEAP_MAX} -Duser.home=${WINERY_USER_HOME}"
if [ ! "x${WINERY_JMX_ENABLED}" = "x" ]; then
	export CATALINA_OPTS="${CATALINA_OPTS} -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.port=9010 -Dcom.sun.management.jmxremote.rmi.port=9010 -Djava.rmi.server.hostname=0.0.0.0 -Dcom.sun.management.jmxremote.ssl=false"
fi
${CATALINA_HOME}/bin/catalina.sh run
