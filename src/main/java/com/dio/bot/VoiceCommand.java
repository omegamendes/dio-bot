package com.dio.bot;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.voice.AudioProvider;
import discord4j.voice.VoiceConnection;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Future;

@Slf4j
public class VoiceCommand {

    final AudioPlayerManager playerManager;
    final AudioPlayer player;
    final AudioProvider provider;
    final TrackScheduler scheduler;

    public VoiceCommand() {
        playerManager = new DefaultAudioPlayerManager();
        player = playerManager.createPlayer();
        provider = new LavaPlayerAudioProvider(player);
        scheduler = new TrackScheduler(player);
        playerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    public void voiceStateUpdate(GatewayDiscordClient client, VoiceStateUpdateEvent event) {
        User user = event.getCurrent().getUser().block();
        User self = client.getSelf().block();
        if(user.getId().equals(self.getId())){
            log.info("Evento gerado pelo próprio bot");
            return;
        }

        if(!event.getOld().isPresent() && event.getCurrent() != null) {
            sendMessage(playerManager, provider, scheduler, event.getCurrent().getChannel().block(), user, "entrou");
        } else if(!event.getCurrent().getChannelId().isPresent()){
            sendMessage(playerManager, provider, scheduler, event.getOld().orElse(null).getChannel().block(), user, "saiu");
        }
    }

    private void sendMessage(AudioPlayerManager playerManager, AudioProvider provider, TrackScheduler scheduler, VoiceChannel channel, User user, String msg) {
        //entrou
        VoiceConnection conn = channel.join(spec -> spec.setProvider(provider)).block();
        Future voice = playerManager.loadItem(GoogleVoicePlayer.getAudio(user.getUsername()+" "+msg), scheduler);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        conn.disconnect().block();
    }
}
