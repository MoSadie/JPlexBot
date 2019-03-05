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
import io.github.mosadie.plex.PlexServer;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

public class MusicConnection {
    private final JPlexBot plexBot;
    private final AudioPlayerManager playerManager;
    private final Map<Guild, AudioPlayerSendHandler> players;
    public MusicConnection(JPlexBot plexBot) {
        this.plexBot = plexBot;
        
        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(getPlayerManager());
        players = new HashMap<>();
    }
    
    /**
	 * @return the playerManager
	 */
	public AudioPlayerManager getPlayerManager() {
		return playerManager;
	}

	public void addToQueue(VoiceChannel vc, MessageChannel tc, User author, String song) {
        Guild guild = vc.getGuild();
        
        checkAndJoinVC(vc);
        
        for (PlexServer server : plexBot.plex.getServers()) {
            try {
                List<PlexMusicTrack> tracks = server.searchTracks(song);
                if (tracks != null && tracks.size() > 0) {
                    song = tracks.get(0).getMediaFileURL(true);
                    break;
                }
            } catch (Exception e) {
                System.out.println("An error occured searching for a song on " + server +": " + e.getLocalizedMessage());
                System.out.println("The search will continue.");
                e.printStackTrace();
            }
        }
        
        getPlayerManager().loadItemOrdered(guild.getAudioManager(), song, new AudioLoadResultHandler(){
            
            @Override
            public void trackLoaded(AudioTrack track) {
                players.get(guild).trackScheduler.queue(track);
                tc.sendMessage(author.getAsMention() + ", added song `" + track.getInfo().title + "` to queue").queue();
            }
            
            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                for(AudioTrack track : playlist.getTracks()) {
                    players.get(guild).trackScheduler.queue(track);
                }
                tc.sendMessage(author.getAsMention() + ", added playlist `" + playlist.getName() + "` to the queue").queue();
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
            AudioPlayerSendHandler handler = new AudioPlayerSendHandler(getPlayerManager().createPlayer(), this);
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
        return removeFromQueue(guild, 0);
    }

    public AudioTrack removeFromQueue(Guild guild, int index) {
        if (players.containsKey(guild)) {
            AudioTrack track = players.get(guild).trackScheduler.removeFromQueue(index);
            return track;
        }

        return null;
    }

    public boolean getLooping(Guild guild) {
        if (players.containsKey(guild)) {
            return players.get(guild).trackScheduler.isLooping();
        }
        
        return false;
    }

    public void setLooping(Guild guild, boolean looping) {
        if (players.containsKey(guild)) {
            players.get(guild).trackScheduler.setLooping(looping);
        }
    }

    public boolean getPaused(Guild guild) {
        if (players.containsKey(guild)) {
            return players.get(guild).trackScheduler.isPaused();
        }
        
        return false;
    }

    public void setPaused(Guild guild, boolean paused) {
        if (players.containsKey(guild)) {
            players.get(guild).trackScheduler.setPaused(paused);
        }
    }
}