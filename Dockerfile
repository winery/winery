FROM maven:3-jdk-8 as winery_builder

ARG GIT_REPO_URL=https://github.com/OpenTOSCA/winery.git
ARG GIT_BRANCH=master

RUN rm /dev/random && ln -s /dev/urandom /dev/random \
    && curl -sL https://deb.nodesource.com/setup_6.x | bash - \
    && apt-get update -qq && apt-get install -qqy \
        nodejs \
        unzip \
    && rm -rf /var/lib/apt/lists/* \
    && echo '{ "allow_root": true }' > /root/.bowerrc \
    && npm install -g bower

RUN git clone --recursive --depth=1 ${GIT_REPO_URL} -b ${GIT_BRANCH} /tmp/winery \
    && cd /tmp/winery \
    && mvn package -DskipTests=true

RUN apt-get update -qq && apt-get install -qqy \
        unzip \
    && unzip /tmp/winery/org.eclipse.winery.repository/target/winery.war -d /opt/winery \
    && unzip /tmp/winery/org.eclipse.winery.topologymodeler/target/winery-topologymodeler.war -d /opt/winery-topologymodeler \
    && sed -i "sXbpmn4toscamodelerBaseURI=.*Xbpmn4toscamodelerBaseURI=../bpmn4toscaX" /opt/winery/WEB-INF/classes/winery.properties \
    && sed -i "sX#repositoryPath=.*XrepositoryPath=/var/opentosca/repositoryX" /opt/winery/WEB-INF/classes/winery.properties


FROM node:6.10 as bpmn4tosca_builder

ARG GIT_REPO_URL=https://github.com/winery/BPMN4TOSCAModeler.git
ARG GIT_BRANCH=master

RUN git clone --recursive --depth=1 ${GIT_REPO_URL} -b ${GIT_BRANCH} /opt/bpmn4tosca \
    && cd /opt/bpmn4tosca \
    && npm install -g grunt-cli \
    && npm install \
    && grunt


FROM tomcat:8.5-jre8
LABEL maintainer "Johannes Wettinger <jowettinger@gmail.com>, Oliver Kopp <kopp.dev@gmail.com>, Michael Wurster <miwurster@gmail.com>"

RUN rm /dev/random && ln -s /dev/urandom /dev/random \
    && rm -rf ${CATALINA_HOME}/webapps/*

COPY --from=winery_builder /opt/winery ${CATALINA_HOME}/webapps/ROOT
COPY --from=winery_builder /opt/winery-topologymodeler ${CATALINA_HOME}/webapps/winery-topologymodeler
COPY --from=bpmn4tosca_builder /opt/bpmn4tosca/dist ${CATALINA_HOME}/webapps/bpmn4tosca

EXPOSE 8080

CMD ${CATALINA_HOME}/bin/catalina.sh run
