package com.dio.bot;

import discord4j.core.DiscordClient;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@Slf4j
public class DioWelcomeBotApplication {

    private static final Map<String, Command> commands = new HashMap<>();

    static {
        commands.put("ping", event -> event.getMessage().getChannel()
                .flatMap(channel -> channel.createMessage("Pong!"))
                .then());

        commands.put("xpto", event -> new XptoCommand().execute(event));
    }

    public static void main(String[] args) {
        SpringApplication.run(DioWelcomeBotApplication.class, args);

        DiscordClient.create(System.getenv("token"))
                .withGateway(client -> {
                    client.getEventDispatcher().on(ReadyEvent.class)
                            .subscribe(ready -> log.info("Logged in as " + ready.getSelf().getUsername()));
                    client.getEventDispatcher().on(VoiceStateUpdateEvent.class)
                            .subscribe( event -> {
                                new VoiceCommand().voiceStateUpdate(client, event);
                            });
                    client.getEventDispatcher().on(MessageCreateEvent.class)
                            .flatMap(event -> Mono.justOrEmpty(event.getMessage().getContent())
                                    .flatMap(content -> Flux.fromIterable(commands.entrySet())
                                            .filter(entry -> content.startsWith('!' + entry.getKey()))
                                            .flatMap(entry -> entry.getValue().execute(event))
                                            .next()))
                            .subscribe();
                    return client.onDisconnect();
                })
                .block();

    }


}
