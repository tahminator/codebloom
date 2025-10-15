set -euo pipefail

db_cleanup() {
    echo "Stopping and removing postgres container..."
    docker stop codebloom-db >/dev/null 2>&1 || true
    docker rm codebloom-db >/dev/null 2>&1 || true

    unset DATABASE_HOST
    unset DATABASE_PORT
    unset DATABASE_NAME
    unset DATABASE_USER
    unset DATABASE_PASSWORD
}

db_startup() {
    echo "Starting postgres container..."
    docker rm -f codebloom-db >/dev/null 2>&1 || true
    docker run -d \
        --name codebloom-db \
        -e POSTGRES_USER=postgres \
        -e POSTGRES_PASSWORD=postgres \
        -e POSTGRES_DB=codebloom \
        -p 5440:5432 \
        postgres:16

    echo "Waiting for postgres to become ready."
    for i in {1..30}; do
        if docker exec codebloom-db pg_isready -U postgres >/dev/null 2>&1; then
            echo "postgres is ready!"
            break
        fi
        echo "Waiting for postgres, sleep 2... ($i/30)"
        sleep 2
    done

    if ! docker exec codebloom-db pg_isready -U postgres >/dev/null 2>&1; then
        echo "postgres failed to start in time."
        docker logs codebloom-db || true
        exit 1
    fi

    export DATABASE_HOST=localhost
    export DATABASE_PORT=5440
    export DATABASE_NAME=codebloom
    export DATABASE_USER=postgres
    export DATABASE_PASSWORD=postgres

    echo "postgres ready. migrating now..."
    ./mvnw flyway:migrate -Dflyway.locations=filesystem:./db
    echo "postgres migration complete"

}
