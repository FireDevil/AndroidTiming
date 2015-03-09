package split.timing.helpers;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import split.timing.R;
import split.timing.items.Sportsmen;
import split.timing.items.Startlist;
import split.timing.items.Timed;

/**
 * Created by Antec on 14.10.2014.
 */
public class TimedAdapter extends ArrayAdapter {

    final int INVALID_ID = -1;

    private Context context;
    private Cursor cursor;

    ArrayList<Timed> mData;
    private TextView num;
    private TextView lap;
    private TextView name;
    private TextView time;
    private TextView clock;

    SimpleDateFormat sdf = new SimpleDateFormat("@ HH:mm:ss");//new SimpleDateFormat("dd.MM.yyyy @ HH:mm:ss");
    Date date;
    Controller controller = Controller.getInstance();

    public TimedAdapter(Context context, ArrayList<Timed> timeds) {
        super(context, R.layout.timing_timed_element);

        this.context = context;
        this.mData = timeds;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.timing_timed_element, parent, false);
        }

        num = (TextView)convertView.findViewById(R.id.timing_timed_element_num);
        lap = (TextView)convertView.findViewById(R.id.timing_timed_element_lap);
        name = (TextView)convertView.findViewById(R.id.timing_timed_element_name);
        time = (TextView)convertView.findViewById(R.id.timing_timed_element_time);
        clock = (TextView) convertView.findViewById(R.id.timing_timed_element_clock);

        Timed timed = mData.get(position);

        if(timed.getNumber() > 0){
            boolean jersey = controller.getStartlistElements().get(timed.getNumber()).isJersey();

            if(jersey){
                num.setBackgroundResource(R.drawable.trikot_rot);
                num.setTextColor(context.getResources().getColor(android.R.color.black));
            }else{
                num.setBackgroundResource(R.drawable.trikot_transparent);
                num.setTextColor(context.getResources().getColor(R.color.gruen));
            }
        }else{
            num.setBackgroundResource(0);
            num.setTextColor(context.getResources().getColor(R.color.gruen));
        }




        if(timed.getNumber()<0){
            date = new Date(timed.getTimedInMillis());

            num.setText("--");
            time.setText("--:--:--");
            name.setText(getContext().getResources().getString(R.string.noNumber));
            clock.setText(sdf.format(date));

            return convertView;
        }

        Startlist startlist = controller.getStartlistElements().get(timed.getNumber());
        Sportsmen sportsmen = controller.getNumbers().get(startlist.getSportsmenId());

        date = new Date(timed.getTimedInMillis());

        num.setText(timed.getNumber()+"");
//        lap.setText("~"+timed.getLap());
        name.setText(  sportsmen.getLastName()+ ", " +sportsmen.getName());
        time.setText(Timer.secondsToTime(timed.getRunInMillis()/1000).format("%d:%H:%M:%S"));
        clock.setText(sdf.format(date));

        return convertView;
    }

    @Override
    public int getCount() {
        return mData.size();
    }
}

