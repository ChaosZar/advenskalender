package org.chaos.advenskalender.calendar.answer;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.extern.slf4j.Slf4j;
import org.chaos.advenskalender.calendar.book.PrePostPagesEvent;
import org.chaos.advenskalender.discord.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

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
        if (channel.getType() != Channel.Type.DM) {
            return;
        }

        Long memberId = message.getAuthorAsMember().block().getId().asLong();

        saveAnswer(message, memberId);


    }

    private void saveAnswer(Message message, Long memberId) {
//        LocalDateTime now = LocalDateTime.now();
//        LocalDateTime.now()
//        answerRepository.findAnswerByUserAndDate(memberId, );
//
//
//        Answer answer = new Answer(memberId, now, message.getContent());
//        answerRepository.save(answer);
    }

    @EventListener
    void onPrePostPages(PrePostPagesEvent prePostPagesEvent){
        LocalDateTime creationDate = prePostPagesEvent.getCreationDate();
        answerRepository.findByDate(creationDate.minusDays(1));
    }
}
