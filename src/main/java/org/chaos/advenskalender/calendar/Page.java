package org.chaos.advenskalender.calendar;

import lombok.Data;

import java.nio.file.Path;

@Data
public class Page {
    private final Path path;

    Page(Path path) {
        this.path = path;
    }
}
