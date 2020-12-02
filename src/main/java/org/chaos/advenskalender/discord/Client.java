package org.chaos.advenskalender.discord;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.MessageCreateSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;


@Service
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Client {

    public static final String EMOJI_A = "\uD83C\uDDE6";
    public static final String EMOJI_B = "\uD83C\uDDE7";
    public static final String EMOJI_C = "\uD83C\uDDE8";
    public static final String EMOJI_D = "\uD83C\uDDE9";
    public static final String EMOJI_UNKNOWING = "\uD83E\uDD37";

    @Value("${discord.channel.id}")
    private String channelId;

    @Value("${discord.client.token}")
    private String clientToken;

    private Logger logger = LoggerFactory.getLogger(Client.class);

    public Mono<Message> sendFile(Path path) {
        logger.debug("send file {}", path);
        return getTextChannel()
                .flatMap(channel -> channel.createMessage(a -> createFileMessage(path, a)));
    }

    public Mono<Void> addEmoji(Message message, String emoji){
        return message.addReaction(ReactionEmoji.unicode(emoji));
    }

    public Mono<Message> sendText(String message) {
        logger.debug("send message {}", message);
        return getTextChannel()
                .flatMap(channel -> channel.createMessage(message));
    }

    private Mono<TextChannel> getTextChannel() {
        return buildClient()
                .flatMap(client -> client.getChannelById(Snowflake.of(channelId)))
                .map(TextChannel.class::cast);
    }

    private void createFileMessage(Path path, MessageCreateSpec a) {
        try {
            logger.debug("send file {}", path);
            InputStream fileInputStream = Files.newInputStream(path);
            String fileName = path.getFileName().toString();
            a.addFile(fileName, fileInputStream);
        } catch (IOException e) {
            logger.error("failed to send File Message.", e);
        }
    }


    private Mono<GatewayDiscordClient> buildClient() {
        DiscordClient clientBuilder = DiscordClient.create(clientToken);
        return clientBuilder.login();
    }
}

