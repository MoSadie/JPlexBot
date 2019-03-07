package io.github.mosadie.jplexbot.discord.commands;

import io.github.mosadie.jplexbot.JPlexBot;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class JoinCommand extends Command {
    
    @Override
    public String getCommandName() {
        return "join";
    }
    
    @Override
    public String getFriendlyName() {
        return "Join Voice Channel";
    }
    
    @Override
    public String getUsage() {
        return "join";
    }
    
    @Override
    public String getHelpMessage() {
        return "Makes the bot join the voice channel you're in.";
    }
    
    @Override
    public String getExampleUsage() {
        return "join";
    }
    
    @Override
    public void execute(VoiceChannel vc, Message msg, JPlexBot plexBot) {
        if (plexBot.music.checkAndJoinVC(vc)) {
            msg.getChannel().sendMessage(msg.getAuthor().getAsMention() + ", I'm now joining the voice channel `" + vc.getName() + "`").queue();
        } else {
            msg.getChannel().sendMessage(msg.getAuthor().getAsMention() + ", I'm already in the voice channel!").queue();
        }
    }
}