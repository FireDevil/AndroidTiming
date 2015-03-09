package split.timing.helpers;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.TreeMap;

import split.timing.R;
import split.timing.items.Startlist;

/**
 * Created by Antec on 05.06.2014.
 */
public class TimingGridAdapter extends BaseAdapter {

    final int INVALID_ID = -1;

    int[] contentList;
    Context mContext;
    int mode;
    Controller controller = Controller.getInstance();
    ArrayList<LineItem> items;

    public TimingGridAdapter(Context context, int[] numbers) {
        contentList = numbers;
        mContext = context;

        items = new ArrayList<>();
        TreeMap<Integer,Startlist> list = controller.getStartlistElements();


        int group = -1;
        int index = 0;
        for(int i : numbers){

            Startlist startlist = list.get(i);

            if(startlist.getStartId() != group){

                if(index%3 == 1){
                    items.add(new LineItem(index,-2,true,""));
                    index++;
                }

                while(index%3 != 1){
                    items.add(new LineItem(index,-2,true,""));
                    index++;
                }

                group = startlist.getStartId();
                items.add(new LineItem(index, -1, true, controller.getGroups().get(group).getName()));
                index++;

                items.add(new LineItem(index,-2,true,""));
                index++;
            }

            items.add(new LineItem(index,i,false,""+i));
            index++;
        }

    }


    @Override
    public LineItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getNumber();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();

        LineItem item = items.get(position);

        if(item.isHeader()){

            if(item.getText().length() == 0 && item.getNumber() == -2){
                convertView = inflater.inflate(R.layout.timing_center_cardview, parent, false);
                CardView cardView = (CardView) convertView.findViewById(R.id.timing_center_item_layout);
                cardView.setVisibility(View.GONE);
            }else {

                convertView = inflater.inflate(R.layout.timing_center_cardview, parent, false);
                CardView cardView = (CardView) convertView.findViewById(R.id.timing_center_item_layout);

                TextView t = (TextView) convertView.findViewById(R.id.timing_center_item_text);
                t.setText(item.getText() + "");
                t.setTextSize(20);
                t.requestFocus();
                cardView.setCardBackgroundColor(mContext.getResources().getColor(android.R.color.holo_red_dark));
            }

        }else{
            convertView = inflater.inflate(R.layout.timing_center_cardview, parent, false);
            FrameLayout fl = (FrameLayout)convertView.findViewById(R.id.timing_center_item_background);
            CardView cardView = (CardView)convertView.findViewById(R.id.timing_center_item_layout);

            TextView t = (TextView) convertView.findViewById(R.id.timing_center_item_text);
            t.setText(item.getText()+"");
            t.setTextSize(35);

            boolean jersey = controller.getStartlistElements().get(item.getNumber()).isJersey();

            if(jersey){
                fl.setBackgroundResource(R.drawable.trikot_rot);
            }

            if(controller.getLapCounter().containsKey(item.getNumber())) {
                int color = controller.getLapCounter().get(item.getNumber());
                color++;
                cardView.setCardBackgroundColor(ColorSetter.colorRoulette(color));
            }else{
                cardView.setCardBackgroundColor(ColorSetter.colorRoulette(1));
            }
        }


        return convertView;
    }

    private class LineItem{

        int position;
        int number;
        boolean isHeader;
        String text;

        public LineItem(int position, int number, boolean isHeader, String text){
            setPosition(position);
            setNumber(number);
            setText(text);
            setHeader(isHeader);
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public boolean isHeader() {
            return isHeader;
        }

        public void setHeader(boolean isHeader) {
            this.isHeader = isHeader;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

    }

}
