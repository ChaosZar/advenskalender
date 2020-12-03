package org.chaos.advenskalender.discord;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.MessageCreateSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;


@Slf4j
@Service
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_SINGLETON)
@RequiredArgsConstructor
public class Client {

    public static final String EMOJI_A = "\uD83C\uDDE6";
    public static final String EMOJI_B = "\uD83C\uDDE7";
    public static final String EMOJI_C = "\uD83C\uDDE8";
    public static final String EMOJI_D = "\uD83C\uDDE9";
    public static final String EMOJI_UNKNOWING = "\uD83E\uDD37";

    private final DiscordProperties discordProperties;
    private final DiscordClient clientBuilder;

    public Mono<Message> sendFile(GatewayDiscordClient discordClient, Path path) {
        log.info("send file {}", path);
        return getTextChannel(discordClient)
                .flatMap(channel -> channel.createMessage(a -> createFileMessage(path, a)));
    }

    public Mono<Void> addEmoji(Message message, String emoji) {
        return message.addReaction(ReactionEmoji.unicode(emoji));
    }

    private Mono<TextChannel> getTextChannel(GatewayDiscordClient discordClient) {
        return discordClient.getChannelById(Snowflake.of(discordProperties.getChannelId()))
                .map(TextChannel.class::cast);
    }

    private void createFileMessage(Path path, MessageCreateSpec a) {
        try {
            log.info("send file {}", path);
            InputStream fileInputStream = Files.newInputStream(path);
            String fileName = path.getFileName().toString();
            a.addFile(fileName, fileInputStream);
        } catch (IOException e) {
            log.error("failed to send File Message.", e);
        }
    }


    public Mono<GatewayDiscordClient> buildGatewayDiscordClient() {
        return clientBuilder.login();
    }
}

