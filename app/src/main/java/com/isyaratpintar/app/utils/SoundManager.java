package com.isyaratpintar.app.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class SoundManager {
    private MediaPlayer mediaPlayer;
    private TextToSpeech textToSpeech;
    private Context context;
    private boolean isTtsInitialized = false;

    public SoundManager(Context context) {
        this.context = context;
        initTTS();
    }

    private void initTTS() {
        textToSpeech = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(new Locale("id", "ID")); // Bahasa Indonesia
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("SoundManager", "Language not supported or data missing.");
                    // Opsional: tampilkan pesan ke pengguna untuk mengunduh data bahasa
                } else {
                    isTtsInitialized = true;
                }
            } else {
                Log.e("SoundManager", "TTS Initialization failed!");
            }
        });
    }

    public void playSound(int soundResId) {
        stopSound();
        try {
            mediaPlayer = MediaPlayer.create(context, soundResId);
            if (mediaPlayer != null) {
                mediaPlayer.setOnCompletionListener(mp -> stopSound());
                mediaPlayer.start();
                Log.d("SoundManager", "Playing sound: " + soundResId);
            }
        } catch (Exception e) {
            Log.e("SoundManager", "Error playing sound: " + e.getMessage());
        }
    }

    public void speakText(String text) {
        if (isTtsInitialized && textToSpeech != null) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            Log.d("SoundManager", "Speaking text: " + text);
        } else {
            Log.w("SoundManager", "TTS not initialized or null, cannot speak text.");
        }
    }

    public void stopSound() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
            Log.d("SoundManager", "Media player stopped and released.");
        }
    }

    public void release() {
        stopSound();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
            Log.d("SoundManager", "TextToSpeech shut down.");
        }
    }
}