package com.dio.bot;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.voice.AudioProvider;
import discord4j.voice.VoiceConnection;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

@SpringBootApplication
public class DioWelcomeBotApplication {

    private static final Map<String, Command> commands = new HashMap<>();

    static {
        commands.put("ping", event -> event.getMessage().getChannel()
                .flatMap(channel -> channel.createMessage("Pong!"))
                .then());
    }

    public static void main(String[] args) {
        SpringApplication.run(DioWelcomeBotApplication.class, args);

        DiscordClient.create(System.getenv("token"))
                .withGateway(client -> {
                    client.getEventDispatcher().on(ReadyEvent.class)
                            .subscribe(ready -> System.out.println("Logged in as " + ready.getSelf().getUsername()));
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
