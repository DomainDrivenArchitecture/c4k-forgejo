#!/bin/bash

set -o pipefail

function main() {
    file_env AWS_ACCESS_KEY_ID
    file_env AWS_SECRET_ACCESS_KEY
    file_env RESTIC_DAYS_TO_KEEP 30
    file_env RESTIC_MONTHS_TO_KEEP 12

    #backup-roles 'TODO'
    backup-db-dump
    backup-fs-from-directory '/var/backups/' 'gitea/' 'git/repositories/'
}

source /usr/local/lib/functions.sh
source /usr/local/lib/pg-functions.sh
source /usr/local/lib/file-functions.sh

main
