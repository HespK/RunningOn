package pacesong.pacesongdemo;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * First activity which searches for all songs located on device and shows then in a list
 */

public class ChoseAllSongsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 32;
    private AllSongsAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("All songs");

        //to get songs from the device memory the app must have a permission to read storage
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
                init();
        }

    }


    private void init() {
        ListView listView = (ListView) findViewById(R.id.songs);
        List<Song> songs = PaceSongApp.allSongs;
        listView.setOnItemClickListener(this);
        if (songs.size() != 0) {
        } else {
            songs.addAll(readSongs(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI));
            songs.addAll(readSongs(MediaStore.Audio.Media.INTERNAL_CONTENT_URI));
        }
        adapter = new AllSongsAdapter(this, songs);
        listView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    init();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1, 1, 1, "Select");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                PaceSongApp.setSelectedSongs();
                startActivity(new Intent(this, ChoseSlowSongsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     * Reads songs from storage with location - uri
     */
    private List<Song> readSongs(Uri uri) {
        List<Song> songs = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        //check if file from storage is a music
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        //order by title
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cur = cr.query(uri, null, selection, null, sortOrder);
        if(cur != null) {
            while (cur.moveToNext()) {
                //filename
                String data = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA));
                songs.add(new Song(data));
            }
            cur.close();
        }
        return songs;
    }

    /*
     * Changes the selection of a touched song
     */

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Song song = ((AllSongsAdapter) adapterView.getAdapter()).items.get(i);
        song.setSelected(!song.isSelected());
        adapter.notifyDataSetChanged();
    }
}
