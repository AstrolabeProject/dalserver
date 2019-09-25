APP=dals
WARNAME = ${APP}.war
BUILDWAR = dist/${WARNAME}

.PHONY: help build clean cleanall exec javadoc jar run stop war watch

help:
	@echo "Make what? Try: build, cleanall, docker, exec, jar, javadoc, run, stop, war, watch"

build:
	ant all

clean:
	ant clean

cleanall:
	ant clean-all

docker: ${BUILDWAR}
	docker build -t ${APP} .

exec:
	docker exec -it ${APP} /bin/bash

javadoc:
	ant javadoc

jar:
	ant jar

run:
	docker run -d --rm --name ${APP} -p 8080:8080 -v ${PWD}/images:/vos/images ${APP}

stop:
	docker stop ${APP}

war:
	ant war

watch:
	docker logs -f ${APP}

${BUILDWAR}:
	ant all

%:
	@:
