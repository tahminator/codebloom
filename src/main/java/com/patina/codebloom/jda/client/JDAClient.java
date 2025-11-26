package com.patina.codebloom.jda.client;

import com.patina.codebloom.jda.JDAInitializer;
import com.patina.codebloom.jda.client.options.EmbeddedImagesMessageOptions;
import com.patina.codebloom.jda.client.options.EmbeddedMessageOptions;
import com.patina.codebloom.jda.properties.patina.JDAPatinaProperties;
import com.patina.codebloom.jda.properties.reporting.JDAErrorReportingProperties;
import com.patina.codebloom.jda.properties.reporting.JDALogReportingProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.FileUpload;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Use this client to interface with any required Discord bot logic.
 */
@Component
@Slf4j
@EnableConfigurationProperties(
    {
        JDAPatinaProperties.class,
        JDAErrorReportingProperties.class,
        JDALogReportingProperties.class,
    }
)
public class JDAClient {

    private final JDAInitializer jdaInitializer;
    private JDA jda;

    @Getter
    private final JDAPatinaProperties jdaPatinaProperties;

    @Getter
    private final JDAErrorReportingProperties jdaErrorReportingProperties;

    @Getter
    private final JDALogReportingProperties jdaLogReportingProperties;

    JDAClient(
        final JDAInitializer jdaInitializer,
        final JDAPatinaProperties jdaPatinaProperties,
        final JDAErrorReportingProperties jdaReportingProperties,
        final JDALogReportingProperties jdaLogReportingProperties
    ) {
        this.jdaInitializer = jdaInitializer;
        this.jdaPatinaProperties = jdaPatinaProperties;
        this.jdaErrorReportingProperties = jdaReportingProperties;
        this.jdaLogReportingProperties = jdaLogReportingProperties;
    }

    private void isJdaReadyOrThrow() {
        if (jda == null) {
            throw new RuntimeException("You must call connect() first.");
        }

        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(
                "Something went wrong when awaiting JDA",
                e
            );
        }
    }

    /**
     * Initializes the JDAClient. Returns the client object on completion.
     */
    public JDAClient connect() {
        try {
            jda = jdaInitializer.initializeJda();
            return this;
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to initialize JDA client", e);
        }
    }

    public List<Guild> getGuilds() {
        isJdaReadyOrThrow();
        return jda.getGuilds();
    }

    public Guild getGuildById(final long guildId) {
        String guildIdString = String.valueOf(guildId);
        isJdaReadyOrThrow();
        return jda
            .getGuilds()
            .stream()
            .filter(g -> g.getId().equals(guildIdString))
            .findFirst()
            .orElse(null);
    }

    public List<Member> getMemberListByGuildId(final String guildId) {
        isJdaReadyOrThrow();
        List<Guild> guilds = jda.getGuilds();

        Optional<Guild> optionalGuild = guilds
            .stream()
            .filter(g -> g.getId().equals(guildId))
            .findFirst();

        if (optionalGuild.isEmpty()) {
            return List.of();
        }

        return optionalGuild.get().getMembers();
    }

    /**
     * Send a Rich Embed message with a file to the guild ID and channel ID of your
     * choosing.
     *
     * Check EmbeddedMessageOptions for details on what is supported.
     */
    public void sendEmbedWithImage(final EmbeddedMessageOptions options) {
        isJdaReadyOrThrow();
        Guild guild = getGuildById(options.getGuildId());
        if (guild == null) {
            log.error("Guild does not exist.");
            return;
        }
        TextChannel channel = guild.getTextChannelById(options.getChannelId());
        if (channel == null) {
            log.error("Channel does not exist on the given guild.");
            return;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder()
            .setTitle(options.getTitle())
            .setDescription(options.getDescription())
            .setFooter(options.getFooterText(), options.getFooterIcon())
            .setColor(options.getColor());

        if (
            options.getFileName().endsWith(".png") ||
            options.getFileName().endsWith(".jpg")
        ) {
            embedBuilder.setImage(
                String.format("attachment://%s", options.getFileName())
            );
        }

        MessageEmbed embed = embedBuilder.build();

        log.info("Message has been built, ready to send...");

        channel
            .sendFiles(
                FileUpload.fromData(
                    options.getFileBytes(),
                    options.getFileName()
                )
            )
            .setEmbeds(embed)
            .queue();

        log.info("Message has been queued");
    }

    public void sendEmbedWithImages(
        final EmbeddedImagesMessageOptions options
    ) {
        isJdaReadyOrThrow();
        Guild guild = getGuildById(options.getGuildId());
        if (guild == null) {
            log.error("Guild does not exist.");
            return;
        }
        TextChannel channel = guild.getTextChannelById(options.getChannelId());
        if (channel == null) {
            log.error("Channel does not exist on the given guild.");
            return;
        }

        List<byte[]> filesBytes = options.getFilesBytes();
        List<String> fileNames = options.getFileNames();
        if (filesBytes == null || filesBytes.isEmpty()) {
            log.error("Files must be provided and non-empty.");
            return;
        }
        if (fileNames == null || fileNames.isEmpty()) {
            log.error("File names must be provided and non-empty.");
            return;
        }

        List<FileUpload> uploads = new ArrayList<>();
        for (int i = 0; i < filesBytes.size(); i++) {
            String name = (fileNames != null && i < fileNames.size())
                ? fileNames.get(i)
                : "image" + i + ".png";
            uploads.add(FileUpload.fromData(filesBytes.get(i), name));
        }

        List<MessageEmbed> embeds = new ArrayList<>();
        EmbedBuilder firstEmbed = new EmbedBuilder()
            .setColor(options.getColor())
            .setTitle(options.getTitle())
            .setUrl("https://codebloom.patinanetwork.org")
            .setDescription(options.getDescription())
            .setFooter(options.getFooterText(), options.getFooterIcon())
            .setImage("attachment://" + fileNames.get(0));

        embeds.add(firstEmbed.build());
        for (int i = 1; i < fileNames.size(); i++) {
            EmbedBuilder additionalEmbed = new EmbedBuilder()
                .setUrl("https://codebloom.patinanetwork.org")
                .setImage("attachment://" + fileNames.get(i));
            embeds.add(additionalEmbed.build());
        }

        channel.sendMessageEmbeds(embeds).setFiles(uploads).queue();
    }
}
