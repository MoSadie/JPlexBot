package io.github.mosadie.jplexbot.discord.commands;

import io.github.mosadie.jplexbot.JPlexBot;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;

public abstract class Command implements Comparable<Command> {
    public abstract String getCommandName();
    public abstract String getFriendlyName();
    public abstract String getUsage();
    public abstract String getHelpMessage();
    public abstract String getExampleUsage();
    public abstract void execute(VoiceChannel vc, Message msg, JPlexBot plexBot);

    public int compareTo(Command other) {
        return getCommandName().compareTo(other.getCommandName());
    }
}