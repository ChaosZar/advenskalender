package org.chaos.advenskalender.calendar.book;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

@Getter
public class PostPostPagesEvent extends ApplicationEvent {

    private final LocalDateTime creationDate = LocalDateTime.now();

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public PostPostPagesEvent(Object source) {
        super(source);
    }
}
