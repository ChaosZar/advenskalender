package org.chaos.advenskalender.calendar.answer;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.MessageChannel;
import org.chaos.advenskalender.discord.Client;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@Import({Client.class, AnswerRepository.class})
class PrivateAnswerServiceTest {

    @Autowired
    private PrivateAnswerService privateAnswerService;

    @Test
    private void onNewMessage(){
        MessageChannel channel = mock(MessageChannel.class);
        when(channel.getType()).thenReturn(Channel.Type.GUILD_TEXT);


        Message message = mock(Message.class);
        when(message.getChannel()).thenReturn(Mono.just(channel));


        MessageCreateEvent messageCreateEvent = mock(MessageCreateEvent.class);
        when(messageCreateEvent.getMessage()).thenReturn(message);

        privateAnswerService.onNewMessage(messageCreateEvent);
    }

    @Configuration
    static class TestConfiguration {

    }

}