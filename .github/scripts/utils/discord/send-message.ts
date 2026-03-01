import { REST, Routes } from "discord.js";

export async function sendDiscordMessage(
  botToken: string,
  channelId: string,
  message: string,
): Promise<void> {
  const rest = new REST().setToken(botToken);

  await rest.post(Routes.channelMessages(channelId), {
    body: {
      content: message,
    },
  });
}
