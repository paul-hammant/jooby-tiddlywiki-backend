#!/bin/sh

echo "Performing TiddlyWiki5 index.html download"
mkdir -p ./public
wget https://raw.githubusercontent.com/rsc/tiddly/master/index.html -O ./public/index.html

