package io.github.mosadie.jplexbot.discord.commands;

import io.github.mosadie.jplexbot.JPlexBot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
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
        MessageBuilder messageBuilder = new MessageBuilder();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        messageBuilder.setContent(msg.getAuthor().getAsMention() + ", here's all the commands I can run:");
        embedBuilder.setTitle("Available Commands");

        String prefix = plexBot.discord.prefix;
        for(Command command : plexBot.discord.getCommands()) {
            embedBuilder.addField(command.getFriendlyName() +": (" + command.getCommandName() + ")",
                "*__Description__*: " + command.getHelpMessage() +
                "\n *__Usage__*: `" + prefix + command.getUsage() + "`" +
                "\n *__Example__*: `" + prefix + command.getExampleUsage() + "`", false);
        }
        messageBuilder.setEmbed(embedBuilder.build());

        msg.getChannel().sendMessage(messageBuilder.build()).queue();
    }

    @Override
    public int compareTo(Command other) {
        return getCommandName().compareTo(other.getCommandName());
    }
}