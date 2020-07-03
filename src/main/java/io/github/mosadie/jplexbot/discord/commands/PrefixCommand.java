package io.github.mosadie.jplexbot.discord.commands;

import io.github.mosadie.jplexbot.JPlexBot;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class PrefixCommand extends Command {

    @Override
    public String getCommandName() {
        return "prefix";
    }

    @Override
    public String getFriendlyName() {
        return "Prefix";
    }

    @Override
    public String getUsage() {
        return "prefix";
    }

    @Override
    public String getHelpMessage() {
        return "Tells you the current prefix.";
    }

    @Override
    public String getExampleUsage() {
        return "prefix";
    }

    @Override
    public boolean requireVC() {
        return false;
    }

    @Override
    public void execute(VoiceChannel vc, Message msg, JPlexBot plexBot) {
        msg.getChannel().sendMessage(msg.getAuthor().getAsMention() + ", the prefix for all commands (other than DM commands) is `" + plexBot.discord.prefix + "`.").queue();
    }
}