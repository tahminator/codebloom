backend_cleanup() {
    if kill $(cat spring_pid.txt) >/dev/null 2>&1; then
        echo "Backend process killed successfully."
    else
        echo "Backend was not running or already stopped."
    fi
}

backend_startup() {
    java -version
    javac -version
    echo "JAVA_HOME=$JAVA_HOME"

    ./mvnw -Dspring-boot.run.profiles=ci spring-boot:run >backend.log 2>&1 &
    echo $! >spring_pid.txt

    backend_started=false
    for i in {1..30}; do
        if curl -s http://localhost:8080/api | grep -q '"success":true'; then
            echo "Backend is up!"
            backend_started=true
            break
        fi
        echo "Waiting for backend... ($i/30)"
        sleep 2
    done

    if [ "$backend_started" = false ]; then
        echo "Backend failed to start in time."
        exit 1
    fi

}
