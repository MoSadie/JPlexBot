package io.github.mosadie.jplexbot.discord.commands;

import java.util.Queue;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import io.github.mosadie.jplexbot.JPlexBot;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class ListQueueCommand implements Command, Comparable<Command> {
    
    @Override
    public String getCommandName() {
        return "queue";
    }
    
    @Override
    public String getFriendlyName() {
        return "Queue";
    }
    
    @Override
    public String getUsage() {
        return "queue";
    }
    
    @Override
    public String getHelpMessage() {
        return "Replys with a list of the current songs in the queue.";
    }
    
    @Override
    public String getExampleUsage() {
        return "queue";
    }
    
    @Override
    public void execute(VoiceChannel vc, Message msg, JPlexBot plexBot) {
        Queue<AudioTrack> queue = plexBot.music.getQueue(vc.getGuild(), true);
        String message = msg.getAuthor().getAsMention() + ", Here's the current queue:";
        
        int i = 0;
        while(!queue.isEmpty()) {
            AudioTrack track = queue.remove();
            if (track != null) {
                message += "\n- " + i + ": *" + track.getInfo().title + (track.getInfo().author != null ? "* by " + track.getInfo().author : "") + " *(" + durationConversion(track.getDuration()) + ")*";
                i++;
            }
        }
                
        message += "\n\n Use `" + plexBot.discord.prefix + "play` to add a song!";
        msg.getChannel().sendMessage(message).queue();
    }
    
    @Override
    public int compareTo(Command other) {
        return getCommandName().compareTo(other.getCommandName());
    }
    
    private String durationConversion(long milliseconds) {
        long minutes = milliseconds/60000;
        long seconds = (milliseconds%60000)/1000;
        return minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
    }
}