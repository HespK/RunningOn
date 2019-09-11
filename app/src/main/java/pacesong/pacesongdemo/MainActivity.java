package pacesong.pacesongdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import static pacesong.pacesongdemo.PaceSongApp.fastSongs;
import static pacesong.pacesongdemo.PaceSongApp.mediumSongs;
import static pacesong.pacesongdemo.PaceSongApp.slowSongs;

/*
 * The last screen with selected and classified songs. It adds the ability to start/stop tracking
 * accelerometer and listen to music.
 */

public class MainActivity extends AppCompatActivity {

    private AllSongsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = (ListView) findViewById(R.id.songs);
        List<Song> songs = new ArrayList<>();
        songs.addAll(slowSongs);
        songs.addAll(mediumSongs);
        songs.addAll(fastSongs);
        adapter = new SongTypeAdapter(this, songs);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (MainService.IS_SERVICE_RUNNING) {
            menu.add(1, 1, 1, "Stop");
        } else {
            menu.add(2, 2, 2, "Track");
        }
        menu.add(3, 3, 3, "Low");
        menu.add(4, 4, 4, "Medium");
        menu.add(5, 5, 5, "Fast");

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        invalidateOptionsMenu();
        Intent service = new Intent(MainActivity.this, MainService.class);
        switch (item.getItemId()) {
            case 1:
                MainService.IS_SERVICE_RUNNING = false;
                service.setAction(Constants.STOPFOREGROUND_ACTION);
                startService(service);
                break;

            case 2:
                MainService.IS_SERVICE_RUNNING = true;
                service.setAction(Constants.STARTFOREGROUND_ACTION);
                startService(service);
                break;

            case 3:
                PaceSongApp.currentType = Song.SongType.LOW;
                break;

            case 4:
                PaceSongApp.currentType = Song.SongType.MEDIUM;
                break;

            case 5:
                PaceSongApp.currentType = Song.SongType.FAST;
                break;

        }
        invalidateOptionsMenu();
        return super.onOptionsItemSelected(item);
    }

    /*
     * called when user touches back button
     * stop tracking when it pressed
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent service = new Intent(MainActivity.this, MainService.class);
        MainService.IS_SERVICE_RUNNING = false;
        service.setAction(Constants.STOPFOREGROUND_ACTION);
        startService(service);
    }
}
