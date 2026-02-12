use chrono::{
    DateTime,
    Utc,
};
use redis::{
    AsyncCommands,
    IntoConnectionInfo,
    aio::ConnectionManager,
    io::tcp::{
        TcpSettings,
        socket2,
    },
};
use std::{
    sync::OnceLock,
    time::Duration,
};

use crate::redis::{
    credentials::RedisCredentials,
    error::RedisClientError,
};

static CONN: OnceLock<ConnectionManager> = OnceLock::new();

pub async fn init(creds: &RedisCredentials) -> Result<(), RedisClientError> {
    let keepalive = socket2::TcpKeepalive::new()
        .with_time(Duration::from_secs(60))
        .with_interval(Duration::from_secs(10));
    let tcp_settings = TcpSettings::default()
        .set_nodelay(true)
        .set_keepalive(keepalive);

    let info = creds
        .redis_uri
        .clone()
        .into_connection_info()
        .expect("Invalid URI")
        .set_tcp_settings(tcp_settings);

    let client = redis::Client::open(info)?;
    let man = client.get_connection_manager().await?;

    match CONN.set(man) {
        Ok(_) => (),
        Err(_) => println!("Attempted to save Redis CONN more than once"),
    }

    Ok(())
}

fn get_conn() -> ConnectionManager {
    CONN.get().expect("Redis not initialized").clone()
}

pub async fn set_last_standup(time: DateTime<Utc>) -> Result<(), RedisClientError> {
    let mut conn = get_conn();

    Ok(conn.set("standup", time.to_string()).await?)
}

pub async fn get_last_standup() -> Result<Option<DateTime<Utc>>, RedisClientError> {
    let mut conn = get_conn();

    let value: Option<String> = conn.get("standup").await?;

    value
        .map(|v| v.parse::<DateTime<Utc>>())
        .transpose()
        .map_err(RedisClientError::from)
}
