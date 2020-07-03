package io.github.mosadie.jplexbot.discord.commands;

import java.util.List;

import io.github.mosadie.jplexbot.JPlexBot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class JoinFriendCommand extends Command {

    @Override
    public String getCommandName() {
        return "joinfriend";
    }

    @Override
    public String getFriendlyName() {
        return "Join a Friend's Voice Channel";
    }

    @Override
    public String getUsage() {
        return "joinfriend <Friend to join>";
    }

    @Override
    public String getHelpMessage() {
        return "Makes the bot join the voice channel a friend is in.";
    }

    @Override
    public String getExampleUsage() {
        return "joinfriend @MoSadie";
    }

    @Override
    public boolean requireVC() {
        return false;
    }

    @Override
    public void execute(VoiceChannel vc, Message msg, JPlexBot plexBot) {
        String[] commandAndArgs = msg.getContentRaw().split(" ", 2);
        String args = (commandAndArgs.length > 1 ? commandAndArgs[1] : "");

        if (args == null || args.equals("")) {
            msg.getChannel().sendMessage(msg.getAuthor().getAsMention() + ", you didn't specify a person to join!").queue();;
            return;
        }

        List<User> users = plexBot.discord.findUser(args);

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

        if (plexBot.music.checkAndJoinVC(vcTarget)) {
            msg.getChannel().sendMessage(msg.getAuthor().getAsMention() + ", I'm now joining the voice channel that " + user.getAsMention() + " is in!").queue();
            user.openPrivateChannel().queue((channel) -> { channel.sendMessage("Hey " + user.getAsMention() + "! I have been summoned to your voice channel by " + msg.getAuthor().getAsMention() + "!").queue();}, (exception) -> { exception.printStackTrace(System.out);} );
        } else {
            msg.getChannel().sendMessage(msg.getAuthor().getAsMention() + ", I'm already in their voice channel!").queue();
        }
    }
}