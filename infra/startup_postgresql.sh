#!/bin/bash

if [ "$(docker ps -q -f name=postgres)" ]; then
    echo "PostgreSQL is already running."
else
    echo "Starting PostgreSQL..."
    docker compose -f ../docker-compose.yml up -d postgres

    echo "Waiting for PostgreSQL to start..."
    while ! docker exec postgres pg_isready -U postgres; do
        sleep 1
    done
    echo "PostgreSQL is fully started."
fi
