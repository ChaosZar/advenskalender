package org.chaos.advenskalender.calendar;

import lombok.Data;

import java.nio.file.Path;
import java.util.TreeSet;

@Data
public class Day {

    private final int dayOfMonth;
    private final TreeSet<Page> pages = new TreeSet<>();


    public Day(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public void add(Path page) {
        pages.add(new Page(page));
    }

    public void determineLastPage() {
        pages.last().setLastPage();
    }
}
