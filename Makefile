ifeq ($(OS),Windows_NT)
	GRADLE_EXECUTABLE = gradlew.cmd
else
	GRADLE_EXECUTABLE = ./gradlew
endif

.PHONY: all
all: clean build

.PHONY: test
test:
	${GRADLE_EXECUTABLE} test

.PHONY: build
build:
	${GRADLE_EXECUTABLE} jar

.PHONY: clean
clean:
	${GRADLE_EXECUTABLE} clean
