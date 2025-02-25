package com.patina.codebloom.jda;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

@Component
@EnableConfigurationProperties(JDAProperties.class)
public class JDAInitializer {
    private final JDAProperties jdaProperties;
    private final JDAEventListener jdaEventListener;

    public JDAInitializer(final JDAProperties jdaProperties, final JDAEventListener jdaEventListener) {
        this.jdaProperties = jdaProperties;
        this.jdaEventListener = jdaEventListener;
    }

    @Bean
    public JDA jda() throws Exception {
        final JDA jda = JDABuilder.createDefault(jdaProperties.getToken()).enableIntents(GatewayIntent.GUILD_MEMBERS)
                        .setChunkingFilter(ChunkingFilter.ALL).setMemberCachePolicy(MemberCachePolicy.ALL)
                        .addEventListeners(jdaEventListener).build();

        jda.awaitReady();

        return jda;
    }

    public JDAProperties getJdaProperties() {
        return jdaProperties;
    }

}
