package org.chaos.advenskalender.calendar;

import org.chaos.advenskalender.discord.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final String FILES_ROOT = "<PATH TO FILES GOES HERE>";
    private Book book;
    Logger logger = LoggerFactory.getLogger(CalendarService.class);

    @Autowired
    private Client client;

    @PostConstruct
    public void postConstruct() {
        book = Book.build(FILES_ROOT);
    }

    @Scheduled(cron = "0 0 * * * *")
    public void postPages() {
        logger.info("cron job triggered.");
        LocalDateTime now = LocalDateTime.now();
        if (now.getHour() > 20 || now.getHour() < 10) {
            logger.info("nighttime detected. job skipped.");
            return;
        }

        List<Day> daysToPost = getDaysToPost();
        logger.info("Following Days will be send: {}", daysToPost);
        daysToPost.stream()
                .flatMap(s -> s.getPages().stream())
                .sorted(Comparator.comparing(Page::getPath))
                .forEachOrdered(p -> client.sendFile(p.getPath()));

        book.deleteDays(daysToPost);
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
