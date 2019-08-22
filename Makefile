APP=dals
JSRC = src/dalserver
WARNAME = $(APP).war
BUILDWAR = dist/$(WARNAME)

.PHONY: help build clean cleanall exec javadoc jar reset run war watch

help:
	@echo "Make what? Try: build, cleanall, docker, exec, jar, javadoc, reset, run, war, watch"

build:
	ant all

clean:
	ant clean

cleanall:
	ant clean-all

docker: $(BUILDWAR)
	docker build -t $(APP) .

exec:
	docker exec -it $(APP) /bin/bash

javadoc:
	ant javadoc

jar:
	ant jar

reset:
	docker stop $(APP)
	docker rm -f $(APP)

run:
	docker run -d --rm --name $(APP) -p 8080:8080 $(APP)

war:
	ant war

watch:
	docker logs -f $(APP)

$(BUILDWAR):
	ant all

%:
	@:
