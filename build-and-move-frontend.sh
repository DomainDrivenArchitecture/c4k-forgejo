#!/usr/bin/env bash

# set things
set -o nounset
set -o xtrace
set -o errexit
set -eo pipefail

# dirs
srcDir="/home/$USER/repo/c4k/c4k-forgejo/public/js/"
srcName="main.js"
targetDir="/home/$USER/repo/website/dda-io/content/templates/js/"
targetName="c4k-gitea.js"

echo "build test"
shadow-cljs compile test

echo "test"
node target/node-tests.js

echo "build frontend"
shadow-cljs compile frontend

echo "move and rename file"
cp $srcDir$srcName $targetDir$targetName

echo "run"
(cd $targetDir; lein ring server)
