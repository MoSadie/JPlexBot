package io.github.mosadie.jplexbot.discord.commands;

import java.util.List;

import io.github.mosadie.jplexbot.JPlexBot;
import io.github.mosadie.plex.PlexMusicTrack;
import io.github.mosadie.plex.PlexServer;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class SearchCommand extends Command {
    
    @Override
    public String getCommandName() {
        return "search";
    }
    
    @Override
    public String getFriendlyName() {
        return "Search";
    }
    
    @Override
    public String getUsage() {
        return "search <query>";
    }
    
    @Override
    public String getHelpMessage() {
        return "Searches the Plex server for tracks.\nYou can search either by Title or Artist";
    }
    
    @Override
    public String getExampleUsage() {
        return "search disney";
    }

    @Override
    public boolean requireVC() {
        return false;
    }
    
    @Override
    public void execute(VoiceChannel vc, Message msg, JPlexBot plexBot) {
        String[] split = msg.getContentRaw().split(" ", 2);
        if (split.length < 2) {
            msg.getChannel().sendMessage("Sorry, " + msg.getAuthor().getAsMention() + ", you didn't tell me what to look for! Try `" + plexBot.discord.prefix + "search <query>`!").queue();
            return;
        }
        String query = split[1];
        
        MessageBuilder mb = new MessageBuilder(msg.getAuthor().getAsMention() + ", here's the result of your search for `" + query + "`:");
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Search results for \"" + query + "\"");
        
        for (PlexServer server : plexBot.plex.getServers()) {
            List<PlexMusicTrack> serverResults = server.searchTracks(query);
            if (serverResults != null && !serverResults.isEmpty()) {
                for (PlexMusicTrack track : serverResults) {
                    eb.addField(track.getTitle(), "Artist: " + track.getArtist(), false);
                }
            }
        }

        mb.setEmbed(eb.build());
        msg.getChannel().sendMessage(mb.build()).queue();
    }
    
}