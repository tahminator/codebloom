use chrono::ParseError;
use redis::RedisError;

#[derive(Debug)]
#[allow(dead_code)]
pub enum RedisClientError {
    Redis(RedisError),
    DateTimeParse(ParseError),
}

impl From<RedisError> for RedisClientError {
    fn from(value: RedisError) -> Self {
        RedisClientError::Redis(value)
    }
}

impl From<ParseError> for RedisClientError {
    fn from(value: ParseError) -> Self {
        RedisClientError::DateTimeParse(value)
    }
}
