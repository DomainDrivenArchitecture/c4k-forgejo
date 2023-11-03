#!/bin/bash

set -eux pipefail

function main() {
    file_env AWS_ACCESS_KEY_ID
    file_env AWS_SECRET_ACCESS_KEY

    init-database-repo
    init-file-repo
}

source /usr/local/lib/functions.sh
source /usr/local/lib/pg-functions.sh
source /usr/local/lib/file-functions.sh
main
