use std::env;

#[derive(Clone)]
pub struct DiscordCredentials {
    #[allow(dead_code)]
    pub client_id: u64,
    #[allow(dead_code)]
    pub client_secret: String,
    pub token: String,
    pub guild_id: u64,
    pub channel_id: u64,
}

pub fn get_discord_credentials() -> DiscordCredentials {
    DiscordCredentials {
        client_id: env::var("DISCORD_CLIENT_ID")
            .expect("DISCORD_CLIENT_ID is missing from environment")
            .parse()
            .expect("DISCORD_CLIENT_ID is not an integer type"),
        client_secret: env::var("DISCORD_CLIENT_SECRET")
            .expect("DISCORD_CLIENT_SECRET is missing from environment"),
        token: env::var("DISCORD_TOKEN").expect("DISCORD_TOKEN is missing from environment"),
        guild_id: env::var("DISCORD_GUILD_ID")
            .expect("DISCORD_GUILD_ID is missing from environment")
            .parse()
            .expect("DISCORD_GUILD_ID is not an integer type"),
        channel_id: env::var("DISCORD_CHANNEL_ID")
            .expect("DISCORD_CHANNEL_ID is missing from environment")
            .parse()
            .expect("DISCORD_CHANNEL_ID is not an integer type"),
    }
}
