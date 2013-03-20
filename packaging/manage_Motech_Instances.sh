#!/bin/sh
# $1 - command: add / remove
# $2 - User name
# $3 - Connector port number
# $4 - Shutdown port number

# Exit on non-zero exit code
set -e

motech_default_package="motech-base"
sytem_type=unknown

# Check if motech default package is already installed in system
if [ -f /etc/redhat-release ] ; then
	system_type=rpm
	rpmpattern=$(rpm -qa | grep -c $motech_default_package)
	if [ "$rpmpattern" -lt 1 ]; then	
		echo "Please install first $motech_default_package package"
		exit
	fi
elif [ -f /etc/debian_version ] ; then
	system_type=deb
	debpattern=$(dpkg -l | grep -c $motech_default_package)
	if [ "$debpattern" -lt 1 ]; then	
		echo "Please install first $motech_default_package package"
		exit
	fi
fi

if [ "$#" -eq 4 -a "$1" = add -a $3 -eq $3 2>/dev/null -a $4 -eq $4 2>/dev/null ]; then
	mkdir -p /var/cache/motech/motech-$2/work/Catalina/localhost
	mkdir -p /var/cache/motech/motech-$2/temp
	mkdir -p /var/cache/motech/motech-$2/felix-cache
	mkdir -p /usr/share/motech/motech-$2
	mkdir -p /var/lib/motech/motech-$2/webapps
	mkdir -p /var/lib/motech/motech-$2/data
	mkdir -p /var/log/motech/motech-$2
	mkdir -p /etc/motech/motech-$2/

	cp -r /usr/share/motech/motech-default/conf /usr/share/motech/motech-$2/conf
	cp -r /var/lib/motech/motech-default/webapps/ROOT.war /var/lib/motech/motech-$2/webapps
	cp -r /var/lib/motech/motech-default/data /var/lib/motech/motech-$2

	cp -r /etc/init.d/motech-default /etc/init.d/motech-$2
	cp -r /etc/motech/motech-default/motech.conf /etc/motech/motech-$2/motech.conf 
	perl -p -i -e "s/motech-default/motech-$2/g" /etc/init.d/motech-$2
	perl -p -i -e "s/motech-default/motech-$2/g" /etc/motech/motech-$2/motech.conf 

	perl -p -i -e "s/8080/$3/g" /usr/share/motech/motech-$2/conf/server.xml
	perl -p -i -e "s/8005/$4/g" /usr/share/motech/motech-$2/conf/server.xml

	rm -r /var/lib/motech/motech-$2/data/config/motech-settings.conf
	echo "motech.app.name=$2" > /var/lib/motech/motech-$2/data/config/motech.properties


	#create symlinks
	ln -s /var/lib/motech/motech-$2/webapps/ /usr/share/motech/motech-$2/webapps
	ln -s /var/lib/motech/motech-$2/data/ /usr/share/motech/motech-$2/.motech
	ln -s /var/cache/motech/motech-$2/felix-cache/ /usr/share/motech/motech-$2/felix-cache
	ln -s /var/cache/motech/motech-$2/temp/ /usr/share/motech/motech-$2/temp
	ln -s /var/cache/motech/motech-$2/work/ /usr/share/motech/motech-$2/work
	ln -s /var/log/motech/motech-$2/ /usr/share/motech/motech-$2/logs

	# Create the motech user, if he doesn't exist
	if [ `grep -c motech-$2: /etc/passwd` -eq 0 ]; then
    		useradd -r -c "motech-$2 user" -d /usr/share/motech/motech-$2 motech-$2
	fi

	# Make motech the owner of relevant directories
	if [ -d /var/log/motech/motech-$2 ]; then
    		chown -R motech-$2:motech-$2 /var/log/motech/motech-$2
	fi
	if [ -d /var/cache/motech/motech-$2 ]; then
    		chown -R motech-$2:motech-$2 /var/cache/motech/motech-$2
	fi
	if [ -d /var/lib/motech/motech-$2 ]; then
    		chown -R motech-$2:motech-$2 /var/lib/motech/motech-$2
	fi

	# Register motech service with udpate-rc.d
	if [ "$system_type" = deb ]; then
		update-rc.d motech-$2 defaults 1>/dev/null
	elif [ "$system_type" = rpm ]; then
		chkconfig --add motech-$2
	else
		echo "Unrecognized Os"
	fi

elif [ "$#" -eq 2 -a "$1" = remove ]; then	
	# Unregister motech service from rc.d
	if [ "$system_type" = deb ]; then
		# Stop the motech server
		if [ -f /etc/init.d/motech-$2 ]; then
    			invoke-rc.d motech-$2 stop
		fi
		update-rc.d -f motech-$2 remove 1>/dev/null
	elif [ "$system_type" = rpm ]; then
		# Stop the motech server
		if [ -f /etc/init.d/motech-$2 ]; then
    			/etc/init.d/motech-$2 stop
		fi
		chkconfig --del motech-$2
	else
		echo "Unrecognized Os"
	fi

	# Delete the motech user, if he exists
	if [ ! `grep -c motech-$2: /etc/passwd` -eq 0 ]; then
    		userdel motech-$2
	fi	
	
	# Remove cache
	rm -rf /var/cache/motech/motech-$2/

	# Clean up the webapp
	rm -rf /var/lib/motech/motech-$2/

	rm -rf /var/log/motech/motech-$2/
	rm -rf /usr/share/motech/motech-$2/
	
	rm -rf /etc/init.d/motech-$2
	rm -rf /etc/motech/motech-$2/

elif [ "$#" -eq 2 -a "$1" = update ]; then
	# Unregister motech service from rc.d
	if [ "$system_type" = deb ]; then
		# Stop the motech server
		if [ -f /etc/init.d/motech-$2 ]; then
    			invoke-rc.d motech-$2 stop
		fi
		update-rc.d -f motech-$2 remove 1>/dev/null
	elif [ "$system_type" = rpm ]; then
		# Stop the motech server
		if [ -f /etc/init.d/motech-$2 ]; then
    			/etc/init.d/motech-$2 stop
		fi
		chkconfig --del motech-$2
	else
		echo "Unrecognized Os"
	fi
	
	cd /var/lib/motech/motech-default/webapps/
	temp_motech_default=$(sudo unzip -p ROOT.war META-INF/MANIFEST.MF | grep Implementation-Version)
	echo "$motech_default_package" $temp_motech_default 

	cd /var/lib/motech/motech-$2/webapps/
	temp_motech_tenant=$(sudo unzip -p ROOT.war META-INF/MANIFEST.MF | grep Implementation-Version)
	echo "Motech-$2" $temp_motech_tenant

	if [ "$temp_motech_default"="$temp_motech_tenant" ]; then
		echo "Motech-$2 package is up to date!"
		exit
	fi
	
	echo "Do you want to update motech-$2 package? (y/n)"
	read answer

	if [ "$answer" = y ]; then
		# Copy and replace new file
		if [ ! `grep -c motech-$2: /etc/passwd` -eq 0 ]; then
			rm -rf /var/lib/motech/motech-$2/webapps/*
			cp -r /var/lib/motech/motech-default/webapps/ROOT.war /var/lib/motech/motech-$2/webapps
			cp -r /var/lib/motech/motech-default/data/bundles /var/lib/motech/motech-$2/data	
			echo "Motech-$2 package updated"	
		fi
	else
		echo "Break"
		exit
	fi			


elif [ "$#" -eq 1 -a "$1" = users ]; then
	#Show installed motech instances and their versions
	for motech_user in `cut -d: -f1 < /etc/passwd | grep motech`
	do
		cd /var/lib/motech/${motech_user}/webapps/
  		echo ${motech_user} $(cat /usr/share/motech/${motech_user}/conf/server.xml | grep -m 1 "Connector port" | awk '{ print $2 }') $(sudo unzip -p ROOT.war META-INF/MANIFEST.MF | grep Implementation-Version) 
		
	done

else	
	echo Please run script with following arguments:
	echo 1.Add Tenant: manage_Motech_Instances.sh add tenat_name connector_port_number shutdown_port_number 
	echo 2.Remove Tenant: manage_Motech_Instances.sh remove tenat_name
	echo 3.Update Tenant: manage_Motech_Instances.sh update tenat_name
	echo 4.Show Tenant List: manage_Motech_Instances.sh users
fi
