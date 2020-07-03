package io.github.mosadie.jplexbot.discord.commands;

import io.github.mosadie.jplexbot.JPlexBot;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class VolumeCommand extends Command {

    @Override
    public String getCommandName() {
        return "volume";
    }

    @Override
    public String getFriendlyName() {
        return "Volume";
    }

    @Override
    public String getUsage() {
        return "volume (volume level)";
    }

    @Override
    public String getHelpMessage() {
        return "Gets or sets the current volume level of the bot. (Must be between 0 and 1000) (Default: 15)";
    }

    @Override
    public String getExampleUsage() {
        return "volume 50";
    }

    @Override
    public boolean requireVC() {
        return true;
    }

    @Override
    public void execute(VoiceChannel vc, Message msg, JPlexBot plexBot) {
        plexBot.music.checkAndJoinVC(vc);
        String[] command = msg.getContentRaw().split(" ", 2);
        if (command.length < 2) {
            int volume = plexBot.music.getVolume(vc.getGuild());
            if (volume < 0) {
                msg.getChannel().sendMessage(msg.getAuthor().getAsMention()
                        + " sorry about this but I couldn't get the current volume. Try again later.").queue();
            } else {
                msg.getChannel().sendMessage(msg.getAuthor().getAsMention() + ", the current volume level is "
                        + plexBot.music.getVolume(vc.getGuild()) + ".").queue();
            }
            return;
        }

        try {
            int newVolume = Integer.parseInt(command[1]);
            if (plexBot.music.setVolume(vc.getGuild(), newVolume)) {
                msg.getChannel().sendMessage(msg.getAuthor().getAsMention() + ", the volume is now at " + newVolume)
                        .queue();
            } else {
                msg.getChannel().sendMessage("Sorry, " + msg.getAuthor().getAsMention()
                        + ", the volume wasn't changed, please make sure your number is between 0 and 1000 and try again.")
                        .queue();
            }
        } catch (NumberFormatException e) {
            msg.getChannel().sendMessage("Sorry, " + msg.getAuthor().getAsMention()
                    + ", something went wrong converting what you asked into a number.").queue();
        }
    }
}