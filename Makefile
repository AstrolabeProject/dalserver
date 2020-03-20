ENVLOC=/etc/trhenv
IMG=dals:devel
NAME=dals-dev
BUILDWAR = dist/dals.war

.PHONY: help build clean cleanall exec jar javadoc run stop war watch

help:
	@echo "Make what? Try: build, clean, cleanall, docker, exec, jar, javadoc, run, stop, war, watch"
	@echo '    where: help     - show this help message'
	@echo '           build    - build project: clean all, recompile, make JAR & WAR files'
	@echo '           clean    - remove build files'
	@echo '           cleanall - remove build files and build output products'
	@echo '           docker   - build a DALS docker image from the DALS WAR file'
	@echo '           exec     - exec into running development server (CLI arg: NAME=containerID)'
	@echo '           jar      - build the DALS JAR file (for development)'
	@echo '           javadoc  - rebuild the DALS documentation'
	@echo '           run      - start a standalone development server container (for testing)'
	@echo '           stop     - stop a running standalone development server'
	@echo '           update   - copy config & web files into running server (for development)'
	@echo '           war      - build the DALS WAR file (for development)'
	@echo '           watch    - show logfile for a running standalone development server'

build:
	ant all

clean:
	ant clean

cleanall:
	ant clean-all

docker: ${BUILDWAR}
	docker build -t ${IMG} .

exec:
	docker cp config/bash_env ${NAME}:${ENVLOC}
	docker exec -it ${NAME} bash

jar:
	ant jar

javadoc:
	ant javadoc

run:
	docker run -d --rm --name ${NAME} -p 8080:8080 -v ${PWD}/images:/vos/images ${IMG}

stop:
	docker stop ${NAME}

update:
	docker cp ${PWD}/config ${NAME}:/etc/dals
	docker cp ${PWD}/web/index.html ${NAME}:/usr/local/tomcat/webapps/dals
	docker cp ${PWD}/web/scs-eazy.html ${NAME}:/usr/local/tomcat/webapps/dals
	docker cp ${PWD}/web/scs-jaguar.html ${NAME}:/usr/local/tomcat/webapps/dals
	docker cp ${PWD}/web/scs-jwst.html ${NAME}:/usr/local/tomcat/webapps/dals
	docker cp ${PWD}/web/scs-photo.html ${NAME}:/usr/local/tomcat/webapps/dals
	docker cp ${PWD}/web/siav1-jwst.html ${NAME}:/usr/local/tomcat/webapps/dals
	docker cp ${PWD}/web/siav2-jwst.html ${NAME}:/usr/local/tomcat/webapps/dals

war:
	ant war

watch:
	docker logs -f ${NAME}

${BUILDWAR}:
	ant all
