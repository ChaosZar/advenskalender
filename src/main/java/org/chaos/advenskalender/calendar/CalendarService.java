package org.chaos.advenskalender.calendar;

import discord4j.core.object.entity.Message;
import org.chaos.advenskalender.discord.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CalendarService {

    Logger logger = LoggerFactory.getLogger(CalendarService.class);

    private final Client client;
    private final Book book;

    CalendarService(final Client client, @Value("${calendar.files.root}") URI filesRoot) {
        this.client = client;
        this.book = Book.build(filesRoot);
    }

    @Scheduled(cron = "0 0 * * * *")
    public void postPages() {
        logger.info("cron job triggered.");

        if (isNighttime()) {
            logger.info("nighttime detected. job skipped.");
            return;
        }

        getFluxes().subscribe(
                emoji -> logger.debug("emoji {} was sent", emoji),
                error -> logger.error("error sending page", error)
        );
    }

    Flux<String> getFluxes() {List<Day> daysToPost = getDaysToPost();
        logger.info("Following Days will be send: {}", daysToPost);
        return Flux.fromIterable(daysToPost.stream()
                .flatMap(s -> s.getPages().stream())
                .collect(Collectors.toList()))
                .flatMap(this::sendPage)
                .doOnComplete(() -> book.deleteDays(daysToPost));
    }

    private Flux<String> sendPage(Page page) {
        logger.debug("sending page {} to discord", page.getPath().getFileName());
        return client.sendFile(page.getPath())
                .doOnNext(message -> logger.debug("file {} was successfully sent", page.getPath().getFileName()))
                .filter(message -> page.isLastPage())
                .flatMapMany(this::createEmojis);
    }

    private Flux<String> createEmojis(Message message) {
        return Flux.concat(
                client.addEmoji(message, Client.EMOJI_A).then(Mono.just("A")),
                client.addEmoji(message, Client.EMOJI_B).then(Mono.just("B")),
                client.addEmoji(message, Client.EMOJI_C).then(Mono.just("C")),
                client.addEmoji(message, Client.EMOJI_D).then(Mono.just("D")),
                client.addEmoji(message, Client.EMOJI_UNKNOWING).then(Mono.just("UNKNOWING"))
        );
    }

    private boolean isNighttime() {
        LocalDateTime now = LocalDateTime.now();
        return now.getHour() > 20 || now.getHour() < 10;
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


}
