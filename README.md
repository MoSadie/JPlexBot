# JPlexbot

## What is this?

It's a Discord bot designed to play music from a [Plex Media Server](https://plex.tv). (And a few other sources as well)

## How to use

1) Create a discord bot on the [Discord Developer Portal](https://discordapp.com/developers/applications/), taking note of the bot token.
1) Select the computer/server you want to use. (I would recommend on the same computer as the Plex Media Server)
2) Download the latest release from [here](https://github.com/MoSadie/JPlexBot/releases/latest) on to that computer and extract the contents. (And make sure Java is installed!)
3) Run the JPlexBot.bat (JPlexBot for non-Windows platforms) file in the bin folder and follow the on-screen instructions.
4) Go to the web address in the console window after finishing setup to invite the bot to your server.
5) Start playing songs with the play command! (You can either DM commands to the bot or use the prefix in any text channel)

## Useful Commands
- `help`: Displays all available commands.
- `play <song name or url>`: Plays a song.
- `skip`: Skips the current song.
- `queue`: Shows the current queue of songs.

## Config file
The config file is created in the folder JPlexBotConfig as config.txt in the directory that the bot was launched from. (Usually inside of bin, but can change based on how you launch the bot.) You can make it yourself to advoid the popup when starting for the first time. Replace anything with brackets with the correct information.

config.txt:
```
<Plex Username>
<Plex Password>
<Discord Bot Token>
<Prefix>
```
