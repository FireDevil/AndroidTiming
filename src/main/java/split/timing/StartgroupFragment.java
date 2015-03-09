package split.timing;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import split.timing.helpers.CircleButton;
import split.timing.helpers.ColorSetter;
import split.timing.helpers.Controller;
import split.timing.helpers.DBHelper;
import split.timing.helpers.DynamicListView;
import split.timing.helpers.StableArrayAdapter;
import split.timing.helpers.SwipeToDismissListener;
import split.timing.helpers.UndoBarController;
import split.timing.items.Startgroup;
import split.timing.items.Startlist;

/**
 * Created by Antec on 06.09.2014.
 */
public class StartgroupFragment  extends Fragment implements
        UndoBarController.UndoListener,
        DynamicListView.SwapListener {

    private Callbacks mCallbacks = sCallbacks;

    @Override
    public void swapElements(int first, int second) {

        Startlist temp = backUpList.get(first);
        backUpList.set(first, backUpList.get(second));
        backUpList.set(second, temp);

    }

    @Override
    public void swapFinished() {


    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        public void onSportsmenSelected(int competitionId, int startgroup, int sportsmen);

        public void callStartgroupAddDialog(ArrayList<Integer> ids, int startgroup);

        public void callStartgroupEditDialog(int startgroup, int competition);

        public void backToList();

    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sCallbacks = new Callbacks() {

        @Override
        public void onSportsmenSelected(int competitionId, int startgroup, int sportsmen) {

        }

        @Override
        public void callStartgroupAddDialog(ArrayList<Integer> ids, int startgroup) {

        }

        @Override
        public void callStartgroupEditDialog(int startgroup, int competition) {

        }

        @Override
        public void backToList() {

        }

    };

    public StartgroupFragment() {
    }

    DynamicListView lv;
    TextView nameLabel;
    CircleButton add;
    StableArrayAdapter adapter;

    int backUpPosition = -1;
    int backId = -1;
    int dismissId = -1;
    ArrayList<Startlist> mData;
    ArrayList<Startlist> backUpList;
    HashMap<Integer, Integer> ids;

    boolean dismissed = false;
    UndoBarController undoBarController;

    Cursor c;
    int competitionId;
    int startgroupId;
    Startgroup startgroup = new Startgroup();

    int mode = 0;

    Controller controller = Controller.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_startgroup, container, false);
        lv = (DynamicListView) rootView.findViewById(R.id.listview);
        lv.init(this);
        final CircleButton cb = (CircleButton) rootView.findViewById(R.id.startgroup_circle_edit);
        add = (CircleButton) rootView.findViewById(R.id.startgroup_circle_btn);

        LinearLayout ll = (LinearLayout)rootView.findViewById(R.id.startgroup_ll);
        LayerDrawable bgDrawable = (LayerDrawable)ll.getBackground();
        GradientDrawable shape = (GradientDrawable) bgDrawable.findDrawableByLayerId(R.id.card_border);
        shape.setColor(ColorSetter.newInstance(2));

        nameLabel = (TextView) rootView.findViewById(R.id.startgroup_label);

        if (getArguments() != null) {
            DBHelper db = new DBHelper();

            mData = new ArrayList<Startlist>();
            backUpList = new ArrayList<Startlist>();
            ids = new HashMap<Integer, Integer>();

            competitionId = getArguments().getInt("competitionId");
            startgroupId = getArguments().getInt("startgroupId");
            mode = 1;

            cb.setColor(ColorSetter.newInstance(2));
            add.setColor(ColorSetter.newInstance(0));
            add.setImageResource(R.drawable.ic_action_new);

            if (startgroupId > -1) {

                Cursor comp = db.select("SELECT * FROM Startgroup WHERE _id=" + startgroupId);
                comp.moveToFirst();

                startgroup = new Startgroup(comp.getInt(0), comp.getString(1), comp.getInt(2), comp.getInt(3), comp.getInt(4),comp.getInt(5), comp.getFloat(6), comp.getInt(7), comp.getInt(8));

                comp.close();

                c = db.select("SELECT * FROM Startlist WHERE Startlist.startgroupId="+startgroupId);
                c.moveToLast();
                int pos = 0;

                if(getArguments().containsKey("added")){

                    int number;
                    int set = -1;

                    if(c.getCount() == 0){
                        number = startgroup.getStartNum()-1;
                    }else{
                        number = c.getInt(1);
                    }

                    boolean overflow = false;

                    for(int id : getArguments().getIntegerArrayList("added")){

                        if(set != 9999) {
                            overflow = set != startgroup.getStartNum() && controller.getStartNumbers().contains(set);
                        }

                        if(overflow || number == 9999){
                            set = 9999;
                        }else {
                            set = number+pos+1;
                        }



                        ContentValues values = new ContentValues();
                        values.put("sportsmenId",id);
                        values.put("startgroupId",startgroupId);
                        values.put("competitionId",competitionId);
                        values.put("jersey",false);
                        values.put("number",set);
                        values.put("startposition",c.getCount()+pos);
                        values.put("difference",((c.getCount()+pos)*startgroup.getInterval()));
                        db.insert("Startlist",values);

                        pos++;
                    }


                }
                c.close();

                c = db.select("SELECT * FROM Startlist WHERE Startlist.startgroupId="+startgroupId+" ORDER BY number");
                if (c.getCount() > 0) {
                    pos = 0;

                    while (c.moveToNext()) {
                        boolean jersey = Boolean.parseBoolean(c.getString(2));

                        mData.add(new Startlist(c.getInt(0),c.getInt(1),jersey,c.getInt(3),c.getInt(4),c.getInt(5),c.getInt(6),c.getInt(7)));
                        backUpList.add(new Startlist(c.getInt(0),c.getInt(1),Boolean.parseBoolean(c.getString(2)),c.getInt(3),c.getInt(4),c.getInt(5),c.getInt(6),c.getInt(7)));
                        ids.put(pos, c.getInt(0));
                        pos++;
                    }

                }

                controller.setStartlistElements(mData);
                controller.setSelectedStartgroup(startgroup.getId());
                c.close();

                nameLabel.setText(startgroup.getName());

                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ArrayList tmp = new ArrayList<Integer>();

                        for(Startlist startlist : mData){
                            if(!tmp.contains(startlist.getSportsmenId())) {
                                tmp.add(startlist.getSportsmenId());
                            }
                        }

                        tmp.add(0);

                        mCallbacks.callStartgroupAddDialog(tmp, startgroupId);
                    }
                });


            }
            db.close();
        }


        adapter = new StableArrayAdapter(getActivity(), R.layout.startlist_element,mData, 0);
        undoBarController = new UndoBarController(rootView.findViewById(R.id.undobar), this);


        final SwipeToDismissListener dismissListener = new SwipeToDismissListener(lv,
                new SwipeToDismissListener.DismissCallbacks() {
                    @Override
                    public boolean canDismiss(int position) {
                        return true;
                    }

                    @Override
                    public void onDismiss(ListView listView, int[] reverseSortedPositions) {


                        deleteOnDismiss();

                        for (int position : reverseSortedPositions) {

                            if(ids.size() >= position) {

                                if (ids.size() > 0) {
                                    backId = mData.get(position).getId();
                                }

                                backUpPosition = position;
                                undoBarController.showUndoBar(
                                        false,
                                        mData.get(position).getNumber() + " - " + controller.getNumbers().get(mData.get(position).getSportsmenId()).getName(),
                                        null);
                                ids.keySet().remove(position);
                                mData.remove(position);
                                //adapter.removeItem(backUpList.get(position),position);
                                dismissed = true;

                            }
                        }

                        adapter.notifyDataSetChanged();
                    }
                }
        );

        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                lv.getmScrollListener().onScrollStateChanged(view, scrollState);
                dismissListener.setEnabled(true);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                lv.getmScrollListener().onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                dismissListener.setEnabled(false);
            }
        });

        lv.setOnTouchListener(dismissListener);
        lv.setItemList(mData);
        lv.setAdapter(adapter);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCallbacks.onSportsmenSelected(competitionId,startgroupId,mData.get(position).getSportsmenId());
            }
        });

        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallbacks.callStartgroupEditDialog(startgroupId,competitionId);

            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (Callbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement Callbacks");
        }


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sCallbacks;
        deleteOnDismiss();
        save();


    }

    @Override
    public void onPause() {
        super.onPause();
        deleteOnDismiss();
        save();

    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        deleteOnDismiss();
        save();
    }

    @Override
    public void onStop(){
        super.onStop();
        deleteOnDismiss();
        save();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.startgroup, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.startgroup_clear_btn:
                DBHelper dbHelper = new DBHelper();
                dbHelper.delete("Startlist"," startgroupId="+startgroupId);
                mData.clear();
                ids.clear();

                undoBarController.showUndoBar(
                        false,
                        "All starters",
                        null);

                adapter.notifyDataSetChanged();
                break;
            case R.id.startgroup_reorder:
                reorderList();
                adapter.notifyDataSetChanged();
                break;
        }

        return true;
    }

    public void reorderList(){

        int start = startgroup.getStartNum();
        int pos=0;
        int number = 0;

        for(Startlist startlist : mData){
            number = start+pos;
            startlist.setNumber(number);
            startlist.setStartposition(pos);
            startlist.setDifference(pos*startgroup.getInterval());

            pos++;
        }

        save();
        adapter.notifyDataSetChanged();

    }

    public void refreshStartgroupData(Startgroup s){
        startgroup = s;
        startgroupId = s.getId();
        nameLabel.setText(s.getName());

        adapter = new StableArrayAdapter(getActivity(), R.layout.startlist_element,mData, 0);
        lv.setAdapter(adapter);

        adapter.notifyDataSetInvalidated();

    }

    private void save(){
        ArrayList<Startlist> startlist = new ArrayList<Startlist>();

        DBHelper db = new DBHelper();
        db.sqlCommand("DELETE FROM Startlist WHERE startgroupId="+startgroupId);

        ContentValues values;
        int position=0;
        for(Startlist s : mData){

            values = new ContentValues();
            values.put("sportsmenId",s.getSportsmenId());
            values.put("startgroupId",startgroupId);
            values.put("competitionId",competitionId);
            values.put("jersey",s.isJersey());
            values.put("number", s.getNumber());
            values.put("startposition",position);
            values.put("difference",position*startgroup.getInterval());
            db.insert("Startlist",values);

            startlist.add(new Startlist(s.getId(),s.getNumber(),s.isJersey(),position,competitionId,startgroupId,s.getSportsmenId(),position*startgroup.getInterval()));

            position++;
        }

        controller.setSelectedStartgroup(startgroup.getId());
        controller.setStartlistElements(startlist);
        db.close();
    }

    private void deleteOnDismiss() {
        if (backUpPosition >= 0 && dismissed) {
            DBHelper db = new DBHelper();
            db.sqlCommand("DELETE FROM Startlist WHERE sportsmenId=" + backUpList.get(backUpPosition).getSportsmenId() + " AND startgroupId=" + startgroupId);

            dismissed = false;
            backUpList.remove(backUpPosition);
            db.close();

        }
    }

    @Override
    public void onUndo(Parcelable token) {
        dismissed = false;

        if(mData.size() == 0){
            mData.addAll(backUpList);

            int pos = 0;
            for(Startlist s: mData) {
                ids.put(pos, s.getId());
                pos++;
            }
        }else {

            if (mData.size() == backUpPosition) {
                ids.put(backUpPosition, ids.size());
                mData.add(backUpList.get(backUpPosition));
                //adapter.addItem(backUpList.get(backUpPosition),mData.size());
            } else {
                ids.put(backUpPosition, backId);
                mData.add(backUpPosition, backUpList.get(backUpPosition));
                //adapter.addItem(backUpList.get(backUpPosition),backUpPosition);
            }
        }

        adapter.notifyDataSetChanged();
    }
}