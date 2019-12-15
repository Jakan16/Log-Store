#!/bin/bash
cd log-store 
export LOG_STORE_IMAGE=$1 
export LOG_STORE_VERSION=$2
docker-compose up -d --no-deps logstore
