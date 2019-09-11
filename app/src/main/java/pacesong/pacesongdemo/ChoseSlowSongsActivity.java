package pacesong.pacesongdemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;


/**
 * Second activity which allows to select slow songs
 */

public class ChoseSlowSongsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private AllSongsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Slow songs");

        ListView listView = (ListView) findViewById(R.id.songs);

        List<Song> songs = PaceSongApp.songs;

        listView.setOnItemClickListener(this);

        adapter = new AllSongsAdapter(this, songs);
        listView.setAdapter(adapter);

        //show info dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose slow songs, please")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create().show();
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
                PaceSongApp.setSlowSongs();
                startActivity(new Intent(this, ChooseMediumSongsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Song song = ((AllSongsAdapter) adapterView.getAdapter()).items.get(i);
        song.setSelected(!song.isSelected());
        adapter.notifyDataSetChanged();
    }
}
