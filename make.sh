#!/usr/bin/env bash

cd lessismore-xauto


mvn clean install -DskipTests

echo "xauto打包完成。。。。"

cd ..

mvn clean package -DskipTests



