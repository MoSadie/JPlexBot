package io.github.mosadie.jplexbot;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;

import net.dv8tion.jda.core.audio.AudioSendHandler;

// From https://github.com/sedmelluq/lavaplayer/blob/9a7fd7417f5beb9eed449e51106375149d868c35/README.md#jda-integration
public class AudioPlayerSendHandler implements AudioSendHandler {
    private final AudioPlayer audioPlayer;
    public final TrackScheduler trackScheduler;
    private AudioFrame lastFrame;
  
    public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
      this.audioPlayer = audioPlayer;
      trackScheduler = new TrackScheduler(audioPlayer);
      audioPlayer.addListener(trackScheduler);
    }
  
    @Override
    public boolean canProvide() {
      lastFrame = audioPlayer.provide();
      return lastFrame != null;
    }
  
    @Override
    public byte[] provide20MsAudio() {
      return lastFrame.getData();
    }
  
    @Override
    public boolean isOpus() {
      return true;
    }
  }