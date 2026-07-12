#!/bin/sh

APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd -P)
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper-main.jar:$APP_HOME/gradle/wrapper/gradle-wrapper-shared.jar
exec java -Xmx64m -Xms64m -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
