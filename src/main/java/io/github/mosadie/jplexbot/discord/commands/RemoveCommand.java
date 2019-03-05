package io.github.mosadie.jplexbot.discord.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import io.github.mosadie.jplexbot.JPlexBot;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class RemoveCommand implements Command, Comparable<Command> {
    
    @Override
    public String getCommandName() {
        return "remove";
    }
    
    @Override
    public String getFriendlyName() {
        return "Remove";
    }
    
    @Override
    public String getUsage() {
        return "remove <Queue ID>";
    }
    
    @Override
    public String getHelpMessage() {
        return "Removes a specified song from the queue";
    }
    
    @Override
    public String getExampleUsage() {
        return "remove 1";
    }
    
    @Override
    public void execute(VoiceChannel vc, Message msg, JPlexBot plexBot) {
        String[] commandAndArgs = msg.getContentRaw().split(" ", 2);
        if (commandAndArgs.length < 2) {
            msg.getChannel().sendMessage(msg.getAuthor().getAsMention() + ", you didn't tell me what song to remove. Try " + plexBot.discord.prefix + getUsage()).queue();
            return;
        }

        boolean wasLooping = plexBot.music.getLooping(vc.getGuild());
        if (wasLooping) {
            plexBot.music.setLooping(vc.getGuild(), false);
        }
        
        String args = commandAndArgs[1];
        AudioTrack track = null;
        try {
            int index = Integer.parseInt(args);
            track = plexBot.music.removeFromQueue(vc.getGuild(), index);
        } catch (Exception e) {
            System.out.println("Exception occured removing song from queue: " + e.getLocalizedMessage());
        }

        if (wasLooping) {
            plexBot.music.setLooping(vc.getGuild(), true);
        }
        
        if (track == null) {
            msg.getChannel().sendMessage(msg.getAuthor().getAsMention() + ", something went wrong trying to remove that song.").queue();
        } else {
            msg.getChannel().sendMessage(msg.getAuthor().getAsMention() + ", *" + track.getInfo().title + "* has been removed.").queue();
        }
    }
    
    @Override
    public int compareTo(Command other) {
        return getCommandName().compareTo(other.getCommandName());
    }
}