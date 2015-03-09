package split.timing.helpers;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import split.timing.R;
import split.timing.items.ResultGroup;
import split.timing.items.ResultItem;
import split.timing.items.Startlist;

/**
 * Created by Antec on 27.11.2014.
 */
public class ResultExpandableAdapter extends BaseExpandableListAdapter {

    private Callbacks mCallbacks = sCallbacks;
    private Context context;
    private ArrayList<ResultGroup> groups;
    Activity act;

    int state;

    Controller controller = Controller.getInstance();

    public interface Callbacks{
        public void showResultDetail(int resultId, int number, int startlistId);
    }

    private static Callbacks sCallbacks = new Callbacks() {
        @Override
        public void showResultDetail(int resultId, int number, int startlistId){
        }
    };

    public ResultExpandableAdapter(Context context, ArrayList<ResultGroup> groups, int state) {
        this.context = context;
        this.groups = groups;
        this.state = state;
    }

    public ResultItem getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).getChildren().get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View view,
                             ViewGroup parent) {
        ResultItem item = getChild(groupPosition, childPosition);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.timing_result_item, null);
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ResultGroup g = groups.get(groupPosition);
                ResultItem i = g.getChildren().get(childPosition);
                mCallbacks.showResultDetail(i.getId(), Integer.parseInt(i.getNumber()), i.getStartlistItem().getId());
            }
        });

        TextView tv = (TextView)view.findViewById(R.id.timing_result_item_position);
        tv.setText(""+item.getPosition());

//        if(item.getPosition() == 1){
//            tv.setTextColor(ColorSetter.newInstance(2));
//        }

        int num = Integer.parseInt(item.getNumber());
        Startlist startlist = item.getStartlistItem();

        ImageView jersey = (ImageView)view.findViewById(R.id.timing_result_item_trikot);
        if(startlist.isJersey()){
            jersey.setVisibility(View.VISIBLE);
        }else{
            jersey.setVisibility(View.GONE);
        }

        tv = (TextView)view.findViewById(R.id.timing_result_item_name);
        tv.setText("( " + item.getNumber()+" )");

        tv = (TextView)view.findViewById(R.id.timing_result_item_number);
        tv.setText(""+item.getName());

        tv = (TextView)view.findViewById(R.id.timing_result_item_difference);

        if(item.getPosition() == 1){
            tv.setText("" + Timer.secondsToTime(item.getRun()/1000).format("%H:%M:%S"));
        }else {
            tv.setText("+" + Timer.secondsToTime(item.getDifference()/1000).format("%H:%M:%S"));
        }

        tv = (TextView)view.findViewById(R.id.timing_result_item_info);
        tv.setText(""+item.getSportsmen().getClub());


        return view;
    }

    public int getChildrenCount(int groupPos) {
        return groups.get(groupPos).getChildren().size();

    }

    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    public int getGroupCount() {
        return groups.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(final int groupPosition, boolean isLastChild, View view,
                             ViewGroup parent) {
        ResultGroup g = (ResultGroup)groups.get(groupPosition);


        if(view==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.timing_result_group,null);
        }

        TextView textView = (TextView)view.findViewById(R.id.timing_result_group_name);
        textView.setText(g.getName());

//        textView = (TextView)view.findViewById(R.id.timing_result_group_info);

        textView = (TextView)view.findViewById(R.id.timing_result_group_count);
        textView.setText(g.getChildrenSize()+"");

        DBHelper db = new DBHelper();

        if(state == 0) {

            Cursor started = db.select("SELECT * FROM Startlist WHERE startgroupId=" + g.getGroup());

            textView = (TextView) view.findViewById(R.id.timing_result_group_started);
            textView.setText("/" + started.getCount());

            started.close();
        }

        db.close();

        return view;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }

    public void setAct(Activity act) {
        this.act = act;
        mCallbacks = (Callbacks) act;
    }
}
