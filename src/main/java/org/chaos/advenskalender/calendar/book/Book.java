package org.chaos.advenskalender.calendar.book;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
public class Book {

    private final List<Day> days = new ArrayList<>();

    public static Book build(final URI filesRoot) {

        Book result = new Book();

        Path path = Paths.get(filesRoot);
        try {
            Files.walkFileTree(path, new FileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String day = file.getParent().getFileName().toString();
                    result.addPage(Integer.valueOf(day), file);

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        result.onLoadingFinished();

        return result;
    }

    private void onLoadingFinished() {
        days.forEach(Day::determineLastPage);
    }

    private void addPage(Integer day, Path page) {
        getDay(day).add(page);
    }


    private Day getDay(final int dayOfMonth) {
        return days.stream()
                .filter(d -> d.getDayOfMonth() == dayOfMonth)
                .findFirst()
                .orElseGet(() -> {
                    Day newDay = new Day(dayOfMonth);
                    days.add(newDay);
                    return newDay;
                });
    }

    public List<Day> getDays() {
        return days;
    }

    public void deleteDays(List<Day> days) {
        days.stream()
                .flatMap(s -> s.getPages().stream())
                .forEach(path -> {
                    try {
                        log.info("file will be deleted: {}", path);
                        Files.delete(path.getPath());
                    } catch (IOException e) {
                        log.error("failed to delete file: {}", path, e);
                    }
                });
        this.days.removeAll(days);
    }
}
