package org.patinanetwork.codebloom.jda;

import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.patinanetwork.codebloom.jda.properties.JDAProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Please use {@link org.patinanetwork.codebloom.jda.client.JDAClient JDAClient} if you intend to utilize/consume the
 * Discord bot.
 */
@Component
@EnableConfigurationProperties(JDAProperties.class)
public class JDAInitializer {

    @Getter
    private final JDAProperties jdaProperties;

    private final JDAEventListener jdaEventListener;

    public JDAInitializer(final JDAProperties jdaProperties, final JDAEventListener jdaEventListener) {
        this.jdaProperties = jdaProperties;
        this.jdaEventListener = jdaEventListener;
    }

    @Bean
    public JDA initializeJda() throws InterruptedException {
        final JDA jda = JDABuilder.createDefault(jdaProperties.getToken())
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .addEventListeners(jdaEventListener)
                .build();

        jda.awaitReady();

        return jda;
    }
}
