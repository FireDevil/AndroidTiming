package split.timing;

import android.app.Activity;
import android.content.ContentValues;
import android.content.res.Resources;
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
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import split.timing.helpers.CircleButton;
import split.timing.helpers.ColorSetter;
import split.timing.helpers.DBHelper;
import split.timing.helpers.DynamicListView;
import split.timing.helpers.StableArrayAdapter;
import split.timing.helpers.SwipeToDismissListener;
import split.timing.helpers.UndoBarController;
import split.timing.items.Competition;
import split.timing.items.Sportsmen;
import split.timing.items.Startgroup;


public class CompetitionSetupFragment extends Fragment implements
        UndoBarController.UndoListener,
        DynamicListView.SwapListener {

    private Callbacks mCallbacks = sCallbacks;

    @Override
    public void swapElements(int first, int second) {

        Object temp = backUpList.get(first);
        backUpList.set(first, backUpList.get(second));
        backUpList.set(second, temp);

    }

    @Override
    public void swapFinished() {


        if (mData.size() > 0) {
            if (mData.get(0).getClass().toString().equals("class split.timing.items.Startgroup")) {
                saveCompetition();
            } else {
                saveStartgroup();
            }
        }
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        public void callStartgroupAddDialog(ArrayList<Integer> ids, int startgroup);

        public void callStartgroupEditDialog(int startgroup, int competition);

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

    public CompetitionSetupFragment() {
    }

    DynamicListView lv;
    LinearLayout ll;
    TableRow edit_row;
    TableRow name_row;
    TableRow location_row;
    TextView nameLabel;
    EditText name;
    EditText location;
    DatePicker datePicker;

    CircleButton add;

    ArrayList mData;
    StableArrayAdapter adapter;

    int backUpPosition = -1;
    int backId = -1;
    ArrayList backUpList;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_competitionsetup, container, false);
        lv = (DynamicListView) rootView.findViewById(R.id.listview);
        lv.init(this);
        final CircleButton cb = (CircleButton) rootView.findViewById(R.id.competition_circle_edit);
        add = (CircleButton) rootView.findViewById(R.id.competition_circle_btn);

        ll = (LinearLayout) rootView.findViewById(R.id.competition_ll);
        edit_row = (TableRow) rootView.findViewById(R.id.startlist_edit_row);
        name_row = (TableRow) rootView.findViewById(R.id.startlist_name_row);
        location_row = (TableRow) rootView.findViewById(R.id.startlist_location_row);
        nameLabel = (TextView) rootView.findViewById(R.id.competition_label);
        name = (EditText) rootView.findViewById(R.id.competition_name_edit);
        location = (EditText) rootView.findViewById(R.id.competition_location_edit);
        datePicker = (DatePicker) rootView.findViewById(R.id.competition_datepicker);

        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), Calendar.MONTH, cal.get(Calendar.DAY_OF_MONTH));


        if (getArguments() != null) {

            if (getArguments().containsKey("competitionId")) {

                mData = new ArrayList<Startgroup>();
                backUpList = new ArrayList<Startgroup>();
                ids = new HashMap<Integer, Integer>();

                competitionId = getArguments().getInt("competitionId");
                mode = 1;

                cb.setColor(ColorSetter.newInstance(3));
                add.setColor(ColorSetter.newInstance(2));
                add.setImageResource(R.drawable.ic_action_new);



                LayerDrawable bgDrawable = (LayerDrawable) ll.getBackground();
                GradientDrawable shape = (GradientDrawable) bgDrawable.findDrawableByLayerId(R.id.card_border);
                shape.setColor(ColorSetter.newInstance(3));

                LinearLayout llFirst = (LinearLayout) datePicker.getChildAt(0);
                LinearLayout llSecond = (LinearLayout) llFirst.getChildAt(0);
                for (int i = 0; i < llSecond.getChildCount(); i++) {
                    NumberPicker picker = (NumberPicker) llSecond.getChildAt(i); // Numberpickers in llSecond
                    // reflection - picker.setDividerDrawable(divider); << didn't seem to work.
                    Field[] pickerFields = NumberPicker.class.getDeclaredFields();
                    for (Field pf : pickerFields) {
                        if (pf.getName().equals("mSelectionDivider")) {
                            pf.setAccessible(true);
                            try {
                                pf.set(picker, getResources().getColor(android.R.color.holo_blue_dark));
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                            } catch (Resources.NotFoundException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }
                }

                if (competitionId >= 0) {
                    DBHelper db = new DBHelper();
                    c = db.select("SELECT * FROM Competitionmember JOIN Startgroup WHERE startgroupId=Startgroup._id AND competitionId =" + competitionId);

                    if (c.getCount() > 0) {

                        int pos = 0;

                        while (c.moveToNext()) {
                            mData.add(new Startgroup(c.getInt(4), c.getString(5), c.getInt(6), c.getInt(7), c.getInt(8), c.getFloat(9), c.getInt(10), c.getInt(11)));
                            backUpList.add(new Startgroup(c.getInt(4), c.getString(5), c.getInt(6), c.getInt(7), c.getInt(8), c.getFloat(9), c.getInt(10), c.getInt(11)));
                            ids.put(pos, c.getInt(4));
                            pos++;
                        }
                    }

                    c.close();

                    Cursor comp = db.select("SELECT * FROM Competition WHERE _id=" + competitionId);
                    comp.moveToFirst();

                    co = new Competition(comp.getInt(0), comp.getString(1), comp.getString(2), comp.getString(3), comp.getInt(4));

                    comp.close();

                    detailsOpened = false;
                    preExisting = true;

                    nameLabel.setVisibility(View.VISIBLE);
                    name_row.setVisibility(View.GONE);
                    location_row.setVisibility(View.GONE);
                    datePicker.setVisibility(View.GONE);

                    setData(co);

                    add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            saveCompetition();
                            ArrayList<Integer> id = new ArrayList<Integer>();
                            id.addAll(ids.values());
                            mCallbacks.onStartgroupSelected(-1, competitionId);
                        }
                    });

                    comp.close();
                    db.close();
                } else {

                    add.setVisibility(View.GONE);

                    nameLabel.setVisibility(View.GONE);
                    name_row.setVisibility(View.VISIBLE);
                    location_row.setVisibility(View.VISIBLE);
                    datePicker.setVisibility(View.VISIBLE);
                    cb.setImageResource(android.R.drawable.ic_menu_save);
                    detailsOpened = true;
                    preExisting = false;

                }
            }
            if (getArguments().containsKey("startgroupId")) {

                mData = new ArrayList<Startgroup>();
                backUpList = new ArrayList<Startgroup>();
                ids = new HashMap<Integer, Integer>();

                startgroupId = getArguments().getInt("startgroupId");
                mode = 0;


                if (startgroupId >= 0) {
                    DBHelper db = new DBHelper();
                    c = db.select("SELECT * FROM Startlist JOIN Sportsmen AS sport WHERE sportsmenId=sport._id AND startgroupId =" + startgroupId);

                    int pos = 0;

                    while (c.moveToNext()) {
                        mData.add(new Sportsmen(c.getInt(7), c.getString(8), c.getString(9), c.getString(10), c.getInt(11), c.getInt(12), c.getString(13), c.getString(14)));
                        backUpList.add(new Sportsmen(c.getInt(7), c.getString(8), c.getString(9), c.getString(10), c.getInt(11), c.getInt(12), c.getString(13), c.getString(14)));
                        ids.put(pos, c.getInt(7));
                        pos++;
                    }

                    c.close();

                    Cursor group = db.select("SELECT * FROM Startgroup WHERE _id=" + startgroupId);
                    group.moveToFirst();

                    startgroup = new Startgroup(group.getInt(0), group.getString(1), group.getInt(2), group.getInt(3), group.getInt(4), group.getFloat(5), group.getInt(6), group.getInt(7));
                    startgroup.add(mData);

                    group.close();

                    setData(startgroup);

                    nameLabel.setVisibility(View.VISIBLE);

                    competitionId = getArguments().getInt("competitionId");

                    name_row.setVisibility(View.GONE);

                    detailsOpened = false;
                    preExisting = true;

                    add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ArrayList<Integer> id = new ArrayList<Integer>();
                            id.addAll(ids.values());
                            saveStartgroup();
                            mCallbacks.callStartgroupAddDialog(id, startgroupId);
                        }
                    });

                    group.close();

                    db.close();
                } else {

                    cb.setImageResource(android.R.drawable.ic_menu_edit);

                    mCallbacks.callStartgroupEditDialog(startgroupId, competitionId);
                    preExisting = false;

                    add.setVisibility(View.GONE);
                }

                if (getArguments().containsKey("added")) {

                    DBHelper db = new DBHelper();
                    Cursor cursor;
                    ArrayList<Integer> addedIds = getArguments().getIntegerArrayList("added");

                    int pos = ids.size();

                    for (int i : addedIds) {

                        if (i == -1) {

                            int pin = competitionId * 10000 + startgroupId * 1000 + pos;


                            mData.add(new Sportsmen(pin, "Unknown #" + pos, " ", "", 0, 0, "none", "none"));
                            backUpList.add(new Sportsmen(pin, "Unknown #" + pos, " ", "", 0, 0, "none", "none"));
                            ids.put(pos, pin);
                            pos++;
                            continue;
                        }

                        if (ids.containsValue(i)) {
                            continue;
                        }
                        cursor = db.select("SELECT * FROM Sportsmen WHERE _id=" + i);
                        cursor.moveToFirst();

                        mData.add(new Sportsmen(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4), cursor.getInt(5), cursor.getString(6), cursor.getString(7)));
                        backUpList.add(new Sportsmen(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4), cursor.getInt(5), cursor.getString(6), cursor.getString(7)));
                        ids.put(pos, cursor.getInt(0));
                        pos++;

                    }

                    saveStartgroup();
                }

            }
        }


        adapter = new StableArrayAdapter(getActivity(), R.layout.startlist_element, mData, mode);
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
                switch (mode) {
                    case 1:
                        mCallbacks.onStartgroupSelected(((Startgroup) mData.get(position)).getId(), competitionId);
                        break;
                }
            }
        });

        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mode == 0){
                    mCallbacks.callStartgroupEditDialog(startgroupId,competitionId);
                }else {

                    if (detailsOpened) {
                        cb.setImageResource(android.R.drawable.ic_menu_edit);
                        co.setName(name.getText().toString());
                        co.setLocation(location.getText().toString());
                        co.setDate(datePicker.getDayOfMonth() + "." + datePicker.getMonth() + "." + datePicker.getYear());

                        setData(co);

                        nameLabel.setVisibility(View.VISIBLE);
                        name_row.setVisibility(View.GONE);
                        location_row.setVisibility(View.GONE);
                        datePicker.setVisibility(View.GONE);

                        saveCompetition();

                        detailsOpened = false;
                    } else {
                        cb.setImageResource(android.R.drawable.ic_menu_save);

                        nameLabel.setVisibility(View.GONE);
                        name_row.setVisibility(View.VISIBLE);
                        location_row.setVisibility(View.VISIBLE);
                        datePicker.setVisibility(View.VISIBLE);

                        detailsOpened = true;
                    }
                }
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            // mListener = (OnDialogInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnDialogInteractionListener");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sCallbacks;
        deleteOnDismiss();


    }

    @Override
    public void onPause() {
        super.onPause();
        deleteOnDismiss();

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
            case R.id.competition_save_btn:
                if (mode == 1) {
                    saveCompetition();
                    //getFragmentManager().popBackStack();
                    //mCallbacks.onCompetitionSelected(competitionId);
                } else {
                    saveStartgroup();
                    //getFragmentManager().popBackStack();
                }

                break;
            case R.id.competition_clear_btn:


                if (detailsOpened) {
                    if (mode == 1) {
                        name.setText("");
                        location.setText("");
                    }

                    detailsOpened = false;
                } else {
                    if (mode == 1) {
                        nameLabel.setVisibility(View.GONE);
                        name_row.setVisibility(View.VISIBLE);
                        location_row.setVisibility(View.VISIBLE);
                        datePicker.setVisibility(View.VISIBLE);
                        name.setText("");
                        location.setText("");
                    }

                    detailsOpened = true;
                }

                break;

            case R.id.competition_startlist:

                mCallbacks.showStartlist(competitionId);

                break;
        }

        return true;
    }

    private boolean saveStartgroup() {
        /*competitionId = getArguments().getInt("competitionId");
        if (distance.getText().toString().equals("")) {
            distance.setText("0");
        }
        boolean succes = false;
        if (!name.getText().toString().equals("")) {
            DBHelper db = new DBHelper();

            if (!preExisting) {
                db.insert(new Startgroup(0, name.getText().toString(), timePicker.getCurrentHour(), timePicker.getCurrentMinute(), spinner.getSelectedItemPosition(), Float.parseFloat(distance.getText().toString()), Integer.parseInt(interval.getText().toString()), firstNumber.getValue()));

                Cursor c = db.select("SELECT * FROM Startgroup");
                c.moveToLast();
                startgroupId = c.getInt(0);
                c.close();

                Cursor position = db.select("SELECT * FROM Startgroupmember WHERE startgroupId=" + startgroupId);

                ContentValues val = new ContentValues();
                val.put("competitionId", competitionId);
                val.put("startgroupId", startgroupId);
                val.put("position", position.getCount());
                db.insert("Competitionmember", val);

                preExisting = true;


            } else {
                ContentValues values = new ContentValues();
                values.put("name", name.getText().toString());
                values.put("mode", spinner.getSelectedItemId());
                values.put("distance", Float.parseFloat(distance.getText().toString()));
                values.put("interval", Integer.parseInt(interval.getText().toString()));
                values.put("starthour", timePicker.getCurrentHour());
                values.put("startminute", timePicker.getCurrentMinute());
                values.put("startnum", firstNumber.getValue());
                db.update("Startgroup", values, "_id=" + startgroupId);
            }

            db.delete("Startlist", " startgroupId=" + startgroupId);
            ContentValues values;

            for (Object o : mData) {
                values = new ContentValues();
                values.put("number", firstNumber.getValue() + mData.indexOf(o));
                values.put("startgroupId", startgroupId);
                values.put("sportsmenId", ((Sportsmen) o).getId());
                values.put("startposition", mData.indexOf(o));
                values.put("competitionId", competitionId);
                db.insert("Startlist", values);

            }


            add.setVisibility(View.VISIBLE);
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<Integer> id = new ArrayList<Integer>();
                    id.addAll(ids.values());
                    mCallbacks.callStartgroupAddDialog(id, startgroupId);
                }
            });
            db.close();
        } else {
            Toast.makeText(getActivity(), "A name would be helpful, thanks", Toast.LENGTH_SHORT).show();
        }*/
        return false;//succes;
    }

    private boolean saveCompetition() {

        boolean succes = false;

        if (!name.getText().toString().equals("")) {
            DBHelper db = new DBHelper();

            if (!preExisting) {

                db.insert(new Competition(0, name.getText().toString(), datePicker.getDayOfMonth() + "." + (datePicker.getMonth() + 1) + "." + datePicker.getYear(), location.getText().toString(), 0));
            } else {

                ContentValues values = new ContentValues();
                values.put("name", name.getText().toString());
                values.put("date", datePicker.getDayOfMonth() + "." + (datePicker.getMonth() + 1) + "." + datePicker.getYear());
                values.put("location", location.getText().toString());
                db.update("Competition", values, " _id=" + competitionId);
            }

            db.delete("Competitionmember", " competitionId=" + competitionId);
            ContentValues values;

            for (Object o : mData) {
                values = new ContentValues();
                values.put("startgroupId", ((Startgroup) o).getId());
                values.put("competitionId", competitionId);
                values.put("position", mData.indexOf(o));
                db.insert("Competitionmember", values);

            }

            db.close();
            succes = true;
            add.setVisibility(View.VISIBLE);
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallbacks.onStartgroupSelected(-1, competitionId);
                }
            });

        } else {
            Toast.makeText(getActivity(), "A name would be helpful, thanks", Toast.LENGTH_SHORT).show();
            succes = false;
        }
        return succes;
    }

    private void deleteOnDismiss() {
        if (backUpPosition >= 0 && dismissed) {
            DBHelper db = new DBHelper();
            switch (mode) {
                case 0:
                    db.sqlCommand("DELETE FROM Startgroupmember WHERE sportsmenId=" + ((Sportsmen) backUpList.get(backUpPosition)).getId() + " AND startgroupId=" + startgroupId);
                    db.sqlCommand("DELETE FROM Startlist WHERE startgroupId=" + startgroupId + " AND sportsmenId=" + ((Sportsmen) backUpList.get(backUpPosition)).getId());
                    break;
                case 1:
                    db.sqlCommand("DELETE FROM Competitionmember WHERE startgroupId=" + ((Startgroup) backUpList.get(backUpPosition)).getId() + " AND competitionId=" + competitionId);
                    db.sqlCommand("DELETE FROM Startgroupmember WHERE startgroupId=" + ((Startgroup) backUpList.get(backUpPosition)).getId());
                    db.sqlCommand("DELETE FROM Startlist WHERE startgroupId=" + ((Startgroup) backUpList.get(backUpPosition)).getId() + " AND competitionId=" + competitionId);
                    break;
            }

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

    private void setData(Startgroup group) {

        /*if (group.getName().equals("")) {
            nameLabel.setText("Startgroup");
            nameLabel.setTextColor(getResources().getColor(android.R.color.darker_gray));
        } else {
            nameLabel.setText(group.getName());
            nameLabel.setTextColor(getResources().getColor(android.R.color.black));
        }

        name.setText(group.getName());

        distance.setText(group.getDistance() + "");
        interval.setText(group.getInterval() + "");

        if (group.getStartHour() != 0) {
            timePicker.setCurrentHour(group.getStartHour());
        }

        timePicker.setCurrentMinute(group.getStartMinute());

        if (group.getMode() == 1) {
            interval_row.setVisibility(View.GONE);
            interval.setText("" + 0);
        } else {
            interval_row.setVisibility(View.VISIBLE);
        }
        spinner.setSelection(group.getMode());*/
    }

    private void setData(Competition c) {

        if (c.getName().equals("")) {
            nameLabel.setText("Competition");
            nameLabel.setTextColor(getResources().getColor(android.R.color.darker_gray));
        } else {
            nameLabel.setText(c.getName());
            nameLabel.setTextColor(getResources().getColor(android.R.color.black));
        }

        name.setText(c.getName());
        location.setText(c.getLocation());

        String sub = "";
        int temp = c.getDate().indexOf('.');
        int day = Integer.parseInt(c.getDate().substring(0, c.getDate().indexOf('.')));
        sub = c.getDate().substring(temp + 1, c.getDate().length());
        int month = Integer.parseInt(sub.substring(0, sub.indexOf('.'))) - 1;
        temp = sub.indexOf('.') + 1;
        sub = sub.substring(temp);
        int year = Integer.parseInt(sub);

        Calendar cald = Calendar.getInstance();
        cald.set(year, month, day);
        datePicker.setMinDate(cald.getTimeInMillis() - 1000);

        datePicker.updateDate(year, month, day);
    }
}
