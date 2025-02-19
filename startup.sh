#!/bin/bash


if [ "$(docker ps -q -f name=postgres)" ]; then
    echo "PostgreSQL 起動済."
else
    echo "PostgreSQL 起動中..."
    docker compose -f docker-compose.yml up -d postgres

    echo "PostgreSQL 起動待ち..."
    while ! docker exec postgres pg_isready -U postgres; do
        sleep 1
    done
    echo "PostgreSQL 完全起動."
fi

echo "book-manager-api 起動..."
docker compose -f docker-compose.yml up -d book-manager-api
