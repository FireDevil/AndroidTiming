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
import split.timing.items.Sportsmen;
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

        Sportsmen temp = backUpList.get(first);
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

    ArrayList<Sportsmen> mData;
    StableArrayAdapter adapter;

    int backUpPosition = -1;
    int backId = -1;
    ArrayList<Sportsmen> backUpList;

    HashMap<Integer, Integer> ids;

    boolean dismissed = false;

    UndoBarController undoBarController;

    Cursor c;
    int competitionId;
    int startgroupId;
    Startgroup startgroup = new Startgroup();

    boolean detailsOpened = true;
    boolean preExisting = false;

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

            mData = new ArrayList<Sportsmen>();
            backUpList = new ArrayList<Sportsmen>();
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

                startgroup = new Startgroup(comp.getInt(0), comp.getString(1), comp.getInt(2), comp.getInt(3), comp.getInt(4),comp.getInt(5), comp.getFloat(6), comp.getInt(7), comp.getInt(8),comp.getInt(9));

                comp.close();

                c = db.select("SELECT * FROM Startlist JOIN Sportsmen WHERE Startlist.sportsmenId = Sportsmen._id AND Startlist.startgroupId="+startgroupId);
                c.moveToLast();
                int pos = 0;

                if(getArguments().containsKey("added")){

                    int number;

                    if(c.getCount() == 0){
                        number = startgroup.getStartNum()-1;
                    }else{
                        number = c.getInt(1);
                    }

                    for(int id : getArguments().getIntegerArrayList("added")){

                        number = number+pos+1;

                        if(number != startgroup.getStartNum()-1 && controller.getStartNumbers().contains(number)){
                            number = 100192;
                        }

                        ContentValues values = new ContentValues();
                        values.put("sportsmenId",id);
                        values.put("startgroupId",startgroupId);
                        values.put("competitionId",competitionId);
                        values.put("jersey",false);
                        values.put("number",number);
                        values.put("startposition",c.getCount()+pos);
                        db.insert("Startlist",values);

                        pos++;
                    }

                    c.close();
                }

                c = db.select("SELECT * FROM Startlist JOIN Sportsmen WHERE Startlist.sportsmenId = Sportsmen._id AND Startlist.startgroupId="+startgroupId);
                if (c.getCount() > 0) {
                    pos = 0;

                    while (c.moveToNext()) {
                        mData.add(new Sportsmen(c.getInt(7), c.getString(8), c.getString(9), c.getString(10), c.getInt(11), c.getInt(12), c.getString(13), c.getString(14)));
                        backUpList.add(new Sportsmen(c.getInt(7), c.getString(8), c.getString(9), c.getString(10), c.getInt(11), c.getInt(12), c.getString(13), c.getString(14)));
                        ids.put(pos, c.getInt(7));
                        pos++;
                    }

                    if(startgroup.getJerseyNum() > -1 && (startgroup.getStartNum()+pos) < startgroup.getJerseyNum()){
                        startgroup.setJerseyNum(-1);
                    }

                    startgroup.setSportsmens(mData);

                }

                c.close();

                nameLabel.setText(startgroup.getName());

                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ArrayList tmp = new ArrayList<Integer>();

                        for(int key : ids.keySet()){
                            tmp.add(ids.get(key));
                        }
                        tmp.add(0);

                        mCallbacks.callStartgroupAddDialog(tmp, startgroupId);
                    }
                });


            }
            db.close();
        }


        adapter = new StableArrayAdapter(getActivity(), R.layout.startlist_element, startgroup, 0);
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

                            if (ids.size() > 0) {
                                backId = ids.get(position);
                            }

                            backUpPosition = position;
                            undoBarController.showUndoBar(
                                    false,
                                    mData.get(position).toString(),
                                    null);
                            ids.remove(position);
                            mData.remove(position);
                            //adapter.removeItem(backUpList.get(position),position);
                            dismissed = true;
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
                mCallbacks.onSportsmenSelected(competitionId,startgroupId,((Sportsmen)mData.get(position)).getId());
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.competition, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return true;
    }

    public void addNewSportsmen(){

    }

    public void refreshStartgroupData(Startgroup s){
        startgroup = s;
        startgroup.setSportsmens(mData);
        startgroupId = s.getId();
        nameLabel.setText(s.getName());

        adapter = new StableArrayAdapter(getActivity(), R.layout.startlist_element, startgroup, 0);
        lv.setAdapter(adapter);

        adapter.notifyDataSetInvalidated();

    }

    private void save(){
        ArrayList<Startlist> startlist = new ArrayList<Startlist>();

        DBHelper db = new DBHelper();
        db.sqlCommand("DELETE FROM Startlist WHERE startgroupId="+startgroupId);

        ContentValues values;
        int position=0;
        boolean jersey = false;

        for(Sportsmen s : mData){


            if(startgroup.getStartNum()+position == startgroup.getJerseyNum()){
                jersey = true;
            }else{
                jersey = false;
            }


            values = new ContentValues();
            values.put("sportsmenId",s.getId());
            values.put("startgroupId",startgroupId);
            values.put("competitionId",competitionId);
            values.put("jersey",jersey);
            values.put("number", startgroup.getStartNum()+position);
            values.put("startposition",position);
            db.insert("Startlist",values);

            startlist.add(new Startlist(s.getId(),startgroup.getStartNum()+position,jersey,competitionId,startgroupId,s.getId(),position));

            position++;
        }

        startgroup.setSportsmens(mData);
        controller.setSelectedStartgroup(startgroup.getId());
        controller.setSportsmen(startgroup.getSportsmens());
        controller.setStartlistElements(startlist);
        db.close();
    }

    private void deleteOnDismiss() {
        if (backUpPosition >= 0 && dismissed) {
            DBHelper db = new DBHelper();
            db.sqlCommand("DELETE FROM Startlist WHERE sportsmenId=" + ((Sportsmen) backUpList.get(backUpPosition)).getId() + " AND startgroupId=" + startgroupId);

            dismissed = false;
            backUpList.remove(backUpPosition);
            db.close();

        }
    }

    @Override
    public void onUndo(Parcelable token) {
        dismissed = false;
        if (mData.size() == backUpPosition) {
            ids.put(backUpPosition, ids.size());
            mData.add(backUpList.get(backUpPosition));
            //adapter.addItem(backUpList.get(backUpPosition),mData.size());
        } else {
            ids.put(backUpPosition, backId);
            mData.add(backUpPosition, backUpList.get(backUpPosition));
            //adapter.addItem(backUpList.get(backUpPosition),backUpPosition);
        }

        adapter.notifyDataSetChanged();
    }
}