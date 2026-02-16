use chrono::ParseError;
use redis::RedisError;
use std::fmt;

#[derive(Debug)]
#[allow(dead_code)]
pub enum RedisClientError {
    Redis(RedisError),
    DateTimeParse(ParseError),
}

impl fmt::Display for RedisClientError {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            RedisClientError::Redis(e) => {
                write!(f, "RedisError: {:?}", e)?;
                write!(f, "\n  Category: {:?}", e.category())?;
                write!(f, "\n  Kind: {:?}", e.kind())?;
                write!(
                    f,
                    "\n  is_connection_dropped: {}",
                    e.is_connection_dropped()
                )?;
                write!(f, "\n  is_timeout: {}", e.is_timeout())?;
                write!(
                    f,
                    "\n  is_connection_refusal: {}",
                    e.is_connection_refusal()
                )?;
                write!(f, "\n  is_io_error: {}", e.is_io_error())?;
                if let Some(detail) = e.detail() {
                    write!(f, "\n  Detail: {}", detail)?;
                }
                Ok(())
            }
            RedisClientError::DateTimeParse(e) => write!(f, "DateTimeParse: {:?}", e),
        }
    }
}

impl std::error::Error for RedisClientError {}

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
