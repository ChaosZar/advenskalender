package org.chaos.advenskalender.calendar;

import lombok.Data;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Data
public class Day {

    private final int dayOfMonth;
    private final List<Page> pages = new ArrayList<>();


    public Day(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public void add(Path page) {
        pages.add(new Page(page));
    }
}
