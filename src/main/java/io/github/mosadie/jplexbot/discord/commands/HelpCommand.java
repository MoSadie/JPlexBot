package io.github.mosadie.jplexbot.discord.commands;

import io.github.mosadie.jplexbot.JPlexBot;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class HelpCommand implements Command, Comparable<Command> {

    @Override
    public String getCommandName() {
        return "help";
    }

    @Override
    public String getFriendlyName() {
        return "Help";
    }

    @Override
    public String getUsage() {
        return "help";
    }

    @Override
    public String getHelpMessage() {
        return "Shows the help message";
    }

    @Override
    public String getExampleUsage() {
        return "help";
    }

    @Override
    public void execute(VoiceChannel vc, Message msg, JPlexBot plexBot) {
        String message = msg.getAuthor().getAsMention() + ", here's all the commands I can run:";
        String prefix = plexBot.discord.prefix;
        for(Command command : plexBot.discord.getCommands()) {
            message += "\n\n\n**__" + command.getFriendlyName() +"__**: (" + command.getCommandName() + ")" +
                        "\n\n *__Description__*: " + command.getHelpMessage() +
                        "\n\n *__Usage__*: `" + prefix + command.getUsage() + "`" +
                        "\n\n *__Example__*: `" + prefix + command.getExampleUsage() + "`";
        }

        msg.getChannel().sendMessage(message).queue();
    }

    @Override
    public int compareTo(Command other) {
        return getCommandName().compareTo(other.getCommandName());
    }
}