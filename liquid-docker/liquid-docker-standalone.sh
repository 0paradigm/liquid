#!/bin/bash
#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

BASEDIR=$(dirname "$0")

if [[ $# -eq 0 ]]
then
    echo "Usage:"
    echo "  rebuild  build the whole backend service and generate docker images"
    echo "  start    run docker compose of liquid backend"
    echo "  stop     stop docker compose of liquid backend"
    echo "  clean    delete all dangling images (possibly caused by rebuild)"
    echo "Examples:"
    echo "  sh liquid-docker-standalone.sh rebuild start"
    echo "  sh liquid-docker-standalone.sh stop clean"
    exit 1
fi


for arg in "$@"
do
  [[ $arg == "update" ]] \
  && git pull \
  && echo "===>> source code updated" \
  && break;
done

for arg in "$@"
do
  [[ $arg == "rebuild" ]] \
  && docker-compose -f "$BASEDIR"/docker-compose.yml down --remove-orphans \
  && "$BASEDIR"/../mvnw clean package -f "$BASEDIR"/../pom.xml -Pdocker -Dmaven.test.skip=true \
  && echo "===>> liquid backend build succeed" \
  && break;
done

for arg in "$@"
do
  [[ $arg == "start" ]] \
  && docker-compose -f "$BASEDIR"/docker-compose.yml up -d \
  && echo "===>> liquid backend services started" \
  && exit 0 \
  || \
  [[ $arg == "stop" ]] \
  && docker-compose -f "$BASEDIR"/docker-compose.yml down --remove-orphans \
  && echo "===>> liquid backend services stopped" \
  && break;
done

for arg in "$@"
do
  [[ $arg == "clean" ]] \
  && docker rmi -f "$(docker images -f 'dangling=true' -q)" \
  && echo "===>> dangling images removed" \
  && break;
done
