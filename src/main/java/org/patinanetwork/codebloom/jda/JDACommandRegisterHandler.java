package org.patinanetwork.codebloom.jda;

import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.patinanetwork.codebloom.jda.command.JDASlashCommand;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for registering (and de-registering) slash commands to the JDA client before it is ready.
 *
 * <p>Their implementation can be found in {@code JDASlashCommandHandler}
 */
@Component
public class JDACommandRegisterHandler extends ListenerAdapter {

    @Override
    public void onReady(final ReadyEvent event) {
        for (var guild : event.getJDA().getGuilds()) {
            var commands = JDASlashCommand.list().stream()
                    .map(c -> Commands.slash(c.getCommand(), c.getDescription()))
                    .toArray(SlashCommandData[]::new);

            guild.updateCommands().addCommands(commands).queue();
        }
    }
}
