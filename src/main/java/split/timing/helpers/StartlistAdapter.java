package split.timing.helpers;

import android.app.Activity;
import android.content.Context;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import split.timing.R;
import split.timing.items.Competition;
import split.timing.items.Sportsmen;
import split.timing.items.Startgroup;
import split.timing.items.Startlist;

/**
 * Created by Antec on 06.10.2014.
 */
public class StartlistAdapter extends ArrayAdapter<String> {

    private int mLayoutViewResourceId;
    private ArrayList<Startlist> mData;
    private TextView num;
    private TextView name;
    private TextView time;

    private Time clock;

    Controller controller = Controller.getInstance();

    public StartlistAdapter(Context context, int layoutViewResourceId,
                              ArrayList<Startlist> list) {
        super(context, layoutViewResourceId);
        mLayoutViewResourceId = layoutViewResourceId;
        mData = list;
        clock = new Time();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.timing_startlist_element, parent, false);
        }

        num = (TextView)convertView.findViewById(R.id.timing_startlist_element_num);
        name = (TextView)convertView.findViewById(R.id.timing_startlist_element_name);
        time = (TextView)convertView.findViewById(R.id.timing_startlist_element_time);

        Sportsmen sportsmen = controller.getNumbers().get(mData.get(position).getSportsmenId());

        num.setText(mData.get(position).getNumber()+"");
        name.setText(sportsmen.getName()+" "+sportsmen.getLastName());

        Startgroup startgroup = controller.getGroups().get(mData.get(position).getStartId());
        Competition competition = controller.getCompetitions().get(controller.getSelectedCompetition());


        int difference = mData.get(position).getDifference()+(startgroup.getStartHour()*3600)+(startgroup.getStartMinute()*60)+startgroup.getStartSecond();

        int hour = difference/3600;
        int minute = (difference%3600)/60;
        int second = (difference%3600)%60;

        clock.second = second;
        clock.minute = minute;
        clock.hour = hour;

        time.setText(clock.format("%H:%M:%S"));

        return convertView;
    }

    @Override
    public int getCount() {
        return mData.size();
    }
}
