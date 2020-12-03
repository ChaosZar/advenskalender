package org.chaos.advenskalender.discord;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class DiscordProperties {

    private final String channelId;
    private final String clientToken;

    public DiscordProperties(@Value("${discord.channel.id}") String channelId, @Value("${discord.client.token}") String clientToken) {
        this.channelId = channelId;
        this.clientToken = clientToken;
    }
}
