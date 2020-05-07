package com.dio.bot;

import discord4j.core.DiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DioWelcomeBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(DioWelcomeBotApplication.class, args);

        DiscordClient.create("Njk1NzkwODM4OTE5MzMxOTEx.XrAToQ.cQKta1M6w2Mpm9s19NRPxTqOrnw")
                .withGateway(client -> {
                    client.getEventDispatcher().on(ReadyEvent.class)
                            .subscribe(ready -> System.out.println("Logged in as " + ready.getSelf().getUsername()));

                    client.getEventDispatcher().on(MessageCreateEvent.class)
                            .map(MessageCreateEvent::getMessage)
                            .filter(msg -> msg.getContent().equals("!ping"))
                            .flatMap(Message::getChannel)
                            .flatMap(channel -> channel.createMessage("Pong!"))
                            .subscribe();

                    client.getEventDispatcher().on(MessageCreateEvent.class)
                            .map(MessageCreateEvent::getMessage)
                            .filter(msg -> msg.getContent().equalsIgnoreCase("!lula"))
                            .flatMap(Message::getChannel)
                            .flatMap(channel -> channel.createMessage("Livre !!"))
                            .subscribe();

                    return client.onDisconnect();
                })
                .block();
    }




}
