package org.chaos.advenskalender.calendar.answer;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AnswerRepository extends CrudRepository<Answer, UUID> {

    Answer findByUserId(Long userId);

    @Query("from answer a where a.answerTime >= ?1")
    List<Answer> findAllByDateFrom(LocalDateTime from);

}
