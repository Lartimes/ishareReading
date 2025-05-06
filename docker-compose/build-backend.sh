#!/bin/bash

IMAGE_NAME="ishare-reading"
IMAGE_TAG="latest"

docker buildx  build .  -t "$IMAGE_NAME:$IMAGE_TAG"..
