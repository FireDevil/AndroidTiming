package split.timing;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import split.timing.helpers.CircleButton;
import split.timing.helpers.ColorSetter;
import split.timing.helpers.CustomArrayAdapter;
import split.timing.helpers.DBHelper;
import split.timing.helpers.SwipeToDismissListener;
import split.timing.helpers.UndoBarController;
import split.timing.items.Group;
import split.timing.items.Sportsmen;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class GroupFragment extends Fragment implements
        UndoBarController.UndoListener {

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

        public void onSportsSelected(int id);

    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {

        @Override
        public void onSportsSelected(int id) {
        }
    };

    public GroupFragment() {
        // Required empty public constructor
    }

    String groupName = "";
    CustomArrayAdapter adapter;
    ListView lv;

    Cursor groupmember;
    Cursor gId;

    ArrayList<Sportsmen> sportsmens;
    ArrayList<Sportsmen> backUpList;
    int backUpPosition = -1;

    int groupId = -1;

    boolean dismissed = false;

    boolean preExist = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group, container, false);

        lv = (ListView) rootView.findViewById(R.id.group_list);
        TableRow tableRow = (TableRow)rootView.findViewById(R.id.grouplist_header_row);
        final TextView header_text = (TextView) rootView.findViewById(R.id.grouplist_header_text);
        final EditText header_edit = (EditText) rootView.findViewById(R.id.grouplist_header_edit);
        final ImageButton confirm = (ImageButton) rootView.findViewById(R.id.grouplist_header_confirm);
        final ImageButton dismiss = (ImageButton) rootView.findViewById(R.id.grouplist_header_dismiss);
        CircleButton cb = (CircleButton) rootView.findViewById(R.id.group_circle_btn);

        LayerDrawable bgDrawable = (LayerDrawable)tableRow.getBackground();
        GradientDrawable shape = (GradientDrawable) bgDrawable.findDrawableByLayerId(R.id.header_card_border);
        shape.setColor(ColorSetter.newInstance(1));

        DBHelper db = new DBHelper();
        Bundle args;

        if (getArguments() != null) {
            args = getArguments();
        } else {
            return rootView;
        }

        groupId = args.getInt("Groupmember");
        sportsmens = new ArrayList<Sportsmen>();
        backUpList = new ArrayList<Sportsmen>();

        if (groupId >= 0) {

            preExist = true;
            gId = db.select("SELECT name FROM Groups WHERE _id="+groupId);
            gId.moveToFirst();
            groupName = gId.getString(0);
            header_text.setText(groupName);


            header_edit.setVisibility(View.GONE);
            header_text.setVisibility(View.VISIBLE);
            confirm.setVisibility(View.GONE);
            dismiss.setVisibility(View.VISIBLE);

            groupmember = db.select("SELECT * FROM Groupmember  JOIN Sportsmen ON sportsmenId=Sportsmen._id AND Groupmember.groupId =" + groupId);


            while (groupmember.moveToNext()) {
                sportsmens.add(new Sportsmen(groupmember.getInt(3), groupmember.getString(4), groupmember.getString(5), groupmember.getString(6), groupmember.getInt(7), groupmember.getInt(8), groupmember.getString(9), groupmember.getString(10)));
                backUpList.add(new Sportsmen(groupmember.getInt(3), groupmember.getString(4), groupmember.getString(5), groupmember.getString(6), groupmember.getInt(7), groupmember.getInt(8), groupmember.getString(9), groupmember.getString(10)));
            }
        }


        header_edit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                groupName = header_edit.getText().toString();
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (!groupName.equals("")) {
                        groupName = header_edit.getText().toString();
                        header_edit.setVisibility(View.GONE);
                        header_text.setVisibility(View.VISIBLE);
                        header_text.setText(groupName);
                        confirm.setVisibility(View.GONE);
                        dismiss.setVisibility(View.VISIBLE);

                        DBHelper db = new DBHelper();

                        if (preExist) {
                            ContentValues values = new ContentValues();
                            values.put("name", groupName);
                            db.update("Groups", values, "_id=" + groupId);
                        } else {
                            db.insert(new Group(0, groupName));
                            Cursor lastEntry = db.select("SELECT _id FROM Groups");
                            lastEntry.moveToLast();

                            groupId = lastEntry.getInt(0);
                            lastEntry.close();
                        }
                        db.close();
                    } else {
                        Toast.makeText(getActivity(), "A group name would be helpful", Toast.LENGTH_SHORT).show();
                    }


                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                            Context.INPUT_METHOD_SERVICE);

                    imm.hideSoftInputFromWindow(v.getWindowToken(),
                            InputMethodManager.RESULT_UNCHANGED_SHOWN);


                }
                return false;
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupName = header_edit.getText().toString();

                if (!groupName.equals("")) {
                    header_edit.setVisibility(View.GONE);
                    header_text.setVisibility(View.VISIBLE);
                    header_text.setText(groupName);
                    confirm.setVisibility(View.GONE);
                    dismiss.setVisibility(View.VISIBLE);

                    DBHelper db = new DBHelper();

                    if (preExist) {
                        ContentValues values = new ContentValues();
                        values.put("name", groupName);
                        db.update("Groups", values, "_id=" + groupId);
                    } else {
                        db.insert(new Group(0, groupName));
                        Cursor lastEntry = db.select("SELECT _id FROM Groups");
                        lastEntry.moveToLast();

                        groupId = lastEntry.getInt(0);
                        lastEntry.close();
                    }
                    db.close();
                } else {
                    Toast.makeText(getActivity(), "A group name would be helpful", Toast.LENGTH_SHORT).show();
                }

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);

                imm.hideSoftInputFromWindow(v.getWindowToken(),
                        InputMethodManager.RESULT_UNCHANGED_SHOWN);
            }
        });

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                header_edit.setText(groupName);
                header_edit.setVisibility(View.VISIBLE);
                header_text.setVisibility(View.GONE);
                dismiss.setVisibility(View.GONE);
                confirm.setVisibility(View.VISIBLE);

                header_edit.requestFocus();

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);

                imm.showSoftInput(header_edit,0);
            }
        });

        cb.setColor(ColorSetter.newInstance(1));
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartgroupAddDialog sad = new StartgroupAddDialog(sportsmens);
                sad.show(getActivity().getFragmentManager(), "");
            }
        });

        adapter = new CustomArrayAdapter(getActivity(), R.layout.startlist_element, sportsmens, 0);
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
                                if (backUpPosition >= 0 && dismissed && backUpList.get(backUpPosition) != null) {

                                    DBHelper db = new DBHelper();
                                    db.delete("Groupmember", " groupId =" + groupId + " AND sportsmenId=" + ((Sportsmen) backUpList.get(backUpPosition)).getId());

                                    backUpList.remove(backUpPosition);
                                    db.close();

                                }
                                for (int position : reverseSortedPositions) {
                                    sportsmens.remove(position);
                                    backUpPosition = position;
                                    finalMUndoBarController.showUndoBar(
                                            false,
                                            getString(R.string.undo),
                                            null);
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
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mCallbacks.onSportsSelected(sportsmens.get(i).getId());
            }
        });

        return rootView;
    }

    @Override
    public void onUndo(Parcelable token) {
        dismissed = false;
        if (sportsmens.size() == backUpPosition) {
            sportsmens.add(backUpList.get(backUpPosition));
        } else {
            sportsmens.add(backUpPosition, backUpList.get(backUpPosition));
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Activity activity){
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
        DBHelper db = new DBHelper();

        if (sportsmens.size() > 0) {
            if (preExist) {
                if (groupName.equals("")) {
                    Calendar c = Calendar.getInstance();
                    groupName = "Group_" + c.get(Calendar.DAY_OF_MONTH) + "_" + c.get(Calendar.MONTH) + "_" + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + "." + c.get(Calendar.SECOND);
                }
                ContentValues values = new ContentValues();
                values.put("name", groupName);
                db.update("Groups", values, "_id=" + groupId);
            } else {
                if (groupName.equals("")) {
                    Calendar c = Calendar.getInstance();
                    db.insert(new Group(0, "Group_" + c.get(Calendar.DAY_OF_MONTH) + "_" + c.get(Calendar.MONTH) + "_" + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + "." + c.get(Calendar.SECOND)));
                } else {
                    db.insert(new Group(0, groupName));
                }
            }

        }

        if (backUpPosition >= 0 && dismissed) {

            db.delete("Groupmember", " groupId =" + groupId + " AND sportsmenId=" + ((Sportsmen) backUpList.get(backUpPosition)).getId());
            backUpList.remove(backUpPosition);

        }
        db.close();

        mCallbacks = sDummyCallbacks;
    }

}
