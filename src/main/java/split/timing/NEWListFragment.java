package split.timing;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import split.timing.helpers.CircleButton;
import split.timing.helpers.ColorSetter;
import split.timing.helpers.CustomArrayAdapter;
import split.timing.helpers.DBHelper;
import split.timing.helpers.SwipeToDismissListener;
import split.timing.helpers.UndoBarController;
import split.timing.items.Competition;
import split.timing.items.Group;
import split.timing.items.Sportsmen;
import split.timing.items.Startgroup;

/**
 * A list fragment representing a list of NEWs. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link NEWDetailFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class NEWListFragment extends Fragment implements
        UndoBarController.UndoListener {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onGroupSelected(int id);

        public void onSportsSelected(int id);

        public void onListSelected(int id);

        public void onResultSelected(int id);

        public void onCompetitionSelected(int id);

        public void callCompetitionEditDialog(int competition);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onGroupSelected(int id) {
        }

        @Override
        public void onSportsSelected(int id) {

        }

        @Override
        public void onListSelected(int id) {

        }

        @Override
        public void onResultSelected(int id) {

        }

        @Override
        public void onCompetitionSelected(int id) {

        }

        @Override
        public void callCompetitionEditDialog(int competition) {

        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NEWListFragment() {
    }

    ListView lv;
    TextView order;

    Bundle mArgs;
    CustomArrayAdapter adapter;
    ArrayList mData;
    ArrayList backUpList;
    int backUpPosition = -1;
    boolean dismissed = false;

    int listMode = -1;
    Cursor c;
    int sortOrder = 1;

    String[] listModeTable = new String[]{"Sportsmen", "Groups", "Startgroup","Competition"};
    String[] sortList;
    
    Controller controller = Controller.newInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        TextView top = (TextView) rootView.findViewById(R.id.listText);
        lv = (ListView) rootView.findViewById(R.id.listView);
        order = (TextView) rootView.findViewById(R.id.list_order);
        CircleButton cb = (CircleButton) rootView.findViewById(R.id.list_circle_btn);

        DBHelper db =null;
        try {
            db = new DBHelper();
        }catch(Exception sql){
            Log.e("SQLiteCantOpenDatabaseException","NEWListFragement Line: 158");
        }

        if (getArguments() != null) {
            mArgs = getArguments();
        } else {
            return rootView;
        }

        if (mArgs.containsKey("Sports")) {

            sortList = new String[]{"_id", "name", "lastname", "age", "club", "federation"};

            c = db.select("SELECT * FROM Sportsmen");

            mData = new ArrayList<Sportsmen>();
            backUpList = new ArrayList<Sportsmen>();
            listMode = 0;

            while (c.moveToNext()) {


                mData.add(new Sportsmen(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getInt(4), c.getInt(5), c.getString(6), c.getString(7)));
                backUpList.add(new Sportsmen(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getInt(4), c.getInt(5), c.getString(6), c.getString(7)));
            }

            top.setText("Sports" + mArgs.getString("Sports"));
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    int sportsmenId = ((Sportsmen) mData.get(position)).getId();
                    mCallbacks.onSportsSelected(sportsmenId);
                }
            });

            cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallbacks.onSportsSelected(-1);
                }
            });

        }

        if (mArgs.containsKey("Group")) {

            sortList = new String[]{"_id", "name"};

            top.setText("Groups");
            c = db.select("SELECT * FROM Groups");
            mData = new ArrayList<Group>();
            backUpList = new ArrayList<Group>();
            listMode = 1;

            while (c.moveToNext()) {
                mData.add(new Group(c.getInt(0), c.getString(1)));
                backUpList.add(new Group(c.getInt(0), c.getString(1)));

            }

            cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallbacks.onGroupSelected(-1);
                }
            });

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    mCallbacks.onGroupSelected(((Group) mData.get(i)).getId());
                }
            });
        }

        /*if(mArgs.containsKey("Startgroup")){
            sortList = new String[]{"_id","name","starthour","distance"};

            top.setText("Startgroups");
            c = db.select("SELECT * FROM Startgroup");

            mData = new ArrayList<Startgroup>();
            backUpList = new ArrayList<Startgroup>();
            listMode = 2;

            while(c.moveToNext()){
                mData.add(new Startgroup(c.getInt(0),c.getString(1),c.getInt(2),c.getInt(3),c.getInt(4),c.getFloat(5),c.getInt(6)));
                backUpList.add(new Startgroup(c.getInt(0),c.getString(1),c.getInt(2),c.getInt(3),c.getInt(4),c.getFloat(5),c.getInt(6)));
            }

            cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallbacks.onListSelected(-1);
                }
            });

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    mCallbacks.onListSelected(((Startgroup)mData.get(i)).getId());
                }
            });
        }*/

        if(mArgs.containsKey("Competition")){
            sortList = new String[]{"_id","name","date","location"};

            top.setText("Competitions");
            c = db.select("SELECT * FROM Competition WHERE finished="+0);

            mData = new ArrayList<Competition>();
            backUpList = new ArrayList<Competition>();
            listMode = 3;

            while(c.moveToNext()){
                mData.add(new Competition(c.getInt(0),c.getString(1),c.getString(2),c.getString(3),c.getInt(4)));
                backUpList.add(new Competition(c.getInt(0),c.getString(1),c.getString(2),c.getString(3),c.getInt(4)));
            }

            cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallbacks.callCompetitionEditDialog(-1);
                }
            });

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    mCallbacks.onCompetitionSelected(((Competition)mData.get(i)).getId());
                }
            });
        }

        adapter = new CustomArrayAdapter(getActivity(), R.layout.startlist_element, mData, listMode);
        lv.setAdapter(adapter);

        final UndoBarController finalMUndoBarController = new UndoBarController(rootView.findViewById(R.id.undobar), this);
        final SwipeToDismissListener touchListener =
                new SwipeToDismissListener(
                        lv,
                        new SwipeToDismissListener.DismissCallbacks() {

                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                deleteOnDismiss();

                                for (int position : reverseSortedPositions) {
                                    backUpPosition = position;
                                    finalMUndoBarController.showUndoBar(
                                            false,
                                            mData.get(position).toString(),
                                            null);
                                    mData.remove(position);
                                    dismissed = true;
                                }

                                adapter.notifyDataSetChanged();
                            }
                        }
                );

        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                touchListener.setEnabled(true);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                touchListener.setEnabled(false);
            }
        });
        lv.setOnTouchListener(touchListener);
        cb.setColor(ColorSetter.newInstance(listMode));

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
       deleteOnDismiss();
        controller.setCompetitions(mData);
        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onPause() {
        super.onPause();
        deleteOnDismiss();
        controller.setCompetitions(mData);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onUndo(Parcelable token) {
        dismissed = false;
        if (mData.size() == backUpPosition) {
            mData.add(backUpList.get(backUpPosition));
        } else {
            mData.add(backUpPosition, backUpList.get(backUpPosition));
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.list_fragment, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.list_sort:

                DBHelper db = new DBHelper();
                Cursor cursor = db.select("SELECT * FROM " + listModeTable[listMode] + " ORDER BY " + sortList[sortOrder] + " COLLATE NOCASE");

                switch (listMode) {
                    case 0:

                        mData.clear();
                        backUpList.clear();

                        while (cursor.moveToNext()) {
                            mData.add(new Sportsmen(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4), cursor.getInt(5), cursor.getString(6), cursor.getString(7)));
                            backUpList.add(new Sportsmen(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4), cursor.getInt(5), cursor.getString(6), cursor.getString(7)));
                        }
                        cursor.close();
                        order.setText(getResources().getString(R.string.order_by) + " " + sortList[sortOrder]);
                        adapter.notifyDataSetChanged();

                        switch (sortOrder) {
                            case 0:
                                item.setIcon(android.R.drawable.ic_menu_sort_by_size);
                                break;
                            case 1:
                                item.setIcon(R.drawable.ic_action_sort_by_size_name);
                                break;
                            case 2:
                                item.setIcon(R.drawable.ic_action_sort_by_size_name);
                                break;
                            case 3:
                                item.setIcon(R.drawable.ic_action_sort_by_size_age);
                                break;
                            case 4:
                                item.setIcon(R.drawable.ic_action_sort_by_size_club);
                                break;
                            case 5:
                                item.setIcon(R.drawable.ic_action_sort_by_size_fed);
                                break;

                        }
                        break;
                    case 1:
                        mData.clear();
                        backUpList.clear();

                        while (cursor.moveToNext()) {
                            mData.add(new Group(cursor.getInt(0), cursor.getString(1)));
                            backUpList.add(new Group(cursor.getInt(0), cursor.getString(1)));

                        }

                        cursor.close();
                        order.setText(getResources().getString(R.string.order_by) + " " + sortList[sortOrder]);
                        adapter.notifyDataSetChanged();

                        switch (sortOrder) {
                            case 0:
                                item.setIcon(android.R.drawable.ic_menu_sort_by_size);
                                break;
                            case 1:
                                item.setIcon(R.drawable.ic_action_sort_by_size_name);
                                break;
                        }


                        break;
                   /* case 2:
                        mData.clear();
                        backUpList.clear();

                        while (cursor.moveToNext()) {
                                mData.add(new Startgroup(cursor.getInt(0),cursor.getString(1),cursor.getInt(2),cursor.getInt(3),cursor.getInt(4),cursor.getFloat(5),cursor.getInt(6)));
                                backUpList.add(new Startgroup(cursor.getInt(0),cursor.getString(1),cursor.getInt(2),cursor.getInt(3),cursor.getInt(4),cursor.getFloat(5),cursor.getInt(6)));
                        }

                        cursor.close();
                        order.setText(getResources().getString(R.string.order_by) + " " + sortList[sortOrder]);
                        adapter.notifyDataSetChanged();
                        break;*/
                    case 3:
                        mData.clear();
                        backUpList.clear();

                        while(cursor.moveToNext()){
                            mData.add(new Competition(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getInt(4)));
                            backUpList.add(new Competition(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getInt(4)));
                        }

                        cursor.close();
                        order.setText(getResources().getString(R.string.order_by) + " " + sortList[sortOrder]);
                        adapter.notifyDataSetChanged();
                        break;

                }

                sortOrder++;
                if (sortOrder == sortList.length) {
                    sortOrder = 0;
                }
                db.close();
                break;

        }

        return false;
    }

    private void deleteOnDismiss(){
        if (backUpPosition >= 0 && dismissed) {

            DBHelper db = new DBHelper();
            switch (listMode) {
                case 0:
                    db.delete("Sportsmen", " _id=" + ((Sportsmen) backUpList.get(backUpPosition)).getId());
                    db.sqlCommand("DELETE FROM Groupmember WHERE sportsmenId=" + ((Sportsmen) backUpList.get(backUpPosition)).getId());
                    db.sqlCommand("DELETE FROM Startlist WHERE sportsmenId=" + ((Sportsmen)backUpList.get(backUpPosition)).getId());
                    db.sqlCommand("DELETE FROM Startgroupmember WHERE sportsmenId="+((Sportsmen)backUpList.get(backUpPosition)).getId());
                    break;
                case 1:
                    db.delete("Groups", "_id=" + ((Group) backUpList.get(backUpPosition)).getId());
                    db.sqlCommand("DELETE FROM Groupmember WHERE groupId=" + ((Group) backUpList.get(backUpPosition)).getId());
                    break;
                case 2:
                    db.delete("Startgroup","_id="+((Startgroup)backUpList.get(backUpPosition)).getId());
                    db.sqlCommand("DELETE FROM Startgroupmember WHERE _id="+((Startgroup) backUpList.get(backUpPosition)).getId());
                    db.sqlCommand("DELETE FROM Startlist WHERE startgroupId="+((Startgroup) backUpList.get(backUpPosition)).getId());
                    break;
                case 3:
                    db.delete("Competition","_id="+((Competition)backUpList.get(backUpPosition)).getId());
                    db.sqlCommand("DELETE FROM Competitionmember WHERE competitionId="+((Competition)backUpList.get(backUpPosition)).getId());
                    break;
            }

            dismissed = false;
            backUpList.remove(backUpPosition);
            db.close();

        }
    }

    public ListView getListView() {
        return lv;
    }
}
