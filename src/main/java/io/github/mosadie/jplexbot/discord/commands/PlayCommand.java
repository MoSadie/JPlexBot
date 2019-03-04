package io.github.mosadie.jplexbot.discord.commands;

import io.github.mosadie.jplexbot.JPlexBot;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class PlayCommand implements Command, Comparable<Command> {

    @Override
    public String getCommandName() {
        return "play";
    }

    @Override
    public String getFriendlyName() {
        return "Play Song";
    }

    @Override
    public String getUsage() {
        return "play <Song Name/URL>";
    }

    @Override
    public String getHelpMessage() {
        return "Play the given song, either by url or name.\n" +
            "Supported platforms:\n" +
            " - Any song on the Plex server (Just type the name)\n" +
            " - Any Youtube video (Just put the url)\n" +
            " - Any Twitch stream (Just put the url)\n" +
            " - Any Soundcloud song (Just put the url)\n" +
            " - Any Bandcamp song (Just put the url)\n" +
            " - Any Vimeo video (Just put the url)\n" +
            " - Literally any url that points to an audio/video file in a standard format.\n" +
            " (MP3, FLAC, WAV, WebM, MP4/M4A (AAC codec), OGG streams, AAC streams, and Stream playlists (M3U and PLS))";  
    }

    @Override
    public String getExampleUsage() {
        return "play Battlefield";
    }

    @Override
    public void execute(VoiceChannel vc, Message msg, JPlexBot plexBot) {
        String[] commandAndArgs = msg.getContentRaw().split(" ", 2);
        String args = (commandAndArgs.length > 1 ? commandAndArgs[1] : "");

        if (args == "") {
            msg.getChannel().sendMessage(msg.getAuthor().getAsMention() +" you didn't tell me what to play! Usage: " + getUsage()).queue();
            return;
        }

        plexBot.music.addToQueue(vc, msg.getChannel(), args);
    }

    @Override
    public int compareTo(Command other) {
        return getCommandName().compareTo(other.getCommandName());
    }
}