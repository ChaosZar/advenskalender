package org.chaos.advenskalender.calendar;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Message;
import discord4j.discordjson.json.MessageData;
import discord4j.rest.RestClient;
import org.chaos.advenskalender.discord.Client;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;

import java.nio.file.Path;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CalendarServiceTest {

    @Test
    void shouldSendFiles() throws Exception {
        var client = mock(Client.class);
        var restClient = mock(RestClient.class);
        var gateway = mock(GatewayDiscordClient.class);
        when(gateway.getRestClient()).thenReturn(restClient);
        var messageData = mock(MessageData.class);
        when(messageData.id()).thenReturn("1");
        when(messageData.channelId()).thenReturn("5");
        var message = new Message(gateway, messageData);
        when(client.sendFile(Mockito.any(Path.class))).thenReturn(Mono.just(message));
        when(client.addEmoji(message, Client.EMOJI_A)).thenReturn(Mono.just("s").then());
        when(client.addEmoji(message, Client.EMOJI_B)).thenReturn(Mono.just("s").then());
        when(client.addEmoji(message, Client.EMOJI_C)).thenReturn(Mono.just("s").then());
        when(client.addEmoji(message, Client.EMOJI_D)).thenReturn(Mono.just("s").then());
        when(client.addEmoji(message, Client.EMOJI_UNKNOWING)).thenReturn(Mono.just("s").then());

        var calendarService = new CalendarService(client, getClass().getResource("root").toURI());
        calendarService.postPages();


    }

}