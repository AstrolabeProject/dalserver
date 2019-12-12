FROM tomcat:8.5.49

MAINTAINER Tom Hicks <hickst@email.arizona.edu>

RUN apt-get update \
    && apt-get install -y --no-install-recommends less \
    && rm -rf /var/lib/apt/lists/*

RUN mkdir -p /etc/dals/config /vos/resultStore /vos/staging /vos/images
COPY web/META-INF/context.xml web/META-INF/server.xml ${CATALINA_HOME}/conf/
COPY dist/dals.war ${CATALINA_HOME}/webapps
COPY config/* /etc/dals/config/
