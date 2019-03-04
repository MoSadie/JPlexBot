package io.github.mosadie.jplexbot.discord.commands;

import io.github.mosadie.jplexbot.JPlexBot;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;

public interface Command {
    public String getCommandName();
    public String getFriendlyName();
    public String getUsage();
    public String getHelpMessage();
    public String getExampleUsage();
    public void execute(VoiceChannel vc, Message msg, JPlexBot plexBot);
}