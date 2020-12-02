package org.chaos.advenskalender.discord;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.spec.MessageCreateSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;


@Service
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Client {

    //CAHNNEL ID GOES HERE!
    public static final long CHANNEL_ID_LIVE = 0L;
    Logger logger = LoggerFactory.getLogger(Client.class);

    private GatewayDiscordClient client;

    @PostConstruct
    public void init() {
        client = buildClient();
    }

    public void sendFile(Path path) {
        TextChannel channel = getTextChannel();

        channel.createMessage(a -> createFileMessage(path, a)).block();
    }

    public void sendText(String message) {
        logger.debug("send message {}", message);
        TextChannel channel = getTextChannel();
        channel.createMessage(message).block();
    }

    private TextChannel getTextChannel() {
        return client
                .getChannelById(Snowflake.of(CHANNEL_ID_LIVE))
                .cast(TextChannel.class)
                .block();
    }

    private void createFileMessage(Path path, MessageCreateSpec a) {
        try {
            InputStream fileInputStream = Files.newInputStream(path);
            String fileName = path.getFileName().toString();
            a.addFile(fileName, fileInputStream);
        } catch (IOException e) {
            logger.error("failed to send File Message.", e);
        }
    }


    private GatewayDiscordClient buildClient() {
        DiscordClient clientBuilder = DiscordClient.create("BOT CLIENT TOKEN GOES HERE!");
        return clientBuilder.login().block();
    }
}
