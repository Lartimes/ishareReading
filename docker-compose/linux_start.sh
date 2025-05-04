#!/bin/bash

# Exit on error
set -e

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Function to check if a service is running
check_service() {
    local service=$1
    local port=$2
    echo "Checking $service on port $port..."
    if ! nc -z localhost $port >/dev/null 2>&1; then
        echo "Error: $service is not running on port $port"
        echo "Please start $service first"
        exit 1
    fi
    echo "$service is running"
}

# Check prerequisites
echo "Checking prerequisites..."
if! command_exists docker; then
    echo "Error: Docker is not installed"
    exit 1
fi

if! command_exists docker-compose; then
    echo "Error: Docker Compose is not installed"
    exit 1
fi

if! command_exists git; then
    echo "Error: Git is not installed"
    exit 1
fi

if! command_exists mvn; then
    echo "Error: Maven is not installed"
    exit 1
fi

if! command_exists nc; then
    echo "Error: netcat is not installed (required for service checks)"
    exit 1
fi

# Create and enter project directory
PROJECT_DIR="ishareReading"
if [ -d "$PROJECT_DIR" ]; then
    echo "Updating existing repository..."
    cd $PROJECT_DIR
    git pull
else
    echo "Cloning repository..."
    git clone https://github.com/yourusername/ishareReading.git
    cd $PROJECT_DIR
fi

# Compile and package modules
echo "Compiling and packaging modules..."
cd oss-starter
mvn clean install
cd ../common
mvn clean install
cd ../agent-reading
mvn clean install
cd ../backend-reading
mvn clean install

# Stop and remove existing containers if any
echo "Cleaning up existing containers..."
docker-compose -f docker-compose/docker-compose.yml down
docker-compose -f docker-compose/pgvector/docker-compose.yml down
docker-compose -f docker-compose/es/docker-compose.yaml down
docker-compose -f docker-compose/redis/docker-compose.yaml down

# Build the application
echo "Building the application..."
mvn clean package -DskipTests

# Check if build was successful
if [ ! -f "backend-reading/target/backend-reading-1.0.0.jar" ]; then
    echo "Error: Build failed - JAR file not found"
    exit 1
fi

# Copy the JAR file to the docker-compose directory
echo "Copying JAR file..."
cp backend-reading/target/backend-reading-1.0.0.jar docker-compose/

# Start PostgreSQL, Elasticsearch and Redis
echo "Starting PostgreSQL, Elasticsearch and Redis..."
docker-compose -f docker-compose/pgvector/docker-compose.yml up --build -d
docker-compose -f docker-compose/es/docker-compose.yaml up --build -d
docker-compose -f docker-compose/redis/docker-compose.yaml up --build -d

# Wait for services to be ready
echo "Waiting for PostgreSQL, Elasticsearch and Redis to be ready..."
sleep 30

# Check required services
echo "Checking required services..."
check_service "PostgreSQL" 54321
check_service "Elasticsearch" 9200
check_service "Redis" 6379

# Build and start the main application containers
echo "Starting Docker containers for the main application..."
cd docker-compose
docker-compose -f docker-compose.yml up --build -d

# Wait for the main application services to be ready
echo "Waiting for the main application services to be ready..."
sleep 30

# Check if services are running
echo "Checking service status..."
if! docker-compose -f docker-compose.yml ps | grep -q "Up"; then
    echo "Error: Some services in the main application failed to start"
    docker-compose -f docker-compose.yml logs
    exit 1
fi

echo "Setup complete! The application should be running at http://localhost:8080"
echo "You can check the logs using: docker-compose -f docker-compose.yml logs -f"