package org.chaos.advenskalender.calendar;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Message;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.chaos.advenskalender.discord.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarService {

    Logger logger = LoggerFactory.getLogger(CalendarService.class);

    private final Client client;
    private final CalendarProperties calendarProperties;
    private Book book;

    @PostConstruct
    public void init() {
        book = Book.build(calendarProperties.getFilesRoot());
    }

    @Scheduled(cron = "0 0 10-20 * * *")
    public void postPagesScheduled() {
        logger.info("cron job triggered.");

        postPages()
                .subscribe(
                        emoji -> logger.info("emoji {} was sent", emoji),
                        error -> logger.error("error sending page", error)
                );
    }

    public Flux<String> postPages() {
        List<Day> daysToPost = getDaysToPost();
        logger.info("Following Days will be send: {}", daysToPost);
        return client.buildGatewayDiscordClient()
                .flatMapIterable(discordClient -> daysToPost.stream()
                        .flatMap(s -> s.getPages().stream())
                        .map(p -> new ClientPagePair(discordClient, p))
                        .collect(Collectors.toList()))
                .flatMap(clientPagePair -> sendPage(clientPagePair.getDiscordClient(), clientPagePair.getPage()))
                .doOnComplete(() -> book.deleteDays(daysToPost));
    }

    private Flux<String> sendPage(GatewayDiscordClient discordClient,  Page page) {
        logger.info("sending page {} to discord", page);
        return client.sendFile(discordClient, page.getPath())
                .doOnNext(message -> logger.info("file {}/{} was successfully sent", page.getPath().getParent().getFileName(), page.getPath().getFileName()))
                .filter(message -> page.isLastPage())
                .flatMapMany(this::createEmojis);
    }

    private Flux<String> createEmojis(Message message) {
        logger.info("creating emojis");
        return Flux.concat(
                client.addEmoji(message, Client.EMOJI_A).then(Mono.just("A")),
                client.addEmoji(message, Client.EMOJI_B).then(Mono.just("B")),
                client.addEmoji(message, Client.EMOJI_C).then(Mono.just("C")),
                client.addEmoji(message, Client.EMOJI_D).then(Mono.just("D")),
                client.addEmoji(message, Client.EMOJI_UNKNOWING).then(Mono.just("UNKNOWING"))
        );
    }

    private List<Day> getDaysToPost() {
        LocalDateTime now = LocalDateTime.now();

        final int dayOfToday = now.getDayOfMonth();
        return book.getDays().stream()
                .filter(d -> now.getMonth() == Month.DECEMBER)
                .filter(d -> d.getDayOfMonth() <= dayOfToday)
                .sorted(Comparator.comparingInt(Day::getDayOfMonth))
                .collect(Collectors.toList());
    }

    @RequiredArgsConstructor
    @Getter
    private static class ClientPagePair {
        private final GatewayDiscordClient discordClient;
        private final Page page;
    }

}
