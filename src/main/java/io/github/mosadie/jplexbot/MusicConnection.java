package io.github.mosadie.jplexbot;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import io.github.mosadie.plex.PlexMusicTrack;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

public class MusicConnection {
    private final JPlexBot plexBot;
    private final AudioPlayerManager playerManager;
    private final Map<Guild, AudioPlayerSendHandler> players;
    public MusicConnection(JPlexBot plexBot) {
        this.plexBot = plexBot;

        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        players = new HashMap<>();
    }

    public void addToQueue(VoiceChannel vc, MessageChannel tc, String song) {
        Guild guild = vc.getGuild();

        checkAndJoinVC(vc);

        if (plexBot.plexServer != null) {
            List<PlexMusicTrack> tracks = plexBot.plexServer.searchTracks(song);
            if (tracks != null && tracks.size() > 0) {
                song = tracks.get(0).getMediaFileURL(true);
            }
        }

        playerManager.loadItemOrdered(guild.getAudioManager(), song, new AudioLoadResultHandler(){
        
            @Override
            public void trackLoaded(AudioTrack track) {
                players.get(guild).trackScheduler.queue(track);
                tc.sendMessage("Added song `" + track.getInfo().title + "` to queue").queue();
            }
        
            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                for(AudioTrack track : playlist.getTracks()) {
                    players.get(guild).trackScheduler.queue(track);
                }
                tc.sendMessage("Added playlist `" + playlist.getName() + "` to the queue").queue();
            }
        
            @Override
            public void noMatches() {
                tc.sendMessage("No songs found. Try checking your spelling").queue();
            }
        
            @Override
            public void loadFailed(FriendlyException exception) {
                tc.sendMessage("Something went wrong adding the song, try again later.").queue();
            }
        });


    }

    public boolean checkAndJoinVC(VoiceChannel vc) {
        Guild guild = vc.getGuild();
        AudioManager audioManager = guild.getAudioManager();
        boolean result = false;

        if (vc != guild.getAudioManager().getConnectedChannel()) {
            guild.getAudioManager().openAudioConnection(vc);
            result = true;
        }

        if (!players.containsKey(guild)) {
            audioManager.openAudioConnection(vc);
            AudioPlayerSendHandler handler = new AudioPlayerSendHandler(playerManager.createPlayer());
            audioManager.setSendingHandler(handler);
            players.put(guild, handler);
        }

        return result;
    }

    public Queue<AudioTrack> getQueue(Guild guild) {
        return getQueue(guild, false);
    }

    public Queue<AudioTrack> getQueue(Guild guild, boolean includeCurrentTrack) {
        if (players.containsKey(guild)) {
            return players.get(guild).trackScheduler.getQueue(includeCurrentTrack);
        }

        return new LinkedList<AudioTrack>();
    }

    public AudioTrack skip(Guild guild) {
        if (players.containsKey(guild)) {
            AudioTrack track = players.get(guild).trackScheduler.getQueue(true).peek();
            players.get(guild).trackScheduler.nextTrack();
            return track;
        }

        return null;
    }
}