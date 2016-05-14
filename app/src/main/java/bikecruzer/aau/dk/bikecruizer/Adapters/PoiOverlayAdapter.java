package bikecruzer.aau.dk.bikecruizer.Adapters;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import bikecruzer.aau.dk.bikecruizer.R;

/**
 * Created by michael on 14-05-2016.
 */
public class PoiOverlayAdapter extends ArrayAdapter<Integer> {
    private final Context context;
    private final Integer[] values;

    public PoiOverlayAdapter(Context context, Integer[] values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.poi_overlay_item, parent, false);
        LinearLayout textView = (LinearLayout) rowView.findViewById(R.id.ratingsIcons);
        //ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        //textView.setText(Integer.toString(values[position]));
        int[] ids = new int[] {R.id.icon2,R.id.icon3,R.id.icon4, R.id.icon5, R.id.icon6};

        Log.i("value", Integer.toString(position));

        for (int i = 0; i < values[position]; i++){
            ImageView v = (ImageView) textView.findViewById(ids[i]);
            v.setImageResource(R.drawable.favourites);
        }

        // change the icon for Windows and iPhone
        return rowView;
    }
}
