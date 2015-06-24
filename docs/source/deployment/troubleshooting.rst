
======================================
Troubleshooting MOTECH
======================================

Synopsis
========

This document describes how to get an incomplete or broken MOTECH installation running, using linux command line tools.


Set environment variables:
==========================

These instructions will presume these environment variables::

		export JAVA_HOME=$(/usr/libexec/java_home -v 1.7)  # TODO: this is mac-only
		export CATALINA_HOME="/usr/local/tomcat"
		export CATALINA_OPTS="-Xms1024m -Xmx2048m -XX:MaxPermSize=1024m"

How to check where the environment variables point::

			ls $JAVA_HOME
			COPYRIGHT				include
			LICENSE					jre
			README.html				lib
			THIRDPARTYLICENSEREADME-JAVAFX.txt	man
			THIRDPARTYLICENSEREADME.txt		release
			bin					src.zip
			db
			ls $CATALINA_HOME
			bin		lib		temp		webapps_old
			conf		logs		webapps		work


Example configuration files
===========================

cat ~/srchome/src/my_motech_config/bootstrap.properties::

		#MOTECH bootstrap properties.
		#Mon Mar 23 15:39:38 PDT 2015
		sql.url=jdbc\:mysql\://localhost\:3306/
		sql.driver=com.mysql.jdbc.Driver
		couchDb.url=
		config.source=UI
		tenant.id=DEFAULT
		sql.password=motech
		sql.user=root


cat ~/srchome/src/my_motech_config/motech-settings.properties::

		########################################################################
		# System configuration properties
		########################################################################

		system.language=en
		statusmsg.timeout=60

		provider.name=
		provider.url=
		login.mode=repository
		jmx.host=localhost
		jmx.broker=localhost


cat ~/srchome/src/my_motech_config/log4j.properties::

		#
		#Wed Mar 25 10:39:02 PDT 2015
		root=ERROR
		org.motechproject.osgi.web=ERROR
		org.motechproject=DEBUG
		org.motechproject.mds.annotations=DEBUG
		org.springframework.web=ERROR
		org.motechproject.scheduler.service.impl.MotechScheduler=ERROR

We recommend keeping three terminal windows always open::

		# terminal 1: mysql session for resetting the database
			mysql --port=3306 -h127.0.0.1 -u root -p

		# terminal 2: shell for tailing logs
			# make temp directory
			tail -f -n0 $CATALINA_HOME/logs/catalina.out | tee rawlogs/replay_try8

		# terminal 3: shell for controlling tomcat
			# check if tomcat is running:
				ps -A | grep tomcat
			# change tomcat state:
			$CATALINA_HOME/bin/catalina.sh jpda start
			$CATALINA_HOME/bin/catalina.sh jpda stop


How to Reset State
==================

tomcat state::

		rm -rf ~/.motech/config/*
		rm -rf $CATALINA_HOME/webapps/motech-platform-server*
		cp ~/srchome/src/my_motech_config/* ~/.motech/config/
		cp ./platform/server/target/motech-platform-server.war $CATALINA_HOME/webapps/motech-platform-server.war

mysql state::

		# inside mysql session
		drop database motech_data_services;
