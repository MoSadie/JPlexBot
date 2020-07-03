package io.github.mosadie.jplexbot.discord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.security.auth.login.LoginException;

import org.reflections.Reflections;

import io.github.mosadie.jplexbot.JPlexBot;
import io.github.mosadie.jplexbot.discord.commands.Command;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDA.Status;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.SelfUser;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class DiscordConnection extends ListenerAdapter {

    private JDA discord;
    public final String prefix;
    private final Map<String, Command> commands;
    private final JPlexBot plexBot;

    public DiscordConnection(String botToken, String prefix, JPlexBot plexBot) {
        this.plexBot = plexBot;
        this.prefix = prefix;
        try {
            discord = new JDABuilder(botToken).addEventListener(this).build();
            discord.getPresence().setPresence(OnlineStatus.ONLINE, Game.listening("Plex! (+ More!)"));
        } catch (LoginException e) {
            e.printStackTrace();
        }

        Reflections reflections = new Reflections("io.github.mosadie.jplexbot.discord.commands");
        Set<Class<? extends Command>> commandClasses = reflections.getSubTypesOf(Command.class);
        commands = new HashMap<>();
        for (Class<? extends Command> commandClass : commandClasses) {
            try {
                Command command = commandClass.getDeclaredConstructor().newInstance();
                commands.put(prefix + command.getCommandName(), command);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public String getInviteURL() {
        return discord.asBot().getInviteUrl(Permission.VIEW_CHANNEL, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getMessage().getAuthor().equals(discord.getSelfUser())) {
            return;
        }

        if (!event.getMessage().getContentRaw().startsWith(prefix) && event.getChannelType() != ChannelType.PRIVATE) {
            return;
        }

        if (event.getAuthor().isBot()) {
            event.getChannel().sendMessage("Sorry, " + event.getAuthor().getAsMention()
                    + ", I only accept commands from non-bots, to prevent abuse.").queue();
            return;
        }

        

        String[] commandAndArgs = event.getMessage().getContentRaw().split(" ", 2);
        String command = commandAndArgs[0].toLowerCase();

        if (event.getChannelType() == ChannelType.PRIVATE && !command.startsWith(prefix)) {
            command = prefix + command;
        }
        
        if (!commands.containsKey(command)) {
            event.getChannel()
                    .sendMessage("Sorry, " + event.getAuthor().getAsMention() + ", the command wasn't recongized.")
                    .queue();
            return;
        }

        VoiceChannel voiceChannel = getVoiceChannel(event.getAuthor());
        if (voiceChannel == null && commands.get(command).requireVC()) {
            event.getChannel().sendMessage("Sorry, " + event.getAuthor().getAsMention()
                    + ", you must join a voice channel I can access before using this command.").queue();
            return;
        }

        commands.get(command).execute(voiceChannel, event.getMessage(), plexBot);
    }

    public List<User> findUser(String username) {
        if (username.startsWith("@")) {
            username = username.substring(1);
            System.out.println("@ removed");
        }
        return discord.getUsersByName(username, true);
    }

    public List<Guild> getMutualGuilds(User...users) {
        return discord.getMutualGuilds(users);
    }

    public VoiceChannel getVoiceChannel(User user) {
        return getVoiceChannel(user, getMutualGuilds(user, discord.getSelfUser()));
    }

    public VoiceChannel getVoiceChannel(User user, List<Guild> guilds) {
        List<VoiceChannel> vcs = new ArrayList<>();
        for (Guild guild : guilds) {
            vcs.addAll(guild.getVoiceChannels());
        }
        for (VoiceChannel vc : vcs) {
            List<Member> members = vc.getMembers();
            for (Member member : members) {
                if (member.getUser().equals(user)) {
                    return vc;
                }
            }
        }
        return null;
    }

    public boolean isConnected() {
        return discord.getStatus() == Status.CONNECTED;
    }

    public SelfUser getSelfUser() {
        return discord.getSelfUser();
    }

    public Set<Command> getCommands() {
        Set<Command> commandList = new TreeSet<>();
        for (Command command : commands.values()) {
            commandList.add(command);
        }
        return commandList;
	}
} 