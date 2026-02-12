package org.patinanetwork.codebloom.jda;

import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
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
            JDASlashCommand.list().stream().forEach(c -> {
                guild.updateCommands().queue();
                guild.upsertCommand(c.getCommand(), c.getDescription()).queue();
            });
        }
    }
}
