package io.github.mosadie.jplexbot.discord.commands;

import io.github.mosadie.jplexbot.JPlexBot;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class LoopCommand extends Command {
    
    @Override
    public String getCommandName() {
        return "loop";
    }
    
    @Override
    public String getFriendlyName() {
        return "Loop";
    }
    
    @Override
    public String getUsage() {
        return "loop";
    }
    
    @Override
    public String getHelpMessage() {
        return "Toggles looping the queue, meaning songs will be put back in the queue after playing.";
    }
    
    @Override
    public String getExampleUsage() {
        return "loop";
    }
    
    @Override
    public void execute(VoiceChannel vc, Message msg, JPlexBot plexBot) {
        boolean looping = plexBot.music.getLooping(vc.getGuild());
        plexBot.music.setLooping(vc.getGuild(), !looping);
        if (looping) {
            msg.getChannel().sendMessage(msg.getAuthor().getAsMention() + ", looping disabled.").queue();
        } else {
            msg.getChannel().sendMessage(msg.getAuthor().getAsMention() + ", looping enabled.").queue();
        }
    }
}