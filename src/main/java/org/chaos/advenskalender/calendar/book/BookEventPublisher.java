package org.chaos.advenskalender.calendar.book;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class BookEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public BookEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    void publish(PrePostPagesEvent prePostPagesEvent){
        applicationEventPublisher.publishEvent(prePostPagesEvent);
    }
}
