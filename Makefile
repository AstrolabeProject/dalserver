IMG=dals-dev
NAME=dals-dev
BUILDWAR = dist/dals.war

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
	docker build -t ${IMG} .

exec:
	docker exec -it ${NAME} bash

javadoc:
	ant javadoc

jar:
	ant jar

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
