#!/bin/sh

KEY=$2
VALUE=$3
FILE_NAME=$1


sed -i "s/${KEY}\s*\=.*$/${KEY}=${VALUE}/g" ${FILE_NAME}
#sleep 1
