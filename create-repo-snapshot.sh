#!/bin/bash

repoRoot="/home/raven/Projects/Current/IntelliJ/24-7-platform/"

#git pull "$repoRoot"
#$repoRoot/link-specifications/update-evaluate-positive.sh
#$repoRoot/link-specifications/update-evaluate-negative.sh

rsnapshot -c linkspec.rsnapshot.conf hourly


