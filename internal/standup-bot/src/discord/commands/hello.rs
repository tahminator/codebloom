use serenity::{
    builder::CreateCommand,
    model::application::ResolvedOption,
};

pub fn run(_options: &[ResolvedOption]) -> String {
    "Hello from Codebloom via the Rust Standup Bot!".to_string()
}

pub fn register() -> CreateCommand {
    CreateCommand::new("hello").description("Test command")
}
