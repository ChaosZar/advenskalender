package org.chaos.advenskalender.calendar;

import lombok.Data;

import java.nio.file.Path;

@Data
public class Page implements Comparable<Page> {
    private final Path path;
    private boolean lastPage = false;

    Page(Path path) {
        this.path = path;
    }

    @Override
    public int compareTo(Page o) {
        return path.compareTo(o.getPath());
    }

    public void setLastPage() {
        this.lastPage = true;
    }
}
