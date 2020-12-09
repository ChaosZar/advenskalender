package org.chaos.advenskalender.calendar;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.extern.slf4j.Slf4j;
import org.chaos.advenskalender.discord.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Slf4j
public class PrivateAnswerService {

    @Autowired
    private Client discordClient;

    @PostConstruct
    private void init() {
        GatewayDiscordClient client = discordClient.buildGatewayDiscordClient().block();
        client.on(MessageCreateEvent.class).subscribe(this::onNewMessage);

    }

    private void onNewMessage(MessageCreateEvent e) {
        Message message = e.getMessage();
        MessageChannel channel = message.getChannel().block();
        if (channel.getType() != Channel.Type.DM) {
            return;
        }

        Long memberId = message.getAuthorAsMember().block().getId().asLong();


    }


}
