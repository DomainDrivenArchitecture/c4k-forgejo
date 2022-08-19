#!/bin/bash

set -o pipefail

function main() {
    file_env AWS_ACCESS_KEY_ID
    file_env AWS_SECRET_ACCESS_KEY

    restic -r ${RESTIC_REPOSITORY}/files snapshots
}

source /usr/local/lib/functions.sh
source /usr/local/lib/file-functions.sh

main
