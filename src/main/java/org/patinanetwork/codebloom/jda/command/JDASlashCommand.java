package org.patinanetwork.codebloom.jda.command;

import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum JDASlashCommand {
    LEADERBOARD("leaderboard", "Shows the current weekly leaderboard");

    private final String command;
    private final String description;

    public static List<JDASlashCommand> list() {
        return Arrays.asList(values());
    }

    public static JDASlashCommand fromCommand(String command) {
        return list().stream()
                .filter(c -> c.getCommand().equals(command))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalArgumentException("There is no JDACommand that maps to this command string"));
    }
}
