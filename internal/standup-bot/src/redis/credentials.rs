use std::env;

pub struct RedisCredentials {
    pub redis_uri: String,
}

pub fn get_redis_credentials() -> RedisCredentials {
    RedisCredentials {
        redis_uri: env::var("REDIS_URI").expect("REDIS_URI is missing from environment"),
    }
}
