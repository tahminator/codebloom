use bb8_redis::{
    RedisConnectionManager,
    bb8::{
        self,
        Pool,
        PooledConnection,
    },
    redis::AsyncCommands,
};
use chrono::{
    DateTime,
    Utc,
};
use std::sync::OnceLock;

use crate::redis::{
    credentials::RedisCredentials,
    error::{
        RedisClientError,
        RedisSingletonEmptyError,
    },
};

static INSTANCE: OnceLock<Pool<RedisConnectionManager>> = OnceLock::new();

pub async fn init(creds: &RedisCredentials) -> Result<(), RedisClientError> {
    let manager = RedisConnectionManager::new(creds.redis_uri.as_str())?;
    let pool = bb8::Pool::builder().build(manager).await?;

    match INSTANCE.set(pool) {
        Ok(_) => (),
        Err(_) => println!("Attempted to save Redis INSTANCE more than once"),
    }

    Ok(())
}

async fn get_pool() -> Result<PooledConnection<'static, RedisConnectionManager>, RedisClientError> {
    match INSTANCE.get() {
        Some(pool) => pool.get().await.map_err(RedisClientError::from),
        None => Err(RedisSingletonEmptyError.into()),
    }
}

pub async fn set_last_standup(time: DateTime<Utc>) -> Result<(), RedisClientError> {
    let mut conn = get_pool().await?;

    Ok(conn.set("standup", time.to_string()).await?)
}

pub async fn get_last_standup() -> Result<Option<DateTime<Utc>>, RedisClientError> {
    let mut conn = get_pool().await?;

    let value: Option<String> = conn.get("standup").await?;

    value
        .map(|v| v.parse::<DateTime<Utc>>())
        .transpose()
        .map_err(RedisClientError::from)
}
