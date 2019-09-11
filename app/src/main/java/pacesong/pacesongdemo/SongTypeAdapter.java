package pacesong.pacesongdemo;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


/**
 * class for viewing selected and classified songs on MainActivity
 * it adds special color in dependence on song category
 */

public class SongTypeAdapter extends AllSongsAdapter {
    
    public SongTypeAdapter(Context context, List<Song> objects) {
        super(context, objects);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = super.getView(i, view, viewGroup);
        TextView textView = (TextView) view.findViewById(R.id.song_item);
        Song.SongType type = items.get(i).getType();
        if (type == null) {
            textView.setBackgroundDrawable(null);
            return view;
        }

        switch (type) {
            case LOW:
                textView.setBackgroundColor(Color.parseColor("#0000ff"));
                break;

            case MEDIUM:
                textView.setBackgroundColor(Color.parseColor("#ffff00"));
                break;

            case FAST:
                textView.setBackgroundColor(Color.parseColor("#ff0000"));
                break;
        }
        return view;
    }
}
