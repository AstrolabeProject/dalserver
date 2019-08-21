FROM tomcat:8.5

MAINTAINER Tom Hicks <hickst@email.arizona.edu>

# RUN apt-get update \
#     && apt-get install -y --no-install-recommends XXX \
#     && rm -rf /var/lib/apt/lists/*

COPY dist/dals.war ${CATALINA_HOME}/webapps
COPY web/META-INF/context.xml web/META-INF/server.xml ${CATALINA_HOME}/conf/
