package io.github.mosadie.jplexbot.discord.commands;

import java.util.Queue;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import io.github.mosadie.jplexbot.JPlexBot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class ListQueueCommand extends Command {
    
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
    public boolean requireVC() {
        return true;
    }
    
    @Override
    public void execute(VoiceChannel vc, Message msg, JPlexBot plexBot) {
        Queue<AudioTrack> queue = plexBot.music.getQueue(vc.getGuild(), true);
        String text = msg.getAuthor().getAsMention() + ", Here's the current queue:";
        
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Current Queue");
        int i = 0;
        while(!queue.isEmpty() && builder.length() < 5000) {
            AudioTrack track = queue.remove();
            if (track != null) {
                String title = i + ": *" + track.getInfo().title;
                String desc = (track.getInfo().author != null ? "Artist: " + track.getInfo().author + "     " : "") + "Duration: " + durationConversion(track.getDuration()) + "";
                builder.addField(title, desc, false);
                i++;
            }
        }
        
        builder.addBlankField(false);
        builder.addField("How to add a song", "Use `" + plexBot.discord.prefix + "play` to add a song!", false);

        MessageEmbed embed = builder.build();
        

        Message message = new MessageBuilder().append(text).setEmbed(embed).build();

        msg.getChannel().sendMessage(message).queue();
    }
    
    private String durationConversion(long milliseconds) {
        long minutes = milliseconds/60000;
        long seconds = (milliseconds%60000)/1000;
        return minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
    }
}