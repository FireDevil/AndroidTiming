package split.timing.helpers;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import split.timing.R;
import split.timing.items.Sportsmen;

/**
 * Created by Antec on 05.06.2014.
 */
public class StableGridAdapter extends BaseAdapter {


    final int INVALID_ID = -1;

    ArrayList<Sportsmen> contentList;
    HashMap<Sportsmen, Integer> mIdMap = new HashMap<Sportsmen, Integer>();
    Context mContext;
    int mode;

    public StableGridAdapter(Context context, ArrayList<Sportsmen> objects, int mode) {
        for (int i = 0; i < objects.size(); ++i) {
            if (mode == 0) {
                mIdMap.put(objects.get(i), i);
            } else {
                mIdMap.put(objects.get(i), i);
            }

        }
        contentList = objects;
        this.mode = mode;
        mContext = context;

    }


    @Override
    public Sportsmen getItem(int position) {
        return contentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (position < 0 || position >= contentList.size()) {
            return INVALID_ID;
        }

        return mIdMap.get(getItem(position));
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getCount() {
        return contentList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();

        convertView = inflater.inflate(R.layout.grid_item, parent, false);

        TextView t = (TextView) convertView.findViewById(R.id.grid_item_text);
        t.setText(contentList.get(position).getName());

        return convertView;
    }

}
