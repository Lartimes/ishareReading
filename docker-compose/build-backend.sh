#!/bin/bash

# 定义镜像名称和标签
IMAGE_NAME="ishare-reading"
IMAGE_TAG="latest"

# 构建镜像
echo "正在构建镜像 $IMAGE_NAME:$IMAGE_TAG..."
docker buildx  build .  -t "$IMAGE_NAME:$IMAGE_TAG"..
