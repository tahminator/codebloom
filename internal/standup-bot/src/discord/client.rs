use std::sync::{
    Arc,
    OnceLock,
};

use serenity::{
    Client,
    Error,
    all::{
        ChannelId,
        Colour,
        CreateEmbed,
        CreateEmbedFooter,
        CreateMessage,
        CreateThread,
        GatewayIntents,
    },
    http::Http,
};

use crate::discord::{
    credentials::DiscordCredentials,
    handlers::Handler,
};

const INTENTS: GatewayIntents = GatewayIntents::GUILD_MEMBERS;
static HTTP: OnceLock<Arc<Http>> = OnceLock::new();

pub async fn init_in_bg(discord_creds: &DiscordCredentials) -> Result<(), Error> {
    let mut client = Client::builder(&discord_creds.token, INTENTS)
        .event_handler(Handler)
        .await?;

    let http = client.http.clone();
    let _ = HTTP
        .set(http)
        .inspect_err(|_| println!("Attempted to save Discord HTTP client more than once"));

    tokio::spawn(async move {
        if let Err(e) = client.start().await {
            println!("Client error: {e:?}");
        }
    });

    Ok(())
}

pub fn get_http() -> Arc<Http> {
    HTTP.get().expect("Discord not initialized").clone()
}

pub async fn send_standup_message(discord_creds: &DiscordCredentials) -> Result<(), Error> {
    println!("Sending standup message!");

    let embed = CreateEmbed::new()
        .title("Codebloom Standup")
        .description(
            "<@&1391944565409316944> Standup time! Please leave an update about your latest progress inside of the thread.",
        )
        .footer(
            CreateEmbedFooter::new("Codebloom - Internal")
                .icon_url("https://codebloom.patinanetwork.org/favicon.ico"),
        )
        .colour(Colour::from_rgb(69, 129, 103));
    let create_msg = CreateMessage::new().embed(embed);
    let channel = ChannelId::new(discord_creds.channel_id);

    let msg = channel
        .send_message(get_http().as_ref(), create_msg)
        .await
        .inspect_err(|e| println!("Error sending message: {e:#?}"))?;

    let thread_builder = CreateThread::new("Daily Standup Thread");

    channel
        .create_thread_from_message(get_http().as_ref(), msg.id, thread_builder)
        .await
        .inspect_err(|e| println!("Error creating thread: {e:#?}"))
        .map(|_| ())
}
