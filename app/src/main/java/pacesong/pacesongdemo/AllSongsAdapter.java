package pacesong.pacesongdemo;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Class for viewing song list with the ability to select/deselect a particular song by changing
 * background color
 */

public class AllSongsAdapter extends BaseListViewAdapter<Song> {

    public AllSongsAdapter(Context context, List<Song> objects) {
        super(context, objects);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = lInflater.inflate(R.layout.song_list_view_item, viewGroup, false);
        }

        TextView textView = (TextView) view.findViewById(R.id.song_item);
        textView.setText(items.get(i).getShortName());

        if (items.get(i).isSelected()) {
            textView.setBackgroundColor(Color.parseColor("#3F51B5"));
        } else {
            textView.setBackgroundColor(Color.parseColor("#303F9F"));
        }

        return view;
    }
}
