#!/bin/sh
rm git.properties
echo "git.branch=`git rev-parse --abbrev-ref HEAD`" >> ./git.properties
