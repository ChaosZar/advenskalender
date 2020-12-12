package org.chaos.advenskalender.calendar.book;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.MessageCreateSpec;
import org.chaos.advenskalender.discord.Client;
import org.chaos.advenskalender.discord.DiscordProperties;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import({CalendarService.class, Client.class, DiscordProperties.class, BookEventPublisher.class})
class AdvenskalenderApplicationTests {

    private static final MessageCreateSpec MESSAGE_CREATE_SPEC = mock(MessageCreateSpec.class);

    @Autowired
    private CalendarService calendarService;

    @Test
    void contextLoads() {
        var nameCaptor = ArgumentCaptor.forClass(String.class);
        var fileCaptor = ArgumentCaptor.forClass(InputStream.class);

        StepVerifier.create(calendarService.postPages())
                .expectNext("A")
                .expectNext("B")
                .expectNext("C")
                .expectNext("D")
                .expectNext("UNKNOWING")
                .expectComplete()
                .verify();

        verify(MESSAGE_CREATE_SPEC, times(2)).addFile(nameCaptor.capture(), fileCaptor.capture());

        assertThat(nameCaptor.getAllValues()).isEqualTo(List.of("1.txt", "2.txt"));
        assertThat(fileCaptor.getAllValues().stream().map(this::readFileFromInputStream).collect(Collectors.toList()))
                .isEqualTo(List.of("content aus 1", "content aus 2"));
    }

    private String readFileFromInputStream(InputStream inputStream) {
        try {
            return new String(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Configuration
    static class TestConfiguration {

        @Bean
        CalendarProperties calendarProperties() throws Exception {
            var calendarProperties = mock(CalendarProperties.class);
            when(calendarProperties.getFilesRoot()).thenReturn(getClass().getResource("root").toURI());
            return calendarProperties;
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
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

            when(textChannel.createMessage(any(Consumer.class))).then(invocation -> {
                ((Consumer)invocation.getArgument(0)).accept(MESSAGE_CREATE_SPEC);
                return Mono.just(message);
            });

            var gatewayDiscordClient = mock(GatewayDiscordClient.class);
            when(gatewayDiscordClient.getChannelById(Snowflake.of("12345"))).thenReturn(Mono.just(textChannel));

            var discordClient = mock(DiscordClient.class);
            when(discordClient.login()).thenReturn(Mono.just(gatewayDiscordClient));

            return discordClient;
        }

    }

}
