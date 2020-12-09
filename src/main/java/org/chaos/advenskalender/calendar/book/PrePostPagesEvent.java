package org.chaos.advenskalender.calendar.book;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

@Getter
public class PrePostPagesEvent extends ApplicationEvent {

    private LocalDateTime creationDate;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public PrePostPagesEvent(Object source) {
        super(source);
    }
}
