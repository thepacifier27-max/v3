@echo off
set APP_HOME=%~dp0
set CLASSPATH=%APP_HOME%gradle\wrapper\gradle-wrapper-main.jar;%APP_HOME%gradle\wrapper\gradle-wrapper-shared.jar
java -Xmx64m -Xms64m -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
