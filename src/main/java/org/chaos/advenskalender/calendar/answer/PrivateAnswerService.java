package org.chaos.advenskalender.calendar.answer;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.extern.slf4j.Slf4j;
import org.chaos.advenskalender.calendar.book.PostPagesEvent;
import org.chaos.advenskalender.discord.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PrivateAnswerService {

    @Autowired
    private Client discordClient;

    @Autowired
    private AnswerRepository answerRepository;

    @PostConstruct
    private void init() {
        GatewayDiscordClient client = discordClient.buildGatewayDiscordClient().block();
        client.on(MessageCreateEvent.class).subscribe(this::onNewMessage);

    }

    private void onNewMessage(MessageCreateEvent e) {
        Message message = e.getMessage();
        MessageChannel channel = message.getChannel().block();
        User member = message.getAuthor().get();

        if (isRestrictedMessage(channel, member)) {
            return;
        }
        log.info("new message received: {}", e);

        saveAnswer(message, member);
    }

    private boolean isRestrictedMessage(MessageChannel channel, User author) {
        return channel.getType() != Channel.Type.DM || author.isBot();
    }

    private void saveAnswer(Message message, User member) {
        log.debug("searching for entity with member id {}", member.getId());
        Answer answer = answerRepository.findByUserId(member.getId().asLong());

        if (answer == null) {
            log.info("No entity for member found. creating new one.");
            answer = new Answer(member);
        }
        answer.setAnswer(message.getContent());
        log.info("new answer will be saved for member {}", member);
        answerRepository.save(answer);

        member.getPrivateChannel().subscribe(c -> c.createMessage("answer received").subscribe());
        discordClient.sendText(member.getUsername() + " submitted an answer.");
    }

    @EventListener
    void onPostPages(PostPagesEvent postPagesEvent) {
        log.debug("pre Post Pages Event received");
        LocalDateTime creationDate = postPagesEvent.getCreationDate();
        List<Answer> allAnswers = answerRepository.findAllByDateFrom(creationDate.minusDays(1));
        log.debug("answers found: {}", allAnswers);
        if (allAnswers.isEmpty()) {
            return;
        }
        String allAnswersAsString = allAnswers.stream().map(
                a -> a.getUserName() + " answers: " + a.getAnswer()
        ).collect(Collectors.joining("\r\n"));
        discordClient.sendText("Answers from Yesterday:\r\n"+allAnswersAsString);
    }
}
