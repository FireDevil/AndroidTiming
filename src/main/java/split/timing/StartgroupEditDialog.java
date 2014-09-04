package split.timing;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import split.timing.helpers.CircleButton;
import split.timing.helpers.DBHelper;
import split.timing.items.Startgroup;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link split.timing.StartgroupEditDialog.SaveStartgroupCallback} interface
 * to handle interaction events.
 */
public class StartgroupEditDialog extends DialogFragment {

    Spinner spinner;
    TableRow interval_row;
    EditText distance;
    EditText name;
    EditText interval;
    TimePicker timePicker;
    EditText start;
    EditText end;
    NumberPicker hourPicker;
    NumberPicker minutePicker;
    NumberPicker secondPicker;

    CircleButton cb;

    int startgroupId;


    private SaveStartgroupCallback mCallback;

    public static StartgroupEditDialog newInstance(int startgroup, int competition) {
        StartgroupEditDialog dialog = new StartgroupEditDialog();
        Bundle args = new Bundle();
        args.putInt("startgroupId", startgroup);
        args.putInt("competitionId", competition);
        dialog.setArguments(args);

        return dialog;
    }


    public StartgroupEditDialog() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_startgroup_edit, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        startgroupId = getArguments().getInt("startgroupId");

        final String[][] intervals = new String[1][1];

        name = (EditText) rootView.findViewById(R.id.startlist_name_edit);
        distance = (EditText) rootView.findViewById(R.id.startlist_distance_edit);
        spinner = (Spinner) rootView.findViewById(R.id.startlist_version_spinner);
        interval = (EditText) rootView.findViewById(R.id.startlist_interval_edit);
        start = (EditText) rootView.findViewById(R.id.rangestart_edit);
        end = (EditText) rootView.findViewById(R.id.rangend_edit);
        interval_row = (TableRow) rootView.findViewById(R.id.startlist_interval_row);
        cb = (CircleButton) rootView.findViewById(R.id.startlist_circle_btn);

        hourPicker = (NumberPicker) rootView.findViewById(R.id.numberPicker);
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        minutePicker = (NumberPicker) rootView.findViewById(R.id.numberPicker2);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        secondPicker = (NumberPicker) rootView.findViewById(R.id.numberPicker3);
        secondPicker.setMinValue(0);
        secondPicker.setMaxValue(1);

        Calendar calendar = Calendar.getInstance();

        hourPicker.setValue(calendar.get(calendar.HOUR_OF_DAY));
        minutePicker.setValue(calendar.get(calendar.MINUTE));
        minutePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (oldVal == 59 && newVal == 0) {
                    hourPicker.setValue(hourPicker.getValue() + 1);
                }

                if (oldVal == 0 && newVal == 59) {
                    hourPicker.setValue(hourPicker.getValue() - 1);
                }
            }
        });


        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.version_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 1) {
                    interval_row.setVisibility(View.GONE);
                    interval.setText("" + 0);
                } else {
                    interval_row.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveStartgroup();
                dismiss();

            }
        });


        if (startgroupId > 0) {
            DBHelper dbHelper = new DBHelper();
            Cursor cursor = dbHelper.select("SELECT * FROM Startgroup WHERE _id=" + startgroupId);

            name.setText(cursor.getString(1));
            distance.setText(cursor.getDouble(5) + "");
            hourPicker.setValue(cursor.getInt(2));
            minutePicker.setValue(cursor.getInt(3));
            start.setText(cursor.getInt(7) + "");
            interval.setText(cursor.getInt(6));
            spinner.setSelection(cursor.getInt(4), true);

            cursor.close();
            dbHelper.close();
        }

        interval.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {


                    ArrayList<Integer> list = new ArrayList<Integer>();

                    int counter = 0;
                    int time = 0;
                    int intervalTime = Integer.parseInt(interval.getText().toString());
                    while (counter * intervalTime > 60) {
                        list.add(counter * intervalTime);
                        counter++;
                    }

                    secondPicker.setDisplayedValues(list.toArray(new String[list.size()]));
                return false;
            }
        });


        if(intervals.length == 0){
            secondPicker.setDisplayedValues(new String[]{"0", "30"});
        }

        /*LayerDrawable bgDrawable = (LayerDrawable) rootView.getBackground();
        GradientDrawable shape = (GradientDrawable) bgDrawable.findDrawableByLayerId(R.id.card_border);
        shape.setColor(ColorSetter.newInstance(2));*/

        // Inflate the layout for this fragment
        return rootView;
    }

    private void saveStartgroup() {
        int competitionId = getArguments().getInt("competitionId");
        if (distance.getText().toString().equals("")) {
            distance.setText("0");
        }

        if (start.getText().toString().equals("")) {
            start.setText("1");
        }

        if (end.getText().toString().equals("")) {
            end.setText("10");
        }

        if (interval.getText().toString().equals("")) {
            interval.setText("30");
        }

        boolean succes = false;
        if (!name.getText().toString().equals("")) {
            DBHelper db = new DBHelper();

            if (startgroupId > 0) {
                db.insert(new Startgroup(0, name.getText().toString(), timePicker.getCurrentHour(), timePicker.getCurrentMinute(), spinner.getSelectedItemPosition(), Float.parseFloat(distance.getText().toString()), Integer.parseInt(interval.getText().toString()), Integer.parseInt(start.getText().toString())));

                Cursor c = db.select("SELECT * FROM Startgroup");
                c.moveToLast();
                startgroupId = c.getInt(0);
                c.close();


            } else {
                ContentValues values = new ContentValues();
                values.put("name", name.getText().toString());
                values.put("mode", spinner.getSelectedItemId());
                values.put("distance", Float.parseFloat(distance.getText().toString()));
                values.put("interval", Integer.parseInt(interval.getText().toString()));
                values.put("starthour", hourPicker.getValue());
                values.put("startminute", minutePicker.getValue());
                values.put("startnum", Integer.parseInt(start.getText().toString()));
                db.update("Startgroup", values, "_id=" + startgroupId);
            }


        } else {
            Toast.makeText(getActivity(), "A name would be helpful, thanks", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // safety check
        if (getDialog() == null) {
            return;
        }

        /*int dialogWidth = FrameLayout.LayoutParams.WRAP_CONTENT; // specify a value here
        int dialogHeight =1200; // specify a value here

        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);*/

        // ... other stuff you want to do in your onStart() method
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (SaveStartgroupCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }


    public interface SaveStartgroupCallback {

    }

}
