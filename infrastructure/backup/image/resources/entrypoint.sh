#!/bin/bash

set -Eexo pipefail

function main() {
    create-pg-pass

	/usr/local/bin/backup.sh
}

source /usr/local/lib/functions.sh
source /usr/local/lib/pg-functions.sh
main
