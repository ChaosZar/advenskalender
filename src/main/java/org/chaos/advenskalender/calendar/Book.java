package org.chaos.advenskalender.calendar;

import lombok.Data;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
public class Book {

    private final List<Day> days = new ArrayList<>();

    public static Book build(final String filesRoot) {

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
        days.stream().forEach(d -> d.determineLastPage());
    }

    private void addPage(Integer day, Path page) {
        getDay(day).add(page);
    }


    private Day getDay(final int dayOfMonth) {
        Optional<Day> day = days.stream().filter(d -> d.getDayOfMonth() == dayOfMonth).findFirst();
        if (day.isEmpty()) {
            Day newDay = new Day(dayOfMonth);
            days.add(newDay);
            return newDay;
        }
        return day.get();
    }

    public List<Day> getDays() {
        return days;
    }

    public void deleteDays(List<Day> days) {
        days.stream()
                .flatMap(s -> s.getPages().stream())
                .forEach(path -> {
                    try {
                        Files.delete(path.getPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        this.days.removeAll(days);
    }
}
