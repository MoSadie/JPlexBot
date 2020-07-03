package io.github.mosadie.jplexbot;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import javax.swing.JOptionPane;

import com.jcabi.manifests.Manifests;

import io.github.mosadie.jplexbot.discord.DiscordConnection;
import io.github.mosadie.plex.PlexApi;

public class JPlexBot {

    @SuppressWarnings("unused")
    public static void main(String[] args) {
        JPlexBot bot = new JPlexBot();
    }

    public DiscordConnection discord;
    public PlexApi plex;
    public MusicConnection music;
    public String version;

    public JPlexBot() {
        version = Manifests.read("Implementation-Version");
        File configDir = new File("JPlexBotConfig");
        configDir.mkdir();
        File configFile = new File(configDir, "config.txt");
        try {
            if (configFile.createNewFile()) {
                JOptionPane.showMessageDialog(null, "Welcome to the JPlexBot setup wizard! Click 'OK' to continue.",
                        "JPlexBot Setup", JOptionPane.PLAIN_MESSAGE);
                String plexUsername = JOptionPane.showInputDialog(null, "What is the username for the Plex account?",
                        "JPlexBot Setup", JOptionPane.QUESTION_MESSAGE);
                String plexPassword = JOptionPane.showInputDialog(null, "What is the password for the Plex account?",
                        "JPlexBot Setup", JOptionPane.QUESTION_MESSAGE);
                String discordBotToken = JOptionPane.showInputDialog(null, "What is the Discord Bot's token?",
                        "JPlexBot Setup", JOptionPane.QUESTION_MESSAGE);
                String prefix = JOptionPane.showInputDialog(null, "What is the desired prefix for all commands?",
                        "JPlexBot Setup", JOptionPane.QUESTION_MESSAGE);
                JOptionPane.showMessageDialog(null,
                        "All configured! Just copy/paste the url shown in the console window to add the bot to your server!",
                        "JPlexBot Setup", JOptionPane.PLAIN_MESSAGE);
                PrintStream file = new PrintStream(configFile);
                file.println(plexUsername);
                file.println(plexPassword);
                file.println(discordBotToken);
                file.println(prefix);
                file.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Scanner configInput = new Scanner(configFile);
            String plexUsername = configInput.nextLine();
            String plexPassword = configInput.nextLine();
            String discordBotToken = configInput.nextLine();
            String prefix = configInput.nextLine();

            configInput.close();

            // plex = new PlexApi(plexUsername, plexPassword);
            plex = new PlexApi("JPlexBot", version, "1");
            plex.authenticate(plexUsername, plexPassword);
            discord = new DiscordConnection(discordBotToken, prefix, this);

            System.out.println();
            System.out.println("DISCORD BOT INVITE LINK: " + discord.getInviteURL());
            System.out.println();

            music = new MusicConnection(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getVersion() {
        return version;
    }
}