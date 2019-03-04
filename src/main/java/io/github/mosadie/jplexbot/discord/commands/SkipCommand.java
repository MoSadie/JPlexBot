package io.github.mosadie.jplexbot.discord.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import io.github.mosadie.jplexbot.JPlexBot;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class SkipCommand implements Command, Comparable<Command> {

    @Override
    public String getCommandName() {
        return "skip";
    }

    @Override
    public String getFriendlyName() {
        return "Skip";
    }

    @Override
    public String getUsage() {
        return "skip";
    }

    @Override
    public String getHelpMessage() {
        return "Skips the currently playing song.";
    }

    @Override
    public String getExampleUsage() {
        return "skip";
    }

    @Override
    public void execute(VoiceChannel vc, Message msg, JPlexBot plexBot) {
        AudioTrack song = plexBot.music.skip(vc.getGuild());
        msg.getChannel().sendMessage(msg.getAuthor().getAsMention() + ", " + song.getInfo().title + " has been skipped.").queue();
    }

    @Override
    public int compareTo(Command other) {
        return getCommandName().compareTo(other.getCommandName());
    }
}