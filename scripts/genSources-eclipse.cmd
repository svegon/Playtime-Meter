@echo off
cd ..
call gradlew.bat genSources
call gradlew.bat eclipse
call "./scripts/build.cmd"
pause