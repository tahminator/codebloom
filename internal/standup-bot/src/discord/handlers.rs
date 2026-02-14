use serenity::{
    all::{
        Command,
        Context,
        CreateInteractionResponse,
        CreateInteractionResponseMessage,
        EventHandler,
        GuildId,
        Interaction,
        Ready,
    },
    async_trait,
};

use crate::{
    discord::{
        commands,
        credentials,
    },
    utils::latch::base::{
        CountdownLatch,
        Latch as _,
    },
};

pub struct Handler {
    pub latch: CountdownLatch,
}

#[async_trait]
impl EventHandler for Handler {
    async fn interaction_create(&self, ctx: Context, interaction: Interaction) {
        if let Interaction::Command(command) = interaction {
            println!("Received command interaction: {command:#?}");

            let content = match command.data.name.as_str() {
                "hello" => Some(commands::hello::run(&command.data.options())),
                cmd => {
                    eprintln!("Command {cmd} not supported");
                    None
                }
            };

            if let Some(content) = content {
                let data = CreateInteractionResponseMessage::new().content(content);
                let builder = CreateInteractionResponse::Message(data);
                if let Err(why) = command.create_response(&ctx.http, builder).await {
                    eprintln!("Cannot respond to slash command: {why}");
                }
            }
        }
    }

    async fn ready(&self, ctx: Context, ready: Ready) {
        println!("{} is connected!", ready.user.name);

        let creds = credentials::get_discord_credentials();

        let commands = get_commands(creds.guild_id, ctx).await;
        println!("I now have the following guild slash commands: {commands:#?}");

        self.latch.count_down();
    }
}

pub async fn get_commands(guild_id: u64, ctx: Context) -> Result<Vec<Command>, serenity::Error> {
    let guild = GuildId::new(guild_id);
    return guild
        .set_commands(&ctx.http, vec![commands::hello::register()])
        .await;
}
