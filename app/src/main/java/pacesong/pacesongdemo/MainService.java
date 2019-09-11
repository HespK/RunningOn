package pacesong.pacesongdemo;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;


import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Class for playing music and sending requests to accelerometer
 */

public class MainService extends Service {

    private static final String LOG_TAG = "MainService";
    public static boolean IS_SERVICE_RUNNING = false;

    private MediaPlayer mediaPlayer;

    private PowerManager.WakeLock wl;

    @Override
    public void onCreate() {
        super.onCreate();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "wake_lock");
    }


    /*
     * manages starting and stopping tracking and plating music
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(Constants.STARTFOREGROUND_ACTION)) {
            //start playing music and sending requests to accelerometer
            wl.acquire();
            showNotification("");
            startRequestingAccelerometerDataFromBackend();
            playSong();
            Toast.makeText(this, "Tracking Started!", Toast.LENGTH_SHORT).show();
        } else if (intent.getAction().equals(
                Constants.STOPFOREGROUND_ACTION)) {
            //stop tracking
            stopForeground(true);
            stopSelf();
        }
        return START_REDELIVER_INTENT;
    }

    private void startRequestingAccelerometerDataFromBackend() {
        requestAccelerometerDataFromBackend();
    }

    private void requestAccelerometerDataFromBackend() {
        //get request method
        Call<Result> call = ServiceFactory.getPaceService().getPace();

        //call async
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                try {
                    //get pace, analyze it and set appropriate category
                    Result result = response.body();
                    Song.SongType newValue = defineType(result.result);
                    PaceSongApp.currentType = newValue;
                    if (IS_SERVICE_RUNNING) {
                        showNotification(String.valueOf(result.result));
                        requestAccelerometerDataFromBackend();
                    }
                } catch (Exception e) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            requestAccelerometerDataFromBackend();
                        }
                    }, 1000);
                }

            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(MainService.this, "on failure: " + (t != null ? t.getMessage() : ""), Toast.LENGTH_SHORT).show();
            }
        });

    }


    /*
     * define category due to pace
     */
    private Song.SongType defineType(double pace) {
        if (pace < 0) {
            pace = -pace;
        }
        if (pace < 80) {
            return Song.SongType.LOW;
        } else if (pace < 120) {
            return Song.SongType.MEDIUM;
        } else {
            return Song.SongType.FAST;
        }
    }

    private void playSong() {

        //get next song
        Song song = PaceSongApp.findNext();

        Uri uri = Uri.parse(song.getName());
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (IS_SERVICE_RUNNING) {
                    playSong();
                }
            }
        });
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            //set song location to play
            mediaPlayer.setDataSource(getApplicationContext(), uri);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            //// TODO: 10/12/16
        }
        mediaPlayer.start();
    }
    
    
    private void showNotification(String pace) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.MAIN_ACTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("TutorialsFace Music Player")
                .setTicker("TutorialsFace Music Player")
                .setContentText(pace)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
        startForeground(Constants.NOTIFICATION_ID,
                notification);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        IS_SERVICE_RUNNING = false;
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        if (wl.isHeld())
            wl.release();
        Log.i(LOG_TAG, "In onDestroy");
        Toast.makeText(this, "Service Detroyed!", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
