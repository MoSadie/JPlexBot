package io.github.mosadie.jplexbot.discord.commands;

import java.util.List;

import io.github.mosadie.jplexbot.JPlexBot;
import io.github.mosadie.plex.PlexServer;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class StatusCommand implements Command, Comparable<Command> {

    @Override
    public String getCommandName() {
        return "status";
    }

    @Override
    public String getFriendlyName() {
        return "Status";
    }

    @Override
    public String getUsage() {
        return "status";
    }

    @Override
    public String getHelpMessage() {
        return "Replys with the current status of the bot. Not very interesting.";
    }

    @Override
    public String getExampleUsage() {
        return "status";
    }

    @Override
    public void execute(VoiceChannel vc, Message msg, JPlexBot plexBot) {
        List<PlexServer> plexServers =plexBot.plex.getServers();
        boolean plexStatus = true;
        if (plexServers == null || plexServers.isEmpty()) {
            plexStatus = false;
        }

        boolean discordStatus = plexBot.discord.isConnected();

        msg.getChannel().sendMessage("Current Status:\nPlex Servers found: " + (plexStatus ? "Yes" : "No") +
            "\nDiscord Connected: " + (discordStatus ? "Yes" : "No")).queue();
        
    }

    @Override
    public int compareTo(Command other) {
        return getCommandName().compareTo(other.getCommandName());
    }
}