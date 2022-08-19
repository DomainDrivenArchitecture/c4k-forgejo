#!/bin/bash

set -Eeo pipefail

function main() {

    file_env AWS_ACCESS_KEY_ID
    file_env AWS_SECRET_ACCESS_KEY

    file_env POSTGRES_DB
    file_env POSTGRES_PASSWORD
    file_env POSTGRES_USER

    # Restore latest snapshot into /var/backups/restore
    rm -rf /var/backups/restore
    restore-directory '/var/backups/restore'

    rm -rf /data/gitea/*
    rm -rf /data/git/repositories/*
    cp /var/backups/restore/gitea/* /data/gitea/
    cp /var/backups/restore/git/repositories/* /data/git/repositories/
    
    # adjust file permissions
    #chown -R git:git /data

    # Regenerate Git Hooks
    /usr/local/bin/gitea -c '/data/gitea/conf/app.ini' admin regenerate hooks

    # Restore db
    drop-create-db
    #restore-roles
    restore-db
}

source /usr/local/lib/functions.sh
source /usr/local/lib/pg-functions.sh
source /usr/local/lib/file-functions.sh

main
