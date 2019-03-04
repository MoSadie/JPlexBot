package io.github.mosadie.jplexbot;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import javax.swing.JOptionPane;

import io.github.mosadie.jplexbot.discord.DiscordConnection;
import io.github.mosadie.plex.PlexApi;
import io.github.mosadie.plex.PlexServer;

public class JPlexBot {
    public static void main(String[] args) {
        JPlexBot bot = new JPlexBot();
    }

    public DiscordConnection discord;
    public PlexApi plex;
    public PlexServer plexServer;
    public MusicConnection music;
    
    public JPlexBot() {
        File configDir = new File("JPlexBotConfig");
        configDir.mkdir();
        File configFile = new File(configDir, "config.txt");
        if (configDir.exists()) {
            System.out.println("Config Dir: " + configDir.getAbsolutePath());
        }
        try {
            if (configFile.createNewFile()) {
                JOptionPane.showMessageDialog(null, "Welcome to the JPlexBot setup wizard! Click 'OK' to continue.", "JPlexBot Setup", JOptionPane.PLAIN_MESSAGE);
                String plexUsername = JOptionPane.showInputDialog(null, "What is the username for the Plex account?", "JPlexBot Setup", JOptionPane.QUESTION_MESSAGE);
                String plexPassword = JOptionPane.showInputDialog(null, "What is the password for the Plex account?", "JPlexBot Setup", JOptionPane.QUESTION_MESSAGE);
                String discordBotToken = JOptionPane.showInputDialog(null, "What is the Discord Bot's token?", "JPlexBot Setup", JOptionPane.QUESTION_MESSAGE);
                String prefix = JOptionPane.showInputDialog(null, "What is the desired prefix for all commands?", "JPlexBot Setup", JOptionPane.QUESTION_MESSAGE);
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

            plex = new PlexApi(plexUsername, plexPassword);
            plexServer = (plex.getServers() != null ? plex.getServers().get(0) : null);
            discord = new DiscordConnection(discordBotToken, prefix, this);

            music = new MusicConnection(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}