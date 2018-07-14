package dreadloaf.com.countryquiz.util;

import android.content.Context;
import android.media.MediaPlayer;

import dreadloaf.com.countryquiz.R;

public class AudioUtil {

    public static final int menuMusic = R.raw.mystery_bazaar;
    public static final int quizMusic = R.raw.industrious_ferret;

    private static MediaPlayer mediaPlayer;
    private static boolean isPlayingMusic = false;

    public static void playMusic(Context context, int id){

        if(!isPlayingMusic){
            mediaPlayer = MediaPlayer.create(context, id);
            mediaPlayer.setVolume(0.8f, 0.8f);
            isPlayingMusic = true;
            mediaPlayer.start();
        }
    }

    public static void stopMusic(){
        if(mediaPlayer != null){
            isPlayingMusic = false;
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public static void pauseMusic(){
        if(mediaPlayer != null){
            mediaPlayer.pause();
        }
    }

    public static void resumeMusic(){
        if(mediaPlayer != null){
            mediaPlayer.start();
        }
    }

    public static void playSound(Context context, int id){
        MediaPlayer mp = MediaPlayer.create(context, id);
        mp.setVolume(0.5f, 0.5f);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
        });
        mp.start();
    }
}
