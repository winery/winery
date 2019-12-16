FROM maven:3-jdk-8 as builder

RUN rm /dev/random && ln -s /dev/urandom /dev/random \
    && apt-get update -qq && apt-get install -qqy \
        unzip \
        git \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/* \
    && echo '{ "allow_root": true }' > /root/.bowerrc

COPY . /tmp/winery
WORKDIR /tmp/winery
RUN mvn package -DskipTests


FROM tomcat:8.5.31
LABEL maintainer = "Oliver Kopp <kopp.dev@gmail.com>, Michael Wurster <miwurster@gmail.com>, Lukas Harzenetter <lharzenetter@gmx.de>"

ARG DOCKERIZE_VERSION=v0.3.0

ENV WINERY_REPOSITORY_URL ""
ENV WINERY_HEAP_MAX 2048m
ENV WINERY_JMX_ENABLED ""
ENV CONTAINER_HOSTNAME localhost
ENV CONTAINER_PORT 1337
ENV WORKFLOWMODELER_HOSTNAME localhost
ENV WORKFLOWMODELER_PORT 8080
ENV TOPOLOGYMODELER_HOSTNAME localhost
ENV TOPOLOGYMODELER_PORT 8080
ENV WINERY_REPOSITORY_PATH "/var/opentosca/repository"
ENV WINERY_HOSTNAME localhost
ENV WINERY_PORT 8080

RUN rm /dev/random && ln -s /dev/urandom /dev/random \
    && curl -s https://packagecloud.io/install/repositories/github/git-lfs/script.deb.sh | bash \
    && apt-get update -qq && apt-get install -qqy \
        git \
        git-lfs \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/* \
    && rm -rf ${CATALINA_HOME}/webapps/* \
    && sed -ie "s/securerandom.source=file:\/dev\/random/securerandom.source=file:\/dev\/.\/urandom/g" /usr/lib/jvm/java-8-openjdk-amd64/jre/lib/security/java.security \
    && wget https://github.com/jwilder/dockerize/releases/download/$DOCKERIZE_VERSION/dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && tar -C /usr/local/bin -xzvf dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && rm dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz

COPY --from=builder /tmp/winery/org.eclipse.winery.repository.rest/target/winery.war ${CATALINA_HOME}/webapps/winery.war
COPY --from=builder /tmp/winery/org.eclipse.winery.frontends/target/tosca-management.war ${CATALINA_HOME}/webapps/ROOT.war
COPY --from=builder /tmp/winery/org.eclipse.winery.frontends/target/topologymodeler.war ${CATALINA_HOME}/webapps/winery-topologymodeler.war
COPY --from=builder /tmp/winery/org.eclipse.winery.frontends/target/workflowmodeler.war ${CATALINA_HOME}/webapps/winery-workflowmodeler.war

ADD docker/winery.yml.tpl /root/.winery/winery.yml.tpl

EXPOSE 8080

CMD dockerize -template /root/.winery/winery.yml.tpl:/root/.winery/winery.yml \
    && if [ -d "${WINERY_REPOSITORY_PATH}" ] && [ "$(ls -A ${WINERY_REPOSITORY_PATH})" ]; then echo "Repository at ${WINERY_REPOSITORY_PATH} is already initialized!"; else if [ ! "x${WINERY_REPOSITORY_URL}" = "x" ]; then git clone ${WINERY_REPOSITORY_URL} ${WINERY_REPOSITORY_PATH}; else git init $WINERY_REPOSITORY_PATH; fi fi \
    && cd ${WINERY_REPOSITORY_PATH} \
    && git config --global core.fscache true \
    && git lfs install \
    && echo 'export CATALINA_OPTS="-Djava.security.egd=file:/dev/./urandom -Xms512m -Xmx${WINERY_HEAP_MAX} -XX:MaxPermSize=256m"' > ${CATALINA_HOME}/bin/setenv.sh \
    && if [ ! "x${WINERY_JMX_ENABLED}" = "x" ]; then echo 'export CATALINA_OPTS="${CATALINA_OPTS} -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.port=9010 -Dcom.sun.management.jmxremote.rmi.port=9010 -Djava.rmi.server.hostname=0.0.0.0 -Dcom.sun.management.jmxremote.ssl=false"' >> ${CATALINA_HOME}/bin/setenv.sh; fi \
    && chmod a+x ${CATALINA_HOME}/bin/setenv.sh \
    && ${CATALINA_HOME}/bin/catalina.sh run
