package com.isyaratpintar.app.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class SoundManager {
    private MediaPlayer mediaPlayer;
    private TextToSpeech textToSpeech;
    private Context context;

    public SoundManager(Context context) {
        this.context = context;
        initTTS();
    }

    private void initTTS() {
        textToSpeech = new TextToSpeech(context, status -> {
            if (status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(new Locale("id", "ID")); // Bahasa Indonesia
            }
        });
    }

    public void playSound(int soundResId) {
        stopSound();
        mediaPlayer = MediaPlayer.create(context, soundResId);
        if (mediaPlayer != null) {
            mediaPlayer.setOnCompletionListener(mp -> stopSound());
            mediaPlayer.start();
        }
    }

    public void speakText(String text) {
        if (textToSpeech != null) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    public void stopSound() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void release() {
        stopSound();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }
    }
}
