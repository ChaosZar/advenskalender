package org.chaos.advenskalender.calendar;

import discord4j.core.object.entity.Message;
import org.chaos.advenskalender.discord.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CalendarService {

    @Value("${calendar.files.root}")
    private String filesRoot;
    private Book book;
    Logger logger = LoggerFactory.getLogger(CalendarService.class);

    @Autowired
    private Client client;

    @PostConstruct
    public void postConstruct() {
        book = Book.build(filesRoot);
    }

    @Scheduled(cron = "0 0 * * * *")
    public void postPages() {
        logger.info("cron job triggered.");

        if (isNighttime()) {
            logger.info("nighttime detected. job skipped.");
            return;
        }

        List<Day> daysToPost = getDaysToPost();
        logger.info("Following Days will be send: {}", daysToPost);
        daysToPost.stream()
                .flatMap(s -> s.getPages().stream())
                .forEachOrdered(this::sendPage);

        book.deleteDays(daysToPost);
    }

    private void sendPage(Page page) {
        Message message = client.sendFile(page.getPath());
        if(page.isLastPage()){
            client.addEmoji(message, Client.EMOJI_A);
            client.addEmoji(message, Client.EMOJI_B);
            client.addEmoji(message, Client.EMOJI_C);
            client.addEmoji(message, Client.EMOJI_D);
            client.addEmoji(message, Client.EMOJI_UNKNOWING);
        }
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
