FROM rust:1-alpine AS build

WORKDIR /app

COPY internal/standup-bot/Cargo.toml internal/standup-bot/Cargo.lock ./ 
COPY internal/standup-bot/src ./src
RUN cargo build --release -v

FROM alpine:3.23.3

WORKDIR /app

COPY --from=build /app/target/release/codebloom-standup-bot ./

ENTRYPOINT ["./codebloom-standup-bot"]
