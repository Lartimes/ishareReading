#!/bin/bash
#需要确保在WORK_DIR
#需要将data 目录 放到当前WORK_DIR
cd oss-starter
mvn clean install
cd ../ishareReadingParent
mvn clean install
cd ..


docker-compose -f docker-compose/docker-compose.yml down
docker-compose -f docker-compose/pgvector/docker-compose.yml down
docker-compose -f docker-compose/es/docker-compose.yaml down
docker-compose -f docker-compose/redis/docker-compose.yaml down

#复制jar包进去
cp backend-reading/target/backend-reading-1.0.0.jar docker-compose/

docker-compose -f docker-compose/pgvector/docker-compose.yml up --build -d
docker-compose -f docker-compose/es/docker-compose.yaml up --build -d
docker-compose -f docker-compose/redis/docker-compose.yaml up --build -d

check_service "PostgreSQL" 54321
check_service "Elasticsearch" 9200
check_service "Redis" 6379

cd docker-compose
#启动后端
docker-compose -f docker-compose.yml up --build -d