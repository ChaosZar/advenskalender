package org.chaos.advenskalender;

import discord4j.core.DiscordClient;
import org.chaos.advenskalender.discord.DiscordProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AdvenskalenderApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdvenskalenderApplication.class, args);
    }

    @Bean
    DiscordClient discordClient(DiscordProperties discordProperties) {
        return DiscordClient.create(discordProperties.getClientToken());
    }

}
