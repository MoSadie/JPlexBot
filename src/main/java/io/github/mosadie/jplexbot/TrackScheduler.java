package io.github.mosadie.jplexbot;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
* This class schedules tracks for the audio player. It contains the queue of tracks.
* Lightly modified from https://github.com/sedmelluq/lavaplayer/blob/9889618c9ce8a08b89f091fd80084308e71b99e7/demo-jda/src/main/java/com/sedmelluq/discord/lavaplayer/demo/jda/TrackScheduler.java
*/
public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private boolean looping;
    private final MusicConnection musicConn;
    private final AudioPlayerSendHandler apsh;
    
    /**
    * @param player The audio player this scheduler uses
    */
    public TrackScheduler(AudioPlayer player, MusicConnection musicConn, AudioPlayerSendHandler apsh) {
        this.player = player;
        this.musicConn = musicConn;
        this.apsh = apsh;
        this.queue = new LinkedBlockingQueue<>();
        this.looping = false;
    }
    
    /**
    * Add the next track to queue or play right away if nothing is in the queue.
    *
    * @param track The track to play or add to queue.
    */
    public void queue(AudioTrack track) {
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        } else {
            player.setPaused(false);
        }
    }
    
    /**
    * Start the next track, stopping the current one if it is playing.
    */
    public void nextTrack() {
        // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
        // giving null to startTrack, which is a valid argument and will simply stop the player.
        player.startTrack(queue.poll(), false);
    }
    
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (looping) {
            musicConn.getPlayerManager().loadItemOrdered(apsh, track.getIdentifier(), new AudioLoadResultHandler(){
            
                @Override
                public void trackLoaded(AudioTrack track) {
                    queue(track);
                }
                
                @Override
                public void playlistLoaded(AudioPlaylist playlist) {
                }
                
                @Override
                public void noMatches() {
                }
                
                @Override
                public void loadFailed(FriendlyException exception) {
                }
            });
        }
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }
    
    public Queue<AudioTrack> getQueue() {
        return getQueue(false);
    }
    
    public Queue<AudioTrack> getQueue(boolean includeCurrentlyPlaying) {
        Queue<AudioTrack> copyQueue = new LinkedList<AudioTrack>();
        AudioTrack[] queueArray = queue.toArray(new AudioTrack[queue.size() + (includeCurrentlyPlaying ? 1 : 0)]);
        if (includeCurrentlyPlaying && player.getPlayingTrack() != null)  {
            copyQueue.add(player.getPlayingTrack());
        }
        for (AudioTrack track : queueArray) {
            copyQueue.add(track);
        }
        
        return copyQueue;
    }

    public boolean isLooping() {
        return looping;
    }

    public void setLooping(boolean nowLooping) {
        looping = nowLooping;
    }

    public boolean isPaused() {
        return player.isPaused();
    }

    public void setPaused(boolean paused) {
        player.setPaused(paused);
    }

    public AudioTrack removeFromQueue(int index) {
        if (index == 0) {
            AudioTrack track = player.getPlayingTrack();
            nextTrack();
            return track;
        }

        int currIndex = 1;
        Iterator<AudioTrack> itr = queue.iterator();
        while (itr.hasNext() && currIndex <= index) {
            AudioTrack track = itr.next();
            if (currIndex == index) {
                itr.remove();
                return track;
            }
            currIndex++;
        }

        return null;
    }
}