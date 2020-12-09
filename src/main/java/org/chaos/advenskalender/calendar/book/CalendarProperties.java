package org.chaos.advenskalender.calendar.book;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
@Getter
public class CalendarProperties {

    private final URI filesRoot;

    public CalendarProperties(@Value("${calendar.files.root}") final URI filesRoot) {
        this.filesRoot = filesRoot;
    }
}
