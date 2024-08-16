#!/bin/bash

set -Eexo pipefail

function main() {
    local db_snapshot_id="${1:-latest}"
    local file_snapshot_id="${2:-latest}"

    file_env AWS_ACCESS_KEY_ID
    file_env AWS_SECRET_ACCESS_KEY

    file_env POSTGRES_DB
    file_env POSTGRES_PASSWORD
    file_env POSTGRES_USER

    # Restore latest snapshot into /var/backups/restore
    restore-directory '/var/backups/restore'  ${file_snapshot_id}

    rm -rf /var/backups/gitea/*
    rm -rf /var/backups/git/repositories/*
    mv /var/backups/restore/gitea /var/backups/
    mv /var/backups/restore/git/repositories /var/backups/git/
    
    # adjust file permissions for the git user
    chown -R 1000:1000 /var/backups

    # TODO: Regenerate Git Hooks? Do we need this?
    #/usr/local/bin/gitea -c '/data/gitea/conf/app.ini' admin regenerate hooks

    # Restore db
    drop-create-db
    restore-db ${db_snapshot_id}
}

source /usr/local/lib/functions.sh
source /usr/local/lib/pg-functions.sh
source /usr/local/lib/file-functions.sh

main "$@"
