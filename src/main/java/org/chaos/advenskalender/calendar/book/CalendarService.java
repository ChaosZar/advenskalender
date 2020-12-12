package org.chaos.advenskalender.calendar.book;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chaos.advenskalender.discord.Client;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class CalendarService {

    private final BookEventPublisher bookEventPublisher;
    private final Client client;
    private final CalendarProperties calendarProperties;
    private Book book;

    @PostConstruct
    public void init() {
        book = Book.build(calendarProperties.getFilesRoot());
    }

    @Scheduled(cron = "0 0 10-20 * * *")
    public void postPagesScheduled() {
        log.info("cron job triggered.");
        postPages()
                .subscribe(
                        emoji -> log.info("emoji {} was sent", emoji),
                        error -> log.error("error sending page", error)
                );
    }

    public Flux<String> postPages() {
        List<Day> daysToPost = getDaysToPost();
        log.info("Following Days will be send: {}", daysToPost);
        return client.buildGatewayDiscordClient()
                .flatMapIterable(discordClient -> daysToPost.stream()
                        .flatMap(s -> s.getPages().stream())
                        .map(p -> new ClientPagePair(discordClient, p))
                        .collect(Collectors.toList()))
                .doFirst(() -> publichPrePostEvent(daysToPost))
                .flatMap(clientPagePair -> sendPage(clientPagePair.discordClient(), clientPagePair.page()))
                .doOnComplete(() -> {
                    publishPostPostEvent(daysToPost);
                    book.deleteDays(daysToPost);
                });
    }

    private void publichPrePostEvent(List<Day> daysToPost) {
        if (!daysToPost.isEmpty()) {
            bookEventPublisher.publish(new PostPostPagesEvent(this));
        }
    }

    private void publishPostPostEvent(List<Day> daysToPost) {
        if (!daysToPost.isEmpty()) {
            bookEventPublisher.publish(new PostPostPagesEvent(this));
        }
    }

    private Flux<String> sendPage(GatewayDiscordClient discordClient, Page page) {
        return client.sendFile(discordClient, page.getPath())
                .doOnNext(message -> log.info("file {} was successfully sent", page))
                .filter(message -> page.isLastPage())
                .flatMapMany(this::createEmojis);
    }

    private Flux<String> createEmojis(Message message) {
        log.info("creating emojis");
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

    private static record ClientPagePair(GatewayDiscordClient discordClient, Page page) {
    }

}
