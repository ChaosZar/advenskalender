package org.chaos.advenskalender.calendar.answer;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AnswerRepository extends CrudRepository<Answer, UUID> {

    List<Answer> findAllByUserId(Long userId);

    @Query("from answer a where a.answerTime >= from and a.answerTime <= to")
    Answer findByDate(LocalDateTime from);

}
