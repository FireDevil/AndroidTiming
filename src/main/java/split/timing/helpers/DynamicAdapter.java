package split.timing.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Space;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import split.timing.R;
import split.timing.items.Sportsmen;

/**
 * Created by Antec on 17.06.2014.
 */
public class DynamicAdapter extends BaseDynamicGridAdapter {
    public DynamicAdapter(Context context, List<?> items, int columnCount) {
        super(context, items, columnCount);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Space space;
        int height;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.empty_grid_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.build(getItem(position).toString());

//        space = (Space)convertView.findViewById(R.id.grid_item_space);

        if(position%getColumnCount()-1 != 0){
//            space.setVisibility(View.GONE);
        }

        return convertView;
    }

    private class ViewHolder {
        private TextView titleText;

        private ViewHolder(View view) {
//            titleText = (TextView) view.findViewById(R.id.grid_item_text);
        }

        void build(String title) {
            titleText.setText(title);
        }
    }

    public ArrayList<Sportsmen> getElements(){
        return (ArrayList<Sportsmen>) getItems();
    }
}
