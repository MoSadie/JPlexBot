package io.github.mosadie.jplexbot.discord.commands;

import io.github.mosadie.jplexbot.JPlexBot;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class PauseCommand implements Command, Comparable<Command> {

    @Override
    public String getCommandName() {
        return "pause";
    }

    @Override
    public String getFriendlyName() {
        return "Pause";
    }

    @Override
    public String getUsage() {
        return "pause";
    }

    @Override
    public String getHelpMessage() {
        return "Pauses the currently playing song.";
    }

    @Override
    public String getExampleUsage() {
        return "pause";
    }

    @Override
    public void execute(VoiceChannel vc, Message msg, JPlexBot plexBot) {
        if (plexBot.music.getPaused(vc.getGuild())) {
            plexBot.music.setPaused(vc.getGuild(), false);
            msg.getChannel().sendMessage(msg.getAuthor().getAsMention() + ", unpaused.").queue();
        } else {
            plexBot.music.setPaused(vc.getGuild(), true);
            msg.getChannel().sendMessage(msg.getAuthor().getAsMention() + ", paused.").queue();
        }
    }

    @Override
    public int compareTo(Command other) {
        return getCommandName().compareTo(other.getCommandName());
    }
}