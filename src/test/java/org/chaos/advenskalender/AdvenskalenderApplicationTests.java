package org.chaos.advenskalender;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import org.chaos.advenskalender.calendar.CalendarProperties;
import org.chaos.advenskalender.calendar.CalendarService;
import org.chaos.advenskalender.discord.Client;
import org.chaos.advenskalender.discord.DiscordProperties;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@Import({CalendarService.class, Client.class, DiscordProperties.class})
class AdvenskalenderApplicationTests {

    @Autowired
    private CalendarService calendarService;

    @Test
    void contextLoads() throws Exception {
        StepVerifier.create(calendarService.postPages())
                .expectNext("A")
                .expectNext("B")
                .expectNext("C")
                .expectNext("D")
                .expectNext("UNKNOWING")
                .expectComplete()
                .verify();
    }

    @Configuration
    static class TestConfiguration {

        @Bean
        CalendarProperties calendarProperties() throws Exception {
            var calendarProperties = mock(CalendarProperties.class);
            when(calendarProperties.getFilesRoot()).thenReturn(getClass().getResource("calendar/root").toURI());
            return calendarProperties;
        }

        @Bean
        DiscordClient discordClient() {
            var message = mock(Message.class);
            when(message.getId()).thenReturn(Snowflake.of(234));
            when(message.getChannelId()).thenReturn(Snowflake.of(12345));
            when(message.addReaction(ReactionEmoji.unicode(Client.EMOJI_A))).thenReturn(Mono.just("A").then());
            when(message.addReaction(ReactionEmoji.unicode(Client.EMOJI_B))).thenReturn(Mono.just("B").then());
            when(message.addReaction(ReactionEmoji.unicode(Client.EMOJI_C))).thenReturn(Mono.just("C").then());
            when(message.addReaction(ReactionEmoji.unicode(Client.EMOJI_D))).thenReturn(Mono.just("D").then());
            when(message.addReaction(ReactionEmoji.unicode(Client.EMOJI_UNKNOWING))).thenReturn(Mono.just("UNKNOWING").then());

            var textChannel = mock(TextChannel.class);
            //noinspection unchecked
            when(textChannel.createMessage(any(Consumer.class))).thenReturn(Mono.just(message));

            var gatewayDiscordClient = mock(GatewayDiscordClient.class);
            when(gatewayDiscordClient.getChannelById(Snowflake.of("12345"))).thenReturn(Mono.just(textChannel));

            var discordClient = mock(DiscordClient.class);
            when(discordClient.login()).thenReturn(Mono.just(gatewayDiscordClient));

            return discordClient;
        }

    }

}
