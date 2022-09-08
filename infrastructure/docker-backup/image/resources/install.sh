#!/bin/bash

apt-get update > /dev/null;

install -m 0700 /tmp/entrypoint.sh /
install -m 0700 /tmp/entrypoint-start-and-wait.sh /

install -m 0700 /tmp/init.sh /usr/local/bin/
install -m 0700 /tmp/backup.sh /usr/local/bin/
install -m 0700 /tmp/restore.sh /usr/local/bin/
install -m 0700 /tmp/restic-snapshots.sh /usr/local/bin/
