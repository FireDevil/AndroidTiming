package split.timing;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import split.timing.helpers.DBHelper;
import split.timing.items.Sportsmen;
import split.timing.items.Startgroup;

public class StartgroupAddDialog extends DialogFragment {

    private OnDialogInteractionListener mListener;
    private boolean isStartgroup = false;
    private int competitionId;

    public StartgroupAddDialog() {
        // Required empty public constructor
    }

    public StartgroupAddDialog(ArrayList<Sportsmen> sportsmen) {
        DBHelper db = new DBHelper();
        Cursor c = db.select("SELECT * FROM Sportsmen");

        while (c.moveToNext()) {
            Sportsmen tmp = new Sportsmen(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getInt(4), c.getInt(5), c.getString(6), c.getString(7));
            boolean existing = false;

            for (Sportsmen s : sportsmen) {
                if (tmp.getId() == s.getId()) {
                    existing = true;
                }
            }

            if (!existing) {
                map.put(itemList.size(), tmp.getId());
                itemList.add(tmp.getName() + "\n" + tmp.getLastName());
            }
        }
    }

    public StartgroupAddDialog(ArrayList<Startgroup> startgroups, int competitionId) {
        DBHelper db = new DBHelper();
        Cursor c = db.select("SELECT * FROM Startgroup");
        this.isStartgroup = true;
        this.competitionId = competitionId;

        while (c.moveToNext()) {
            Startgroup tmp = new Startgroup(c.getInt(0), c.getString(1), c.getInt(2), c.getInt(3), c.getInt(4), c.getInt(5),c.getFloat(6), c.getInt(7),c.getInt(8));
            boolean existing = false;

            for (Startgroup sg : startgroups) {
                if (tmp.getId() == sg.getId()) {
                    existing = true;
                }
            }

            if (!existing) {
                map.put(itemList.size(), tmp.getId());
                itemList.add(tmp.getName());
            }
        }


    }

    ArrayList<String> itemList = new ArrayList<String>();
    ArrayList<Integer> mSelectedItems;
    HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
    String[] sportsArray;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (itemList.size() == 0) {

            builder.setMessage("No elements available for import");

            return builder.create();
        }

        mSelectedItems = new ArrayList<Integer>();
        sportsArray = itemList.toArray(new String[itemList.size()]);

        View v = getActivity().getLayoutInflater().inflate(android.R.layout.simple_list_item_1, null);
        TextView t = (TextView) v.findViewById(android.R.id.text1);
        t.setText("Select...");
        t.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setCustomTitle(v)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(sportsArray, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    mSelectedItems.add(which);
                                } else if (mSelectedItems.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    mSelectedItems.remove(Integer.valueOf(which));
                                }
                            }
                        }
                )
                        // Add action buttons
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (isStartgroup) {
                            mListener.onDialogCallback(mSelectedItems,map,competitionId);
                        } else {
                            mListener.onDialogInteraction(mSelectedItems, map);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        StartgroupAddDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnDialogInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnDialogInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnDialogInteractionListener {
        public void onDialogInteraction(ArrayList<Integer> selectedItems, HashMap<Integer, Integer> idMap);
        public void onDialogCallback(ArrayList<Integer> selectedItems, HashMap<Integer,Integer> idMap, int competitionId);
    }

}
