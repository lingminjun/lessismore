#!/usr/bin/env bash

cd ../lessismore-xauto


mvn clean install -DskipTests


cd ../lessismore-sample-copier


mvn clean package -DskipTests



