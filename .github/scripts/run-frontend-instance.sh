#!/usr/bin/env bash

frontend_cleanup() {
    echo "[INFO] ===== FRONTEND LOGS START ====="
    cat frontend.log || echo "[WARN] frontend.log not found"
    echo "[INFO] ===== FRONTEND LOGS END ====="
    if kill $(cat frontend_pid.txt) >/dev/null 2>&1; then
        echo "Frontend process killed successfully."
    else
        echo "Frontend was not running or already stopped."
    fi
}

frontend_startup() {
    corepack enable pnpm
    cd js
    pnpm i --frozen-lockfile

    echo "Generating API schema..."
    pnpm run generate

    if [ ! -f "src/lib/api/types/autogen/schema.ts" ]; then
        echo "Schema generation failed!"
        exit 1
    fi
    echo "Schema file generated successfully."

    pnpm run dev >../frontend.log 2>&1 &
    echo $! >../frontend_pid.txt
    cd ..

    frontend_started=false
    for i in {1..30}; do
        if curl -s http://localhost:5173/ | grep -q '<title>'; then
            echo "Frontend is up!"
            frontend_started=true
            break
        fi
        echo "Waiting for frontend... ($i/30)"
        sleep 2
    done

    if [ "$frontend_started" = false ]; then
        echo "Frontend failed to start in time."
        cat frontend.log || true
        exit 1
    fi
}