package org.chaos.advenskalender.calendar.answer;

import discord4j.core.object.entity.User;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "answer")
@Data
public class Answer {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private UUID id;

    @Column(unique = true)
    private Long userId;

    @Column
    private String userName;

    @Column
    private LocalDateTime answerTime;

    @Column
    private String answer;

    public Answer() {
    }

    public Answer(User user) {
        this.userId = user.getId().asLong();
        this.userName = user.getUsername();
    }

    @PrePersist
    void prePersist(){
        answerTime = LocalDateTime.now();
    }
}
