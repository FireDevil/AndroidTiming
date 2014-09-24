package split.timing;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import split.timing.helpers.CircleButton;
import split.timing.helpers.DBHelper;
import split.timing.items.Competition;

/**
 * Created by Antec on 06.09.2014.
 */
public class CompetitionEditDialog extends DialogFragment{

    int compId;

    TextView name;
    TextView location;
    DatePicker picker;
    CircleButton cb;

    private Callbacks mCallbacks = sCallbacks;

    public static CompetitionEditDialog newInstance( int competition) {
        CompetitionEditDialog dialog = new CompetitionEditDialog();
        Bundle args = new Bundle();
        args.putInt("competitionId", competition);
        dialog.setArguments(args);

        return dialog;
    }

    public CompetitionEditDialog() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.fragment_competition_edit,container,false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        compId = getArguments().getInt("competitionId");

        name = (TextView)rootView.findViewById(R.id.competition_name_edit);
        location = (TextView)rootView.findViewById(R.id.competition_location_edit);
        picker = (DatePicker)rootView.findViewById(R.id.competition_datepicker);
        cb = (CircleButton)rootView.findViewById(R.id.competition_circle_btn);

        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(saveCompetition()){
                        dismiss();
                    }
            }
        });

        if(compId > -1) {
            DBHelper db = new DBHelper();
            Cursor cursor = db.select("SELECT * FROM Competition WHERE _id=" + compId);
            cursor.moveToFirst();

            if (cursor.getCount() > -1) {
                name.setText(cursor.getString(1));
                location.setText(cursor.getString(3));

                String date = cursor.getString(2);

                String sub = "";
                int temp = date.indexOf('.');
                int day = Integer.parseInt(date.substring(0, date.indexOf('.')));
                sub = date.substring(temp + 1, date.length());
                int month = Integer.parseInt(sub.substring(0, sub.indexOf('.'))) - 1;
                temp = sub.indexOf('.') + 1;
                sub = sub.substring(temp);
                int year = Integer.parseInt(sub);

                Calendar cald = Calendar.getInstance();
                //noinspection ResourceType
                cald.set(year, month, day);
                picker.setMinDate(cald.getTimeInMillis() - 1000);

                picker.updateDate(year, month, day);
            }
        }

        return rootView;
    }

    private boolean saveCompetition(){
        String cName="";
        String cLocation="";
        String cDate=picker.getDayOfMonth()+"."+picker.getMonth()+"."+picker.getYear();

        if(name.getText().toString().equals("")){
            Toast.makeText(getActivity(), "A name would be helpful, thanks", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            cName = name.getText().toString();
        }

        if(location.getText().toString().equals("")){
            cLocation = getString(R.id.competition_location_text);
        }else{
            cLocation = location.getText().toString();
        }

        DBHelper dbHelper = new DBHelper();
        Competition competition = new Competition(cName,cDate,cLocation);

        if(compId < 0){
            dbHelper.insert(competition);

            Cursor c = dbHelper.select("SELECT * FROM Competition");
            c.moveToLast();
            compId = c.getInt(0);
            c.close();
        }else{

            ContentValues values = new ContentValues();
            values.put("name",cName);
            values.put("location",cLocation);
            values.put("date",cDate);
            dbHelper.update("Competition", values, " _id=" + compId);
        }
        competition.setId(compId);
        mCallbacks.saveCompetition(competition);

        return true;
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
            mCallbacks = (Callbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement Callbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public interface Callbacks {
        public void saveCompetition(Competition c);
    }

    private static Callbacks sCallbacks = new Callbacks(){
        @Override
        public void saveCompetition(Competition c){

        }
    };
}
