FROM tomcat:8.5

MAINTAINER Tom Hicks <hickst@email.arizona.edu>

RUN apt-get update \
#     && apt-get install -y --no-install-recommends XXX \
    && rm -rf /var/lib/apt/lists/*

RUN mkdir -p /opt/services/dalserver /vos/resultStore /vos/staging /vos/images
COPY web/META-INF/context.xml web/META-INF/server.xml ${CATALINA_HOME}/conf/
COPY dist/dals.war ${CATALINA_HOME}/webapps
COPY config/* /opt/services/dalserver/
