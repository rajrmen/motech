#!/bin/bash

function purge_motech() {
    $CHROOT yum purge motech-base -y
    $CHROOT rm -rf /var/log/motech/motech-default
    $CHROOT rm -rf /var/cache/motech/motech-default
    $CHROOT rm -rf /usr/share/motech/motech-default
    $CHROOT rm -rf /etc/motech/motech-default
    $CHROOT rm -rf /var/lib/motech/motech-default
    $CHROOT rm -f /etc/init.d/motech-default
}

function init_data() {
	MOTECH_OWNED="/var/lib/motech/motech-default /var/cache/motech/motech-default"
	NON_MOTECH_OWNED="/var/lib/motech/motech-default /var/cache/motech/motech-default"
}
