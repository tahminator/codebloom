use bb8_redis::{
    bb8::RunError,
    redis::RedisError,
};
use chrono::ParseError;
use std::fmt;

#[derive(Debug, Clone)]
pub struct RedisSingletonEmptyError;

#[derive(Debug)]
#[allow(dead_code)]
pub enum RedisClientError {
    PooledRedisError(RunError<RedisError>),
    RedisError(RedisError),
    DateTimeParseError(ParseError),
    EmptyError(RedisSingletonEmptyError),
}

impl fmt::Display for RedisClientError {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            RedisClientError::PooledRedisError(e) => write!(f, "PooledRedisError: {:?}", e),
            RedisClientError::RedisError(e) => write!(f, "RedisError: {:?}", e),
            RedisClientError::DateTimeParseError(e) => write!(f, "DateTimeParse: {:?}", e),
            RedisClientError::EmptyError(_) => {
                write!(f, "Redis Singleton is empty")
            }
        }
    }
}

impl std::error::Error for RedisClientError {}

impl From<RunError<RedisError>> for RedisClientError {
    fn from(value: RunError<RedisError>) -> Self {
        RedisClientError::PooledRedisError(value)
    }
}

impl From<RedisError> for RedisClientError {
    fn from(value: RedisError) -> Self {
        RedisClientError::RedisError(value)
    }
}

impl From<ParseError> for RedisClientError {
    fn from(value: ParseError) -> Self {
        RedisClientError::DateTimeParseError(value)
    }
}

impl From<RedisSingletonEmptyError> for RedisClientError {
    fn from(value: RedisSingletonEmptyError) -> Self {
        RedisClientError::EmptyError(value)
    }
}
