package org.chaos.advenskalender.calendar;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "answer")
@Data
public class Answer {

    @Id
    private UUID id;

    @Column
    private Long userId;

    @Column
    private LocalDateTime answerTime;

    @Column
    private String answer;

}
