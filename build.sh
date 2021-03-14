#!/usr/bin/env bash

gradle clean -x test build

docker build --no-cache -t xingdaba/xingdaba-order .

docker tag xingdaba/xingdaba-order hub.c.163.com/riyuexingchenace/xingdaba/xingdaba-order:latest

docker push hub.c.163.com/riyuexingchenace/xingdaba/xingdaba-order:latest
