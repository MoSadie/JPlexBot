package io.github.mosadie.jplexbot.discord.commands;

import io.github.mosadie.jplexbot.JPlexBot;
import io.github.mosadie.plex.PlexServer;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class ListServersCommand implements Command, Comparable<Command> {
    
    @Override
    public String getCommandName() {
        return "listservers";
    }
    
    @Override
    public String getFriendlyName() {
        return "List Plex Servers";
    }
    
    @Override
    public String getUsage() {
        return "listservers";
    }
    
    @Override
    public String getHelpMessage() {
        return "Lists the available plex servers, use `selectServer` to select a server";
    }
    
    @Override
    public String getExampleUsage() {
        return "listservers";
    }
    
    @Override
    public void execute(VoiceChannel vc, Message msg, JPlexBot plexBot) {
        String message = msg.getAuthor().getAsMention() + ", here are the available Plex servers:";
        for (PlexServer server : plexBot.plex.getServers()) {
            message += "\n - ID: " + server.getID();
        }

        message += "\n\nNote: When searching for a song via Plex, the correct server will be selected automatically.";
        msg.getChannel().sendMessage(message).queue(); 
    }
    
    @Override
    public int compareTo(Command other) {
        return getCommandName().compareTo(other.getCommandName());
    }
}