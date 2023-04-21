#!/usr/bin/env bash

# set things
set -o nounset
set -o xtrace
set -o errexit
set -eo pipefail

# dirs
srcDir="/home/$USER/"
srcName="main.js"
targetDir="/home/$USER/"
targetName="c4k-forgejo.js"

echo "build"
shadow-cljs compile frontend

echo "move and rename file"
cp $srcDir$srcName $targetDir$targetName

echo "build"
(cd $targetDir; lein ring server)
