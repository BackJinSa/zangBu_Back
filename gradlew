#!/bin/sh

DIR="$(cd "$(dirname "$0")" && pwd)"
CLASSPATH="$DIR/gradle/wrapper/gradle-wrapper.jar"
MAIN_CLASS="org.gradle.wrapper.GradleWrapperMain"

if [ -n "$JAVA_HOME" ]; then
  JAVACMD="$JAVA_HOME/bin/java"
else
  JAVACMD="java"
fi

exec "$JAVACMD" -Dorg.gradle.appname=gradlew -classpath "$CLASSPATH" $MAIN_CLASS "$@"
