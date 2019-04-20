#!/bin/sh

lein deps
mkdir -p releases
zip -r releases/section-1-`date +"%Y-%m-%d_%H-%M"`.zip \
    . \
    -x "./.git/*" \
    -x "releases/*"

echo
echo "Don't forget to upload the zipfile"
echo " to https://github.com/<your-account-name>/<repo-name>/downloads"
echo `ls -t releases/section-1-*.zip | head -n1`
echo
