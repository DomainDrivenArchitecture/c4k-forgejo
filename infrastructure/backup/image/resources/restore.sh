#!/bin/bash

set -eux pipefail

function main() {

    file_env AWS_ACCESS_KEY_ID
    file_env AWS_SECRET_ACCESS_KEY

    file_env POSTGRES_DB
    file_env POSTGRES_PASSWORD
    file_env POSTGRES_USER

    # Restore latest snapshot into /var/backups/restore
    restore-directory '/var/backups/restore'

    rm -rf /var/backups/gitea/*
    rm -rf /var/backups/git/repositories/*
    cp -r /var/backups/restore/gitea /var/backups/ #ToDo: mv instead of cp or rm -rf after
    cp -r /var/backups/restore/git/repositories /var/backups/git/ #ToDo: mv instead of cp or rm -rf after
    
    # adjust file permissions for the git user
    chown -R 1000:1000 /var/backups

    # TODO: Regenerate Git Hooks? Do we need this?
    #/usr/local/bin/gitea -c '/data/gitea/conf/app.ini' admin regenerate hooks

    # Restore db
    drop-create-db
    restore-db
}

source /usr/local/lib/functions.sh
source /usr/local/lib/pg-functions.sh
source /usr/local/lib/file-functions.sh

main
