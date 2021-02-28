@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  samza-example startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and SAMZA_EXAMPLE_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\samza-example-1.0.0.jar;%APP_HOME%\lib\samza-yarn_2.11-1.0.0.jar;%APP_HOME%\lib\hadoop-yarn-client-2.6.1.jar;%APP_HOME%\lib\hadoop-yarn-common-2.6.1.jar;%APP_HOME%\lib\hadoop-common-2.6.1.jar;%APP_HOME%\lib\jersey-json-1.9.jar;%APP_HOME%\lib\jackson-jaxrs-1.9.13.jar;%APP_HOME%\lib\samza-kv-rocksdb_2.11-1.0.0.jar;%APP_HOME%\lib\samza-kv_2.11-1.0.0.jar;%APP_HOME%\lib\samza-kafka_2.11-1.0.0.jar;%APP_HOME%\lib\samza-log4j-1.0.0.jar;%APP_HOME%\lib\samza-core_2.11-1.0.0.jar;%APP_HOME%\lib\samza-api-1.0.0.jar;%APP_HOME%\lib\kafka_2.11-0.11.0.2.jar;%APP_HOME%\lib\kafka-clients-0.11.0.2.jar;%APP_HOME%\lib\zkclient-0.10.jar;%APP_HOME%\lib\hadoop-auth-2.6.1.jar;%APP_HOME%\lib\curator-recipes-2.6.0.jar;%APP_HOME%\lib\curator-framework-2.6.0.jar;%APP_HOME%\lib\curator-client-2.6.0.jar;%APP_HOME%\lib\zookeeper-3.4.10.jar;%APP_HOME%\lib\slf4j-log4j12-1.7.25.jar;%APP_HOME%\lib\metrics-core-2.2.0.jar;%APP_HOME%\lib\scalatra-scalate_2.11-2.5.0.jar;%APP_HOME%\lib\scalatra_2.11-2.5.0.jar;%APP_HOME%\lib\avro-1.7.4.jar;%APP_HOME%\lib\grizzled-slf4j_2.11-1.3.0.jar;%APP_HOME%\lib\mime-util-2.1.3.jar;%APP_HOME%\lib\scalate-core_2.11-1.8.0.jar;%APP_HOME%\lib\scalate-util_2.11-1.8.0.jar;%APP_HOME%\lib\apacheds-kerberos-codec-2.0.0-M15.jar;%APP_HOME%\lib\apacheds-i18n-2.0.0-M15.jar;%APP_HOME%\lib\api-asn1-api-1.0.0-M20.jar;%APP_HOME%\lib\api-util-1.0.0-M20.jar;%APP_HOME%\lib\slf4j-api-1.7.25.jar;%APP_HOME%\lib\samza-shell-1.0.0.jar;%APP_HOME%\lib\hadoop-hdfs-2.6.1.jar;%APP_HOME%\lib\jackson-xc-1.9.13.jar;%APP_HOME%\lib\jackson-mapper-asl-1.9.13.jar;%APP_HOME%\lib\jackson-core-asl-1.9.13.jar;%APP_HOME%\lib\log4j-1.2.17.jar;%APP_HOME%\lib\jline-0.9.94.jar;%APP_HOME%\lib\netty-3.10.5.Final.jar;%APP_HOME%\lib\hadoop-yarn-api-2.6.1.jar;%APP_HOME%\lib\htrace-core-3.0.4.jar;%APP_HOME%\lib\guava-17.0.jar;%APP_HOME%\lib\commons-lang3-3.5.jar;%APP_HOME%\lib\scala-compiler-2.11.8.jar;%APP_HOME%\lib\scala-reflect-2.11.8.jar;%APP_HOME%\lib\scala-parser-combinators_2.11-1.0.4.jar;%APP_HOME%\lib\scala-xml_2.11-1.0.6.jar;%APP_HOME%\lib\scalatra-common_2.11-2.5.0.jar;%APP_HOME%\lib\scala-library-2.11.11.jar;%APP_HOME%\lib\jetty-webapp-9.2.7.v20150116.jar;%APP_HOME%\lib\jopt-simple-5.0.3.jar;%APP_HOME%\lib\commons-collections4-4.0.jar;%APP_HOME%\lib\failsafe-1.1.0.jar;%APP_HOME%\lib\rocksdbjni-5.7.3.jar;%APP_HOME%\lib\jets3t-0.9.0.jar;%APP_HOME%\lib\httpclient-4.4.1.jar;%APP_HOME%\lib\commons-httpclient-3.1.jar;%APP_HOME%\lib\joda-time-2.2.jar;%APP_HOME%\lib\lz4-1.3.0.jar;%APP_HOME%\lib\snappy-java-1.1.2.6.jar;%APP_HOME%\lib\jetty-xml-9.2.7.v20150116.jar;%APP_HOME%\lib\jetty-servlet-9.2.7.v20150116.jar;%APP_HOME%\lib\commons-configuration-1.6.jar;%APP_HOME%\lib\commons-lang-2.6.jar;%APP_HOME%\lib\jasper-runtime-5.5.23.jar;%APP_HOME%\lib\commons-el-1.0.jar;%APP_HOME%\lib\commons-digester-1.8.jar;%APP_HOME%\lib\commons-beanutils-core-1.8.0.jar;%APP_HOME%\lib\commons-beanutils-1.7.0.jar;%APP_HOME%\lib\commons-logging-1.2.jar;%APP_HOME%\lib\hadoop-annotations-2.6.1.jar;%APP_HOME%\lib\protobuf-java-2.5.0.jar;%APP_HOME%\lib\httpcore-4.4.1.jar;%APP_HOME%\lib\commons-codec-1.9.jar;%APP_HOME%\lib\commons-cli-1.2.jar;%APP_HOME%\lib\commons-math3-3.1.1.jar;%APP_HOME%\lib\xmlenc-0.52.jar;%APP_HOME%\lib\commons-io-2.4.jar;%APP_HOME%\lib\commons-net-3.1.jar;%APP_HOME%\lib\commons-collections-3.2.1.jar;%APP_HOME%\lib\servlet-api-2.5.jar;%APP_HOME%\lib\jetty-6.1.26.jar;%APP_HOME%\lib\jetty-util-6.1.26.jar;%APP_HOME%\lib\jersey-guice-1.9.jar;%APP_HOME%\lib\jersey-server-1.9.jar;%APP_HOME%\lib\jersey-client-1.9.jar;%APP_HOME%\lib\jersey-core-1.9.jar;%APP_HOME%\lib\jasper-compiler-5.5.23.jar;%APP_HOME%\lib\jsp-api-2.1.jar;%APP_HOME%\lib\gson-2.2.4.jar;%APP_HOME%\lib\jsch-0.1.42.jar;%APP_HOME%\lib\jsr305-1.3.9.jar;%APP_HOME%\lib\commons-compress-1.4.1.jar;%APP_HOME%\lib\commons-daemon-1.0.13.jar;%APP_HOME%\lib\xercesImpl-2.9.1.jar;%APP_HOME%\lib\juniversalchardet-1.0.3.jar;%APP_HOME%\lib\jaxb-impl-2.2.3-1.jar;%APP_HOME%\lib\jaxb-api-2.2.2.jar;%APP_HOME%\lib\guice-servlet-3.0.jar;%APP_HOME%\lib\guice-3.0.jar;%APP_HOME%\lib\jetty-security-9.2.7.v20150116.jar;%APP_HOME%\lib\jetty-server-9.2.7.v20150116.jar;%APP_HOME%\lib\jetty-http-9.2.7.v20150116.jar;%APP_HOME%\lib\jetty-io-9.2.7.v20150116.jar;%APP_HOME%\lib\jetty-util-9.2.7.v20150116.jar;%APP_HOME%\lib\jettison-1.1.jar;%APP_HOME%\lib\cglib-2.2.1-v20090111.jar;%APP_HOME%\lib\asm-3.1.jar;%APP_HOME%\lib\java-xmlbuilder-0.4.jar;%APP_HOME%\lib\paranamer-2.3.jar;%APP_HOME%\lib\xz-1.0.jar;%APP_HOME%\lib\stax-api-1.0-2.jar;%APP_HOME%\lib\activation-1.1.jar;%APP_HOME%\lib\javax.inject-1.jar;%APP_HOME%\lib\aopalliance-1.0.jar;%APP_HOME%\lib\javax.servlet-api-3.1.0.jar

@rem Execute samza-example
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %SAMZA_EXAMPLE_OPTS%  -classpath "%CLASSPATH%" com.alain898.book.realtimestreaming.chapter6.samza.WordCountExample %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable SAMZA_EXAMPLE_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%SAMZA_EXAMPLE_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
