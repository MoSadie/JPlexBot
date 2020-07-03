package io.github.mosadie.jplexbot.discord.commands;

import io.github.mosadie.jplexbot.JPlexBot;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class ClearCommand extends Command {

    @Override
    public String getCommandName() {
        return "clear";
    }

    @Override
    public String getFriendlyName() {
        return "Clear Queue";
    }

    @Override
    public String getUsage() {
        return "clear";
    }

    @Override
    public String getHelpMessage() {
        return "Clears the queue.";
    }

    @Override
    public String getExampleUsage() {
        return "clear";
    }

    @Override
    public boolean requireVC() {
        return true;
    }

    @Override
    public void execute(VoiceChannel vc, Message msg, JPlexBot plexBot) {
        msg.getChannel().sendMessage("Hey, sorry, something's broken with clear, just spam skip to clear the queue.").queue();
        return;
        // while (!plexBot.music.getQueue(vc.getGuild(), true).isEmpty()) {
        //     plexBot.music.skip(vc.getGuild());
        // }
        // msg.getChannel().sendMessage(msg.getAuthor().getAsMention() + ", the queue has been cleared.").queue();
    }

}