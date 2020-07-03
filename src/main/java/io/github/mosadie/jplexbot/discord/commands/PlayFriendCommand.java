package io.github.mosadie.jplexbot.discord.commands;

import java.util.List;

import io.github.mosadie.jplexbot.JPlexBot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class PlayFriendCommand extends Command {

    @Override
    public String getCommandName() {
        return "playfriend";
    }

    @Override
    public String getFriendlyName() {
        return "Play Song To Friend";
    }

    @Override
    public String getUsage() {
        return "playfriend <Friend Username>  <Song Name/URL>";
    }

    @Override
    public String getHelpMessage() {
        return "Play the given song to a friend's voice channel, either by url or name.\n" +
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
        return "play @MoSadie Battlefield";
    }

    @Override
    public boolean requireVC() {
        return false;
    }

    @Override
    public void execute(VoiceChannel vc, Message msg, JPlexBot plexBot) {
        String[] commandAndArgs = msg.getContentRaw().split(" ", 3);
        String username = (commandAndArgs.length > 1 ? commandAndArgs[1] : "");
        String args = (commandAndArgs.length > 2 ? commandAndArgs[2] : "");

        if (username.equals("")) {
            msg.getChannel().sendMessage(msg.getAuthor().getAsMention() + ", you didn't tell me who to join!").queue();
        }

        List<User> users = plexBot.discord.findUser(username);

        if (users.isEmpty()) {
            msg.getChannel().sendMessage(msg.getAuthor().getAsMention() + ", that user wasn't found!").queue();
            return;
        }
        
        if (users.size() > 1) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Pick a user!");
            embedBuilder.setDescription("There was more than one user you could be refering to, here are some suggested users. Please try again.");
            for (int i = 0; i < users.size(); i++) {
                embedBuilder.addField(users.get(i).getAsTag(), "", false);
            }
            msg.getChannel().sendMessage(embedBuilder.build()).queue();
        }

        User user = users.get(0);

        List<Guild> guilds = plexBot.discord.getMutualGuilds(user, msg.getAuthor(), plexBot.discord.getSelfUser());

        if (guilds.size() < 1) {
            msg.getChannel().sendMessage(msg.getAuthor().getAsMention() + ", I couldn't find a server with you, " + user.getAsMention() +", and myself on it!").queue();;
            return;
        }

        VoiceChannel vcTarget = plexBot.discord.getVoiceChannel(user, guilds);

        if (vcTarget == null) {
            msg.getChannel().sendMessage(msg.getAuthor().getAsMention() + ", I couldn't find " + user.getAsMention() + "'s voice channel!").queue();
            return;
        }

        if (args.equals("") && !plexBot.music.getPaused(vcTarget.getGuild())) {
            msg.getChannel().sendMessage(msg.getAuthor().getAsMention() + " you didn't tell me what to play! Usage: " + getUsage()).queue();
            return;
        } else if (args.equals("")) {
            plexBot.music.setPaused(vcTarget.getGuild(), false);
            msg.getChannel().sendMessage(msg.getAuthor().getAsMention() + ", unpaused.").queue();
            return;
        }

        plexBot.music.addToQueue(vcTarget, msg.getChannel(), msg.getAuthor(), args);
        user.openPrivateChannel().queue((channel) -> { channel.sendMessage("Hey " + user.getAsMention() + "! An item was attempted to be added to your queue by " + msg.getAuthor().getAsMention() + "!").queue();}, (exception) -> { exception.printStackTrace(System.out);} );
    }
}