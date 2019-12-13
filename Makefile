ENVLOC=/etc/trhenv
IMG=dals:1H
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
	docker run -d --rm --name ${NAME} -p 8090:8080 -v ${PWD}/images:/vos/images ${IMG}

stop:
	docker stop ${NAME}

war:
	ant war

watch:
	docker logs -f ${NAME}

${BUILDWAR}:
	ant all

%:
	@:
