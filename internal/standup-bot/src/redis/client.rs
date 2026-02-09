use chrono::{
    DateTime,
    Utc,
};
use redis::{
    AsyncCommands,
    aio::ConnectionManager,
};
use std::sync::OnceLock;

use crate::redis::{
    credentials::RedisCredentials,
    error::RedisClientError,
};

static CONN: OnceLock<ConnectionManager> = OnceLock::new();

pub async fn init(creds: &RedisCredentials) -> Result<(), RedisClientError> {
    let client = redis::Client::open(creds.redis_uri.clone())?;
    let man = client.get_connection_manager().await?;

    match CONN.set(man) {
        Ok(_) => (),
        Err(_) => println!("Attempted to save Redis CONN more than once"),
    }

    Ok(())
}

async fn get_conn() -> ConnectionManager {
    CONN.get().expect("Redis not initialized").clone()
}

pub async fn set_last_standup(time: DateTime<Utc>) -> Result<(), RedisClientError> {
    let mut conn = get_conn().await;

    Ok(conn.set("standup", time.to_string()).await?)
}

pub async fn get_last_standup() -> Result<Option<DateTime<Utc>>, RedisClientError> {
    let mut conn = get_conn().await;

    let value: Option<String> = conn.get("standup").await?;

    value
        .map(|v| v.parse::<DateTime<Utc>>())
        .transpose()
        .map_err(RedisClientError::from)
}
