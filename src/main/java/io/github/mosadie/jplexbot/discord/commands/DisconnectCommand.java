package io.github.mosadie.jplexbot.discord.commands;

import io.github.mosadie.jplexbot.JPlexBot;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class DisconnectCommand extends Command {

    @Override
    public String getCommandName() {
        return "disconnect";
    }

    @Override
    public String getFriendlyName() {
        return "Disconnect";
    }

    @Override
    public String getUsage() {
        return "disconnect";
    }

    @Override
    public String getHelpMessage() {
        return "Disconnects the bot from it's current voice channel.";
    }

    @Override
    public String getExampleUsage() {
        return "disconnect";
    }

    @Override
    public boolean requireVC() {
        return true;
    }

    @Override
    public void execute(VoiceChannel vc, Message msg, JPlexBot plexBot) {
        if (plexBot.music.leaveVC(vc.getGuild())) {
            msg.getChannel().sendMessage(msg.getAuthor().getAsMention() + ", I've disconnected from the voice channel.").queue();
        } else {
            msg.getChannel().sendMessage("Sorry, " + msg.getAuthor().getAsMention() + ", I can't disconnect from a voice channel when I'm not currently connected to one!").queue();
        }
    }
    
}