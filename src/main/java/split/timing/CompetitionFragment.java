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
import split.timing.items.Competition;
import split.timing.items.Startgroup;


public class CompetitionFragment extends Fragment implements
        UndoBarController.UndoListener,
        DynamicListView.SwapListener {

    private Callbacks mCallbacks = sCallbacks;

    @Override
    public void swapElements(int first, int second) {

        Startgroup temp = backUpList.get(first);
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
        public void callStartgroupAddDialog(ArrayList<Integer> ids, int startgroup);

        public void callStartgroupEditDialog(int startgroup, int competition);

        public void callCompetitionEditDialog(int competition);

        public void onStartgroupSelected(int startgroup, int competitionId);

        public void onCompetitionSelected(int id);

        public void backToList();

        public void showStartlist(int competitionId);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sCallbacks = new Callbacks() {


        @Override
        public void callStartgroupAddDialog(ArrayList<Integer> ids, int startgroup) {

        }

        @Override
        public void callStartgroupEditDialog(int startgroup, int competition) {

        }

        @Override
        public void callCompetitionEditDialog(int competition) {

        }

        @Override
        public void onStartgroupSelected(int startgroup, int competitionId) {

        }

        @Override
        public void onCompetitionSelected(int id) {

        }

        @Override
        public void backToList() {

        }

        @Override
        public void showStartlist(int competitionId) {

        }

    };

    public CompetitionFragment() {
    }

    DynamicListView lv;
    TextView nameLabel;

    CircleButton add;

    ArrayList<Startgroup> mData;
    StableArrayAdapter adapter;

    int backUpPosition = -1;
    int backId = -1;
    ArrayList<Startgroup> backUpList;

    HashMap<Integer, Integer> ids;

    boolean dismissed = false;

    UndoBarController undoBarController;

    Cursor c;
    int competitionId;
    int startgroupId;
    Competition co = new Competition();
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

        View rootView = inflater.inflate(R.layout.fragment_competition, container, false);
        lv = (DynamicListView) rootView.findViewById(R.id.listview);
        lv.init(this);
        final CircleButton cb = (CircleButton) rootView.findViewById(R.id.competition_circle_edit);
        add = (CircleButton) rootView.findViewById(R.id.competition_circle_btn);

        LinearLayout ll = (LinearLayout)rootView.findViewById(R.id.competition_ll);
        LayerDrawable bgDrawable = (LayerDrawable)ll.getBackground();
        GradientDrawable shape = (GradientDrawable) bgDrawable.findDrawableByLayerId(R.id.card_border);
        shape.setColor(ColorSetter.newInstance(3));

        nameLabel = (TextView) rootView.findViewById(R.id.competition_label);

//        loadSportsmen();

        if (getArguments() != null) {


            mData = new ArrayList<Startgroup>();
            backUpList = new ArrayList<Startgroup>();
            ids = new HashMap<Integer, Integer>();

            competitionId = getArguments().getInt("competitionId");
            mode = 1;

            cb.setColor(ColorSetter.newInstance(3));
            add.setColor(ColorSetter.newInstance(2));
            add.setImageResource(R.drawable.ic_action_new);


            if (competitionId > -1) {
                DBHelper db = new DBHelper();
                c = db.select("SELECT * FROM Competitionmember JOIN Startgroup WHERE startgroupId=Startgroup._id AND competitionId =" + competitionId);

                if (c.getCount() > 0) {

                    int pos = 0;

                    while (c.moveToNext()) {
                        mData.add(new Startgroup(c.getInt(4), c.getString(5), c.getInt(6), c.getInt(7), c.getInt(8),c.getInt(9), c.getFloat(10), c.getInt(11), c.getInt(12),c.getInt(13)));
                        backUpList.add(new Startgroup(c.getInt(4), c.getString(5), c.getInt(6), c.getInt(7), c.getInt(8),c.getInt(9), c.getFloat(10), c.getInt(11), c.getInt(12),c.getInt(13)));
                        ids.put(pos, c.getInt(4));
                        pos++;
                    }
                }

                controller.setStartgroups(mData);
                c.close();

                Cursor comp = db.select("SELECT * FROM Competition WHERE _id=" + competitionId);
                comp.moveToFirst();

                co = new Competition(comp.getInt(0), comp.getString(1), comp.getString(2), comp.getString(3), comp.getInt(4));
                co.setStartgroups(mData);

                comp.close();

                nameLabel.setText(co.getName());

                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCallbacks.callStartgroupEditDialog(-1,competitionId);
                    }
                });

                db.close();
            }
        }


        adapter = new StableArrayAdapter(getActivity(), R.layout.startlist_element,mData, mode);
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
                                    mData.get(position).getName(),
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
                switch (mode) {
                    case 1:
                        mCallbacks.onStartgroupSelected(mData.get(position).getId(), competitionId);
                        break;
                }
            }
        });

        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallbacks.callCompetitionEditDialog(competitionId);
            }
        });

        return rootView;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (Callbacks) activity;
            controller.loadSportsmen();
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

        switch (item.getItemId()) {
            case R.id.competition_clear_btn:
                DBHelper dbHelper = new DBHelper();
                dbHelper.delete("Competitionmember"," competitionId="+competitionId);
                mData.clear();
                ids.clear();

                undoBarController.showUndoBar(
                        false,
                        "All groups",
                        null);

                adapter.notifyDataSetChanged();
                break;
        }

        return true;
    }

    public void addNewStartgroup(Startgroup s){

        mData.add(s);
        backUpList.add(s);
        ids.put(mData.size(),s.getId());


        co.setStartgroups(mData);

        adapter = new StableArrayAdapter(getActivity(), R.layout.startlist_element,mData, mode);
        lv.setAdapter(adapter);
    }

    public void refreshCompetitionData(Competition c){
        co = c;
        co.setStartgroups(mData);
        competitionId = c.getId();
        nameLabel.setText(c.getName());
    }

    private void save(){
        DBHelper db = new DBHelper();
        db.sqlCommand("DELETE FROM Competitionmember WHERE competitionId="+competitionId);

        ContentValues values;
        int position=0;

        for(Startgroup s : mData){
            values = new ContentValues();
            values.put("position",position);
            values.put("startgroupId",s.getId());
            values.put("competitionId",competitionId);
            db.insert("Competitionmember",values);

            position++;
        }

        co.setStartgroups(mData);

        controller.setSelectedCompetition(co.getId());
        controller.setStartgroups(co.getStartgroups());

        db.close();
    }

    private void deleteOnDismiss() {
        if (backUpPosition >= 0 && dismissed) {
            DBHelper db = new DBHelper();
            db.sqlCommand("DELETE FROM Competitionmember WHERE startgroupId=" + backUpList.get(backUpPosition).getId() + " AND competitionId=" + competitionId);
            db.sqlCommand("DELETE FROM Startlist WHERE startgroupId=" + backUpList.get(backUpPosition).getId() + " AND competitionId=" + competitionId);

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
            for(Startgroup s: mData) {
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
