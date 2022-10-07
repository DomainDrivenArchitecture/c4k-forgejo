#!/bin/bash

function main() {
    create-pg-pass

    while true; do
        sleep 1m
    done
}

source /usr/local/lib/functions.sh
source /usr/local/lib/pg-functions.sh
main