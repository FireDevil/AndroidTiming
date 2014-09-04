package split.timing;


import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.Calendar;

import split.timing.helpers.ColorSetter;
import split.timing.helpers.DBHelper;
import split.timing.items.Competition;

public class CompetitionFragment extends Fragment {

    DatePicker datePicker;
    EditText name;
    EditText location;

    public CompetitionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_competition,container,false);

        datePicker = (DatePicker)rootView.findViewById(R.id.competition_datepicker);
        name = (EditText)rootView.findViewById(R.id.competition_name_edit);
        location = (EditText)rootView.findViewById(R.id.competition_location_edit);

        LayerDrawable bgDrawable = (LayerDrawable)datePicker.getBackground();
        GradientDrawable shape = (GradientDrawable) bgDrawable.findDrawableByLayerId(R.id.card_border);
        shape.setColor(ColorSetter.newInstance(2));

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
                        pf.set(picker, getResources().getDrawable(android.R.color.holo_red_dark));
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

        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
        datePicker.setMinDate(cal.getTimeInMillis()-1000);

        if(getArguments()!=null){
            int competitionId = getArguments().getInt("competitionId");

            Log.e("Competition", "" + competitionId);

            DBHelper db = new DBHelper();
            Cursor c = db.select("SELECT * FROM Competition WHERE _id="+competitionId);
            c.moveToFirst();

            name.setText(c.getString(1));
            location.setText(c.getString(3));

            String sub ="";
            int temp = c.getString(2).indexOf(".");
            int day = Integer.parseInt(c.getString(2).substring(0,c.getString(2).indexOf(".")));
            sub = c.getString(2).substring(temp+1,c.getString(2).length()-1);
            int month = Integer.parseInt(sub.substring(0, sub.indexOf(".")));
            temp = sub.indexOf(".")+1;
            sub = sub.substring(temp);
            int year = Integer.parseInt(sub);

            datePicker.updateDate(year,month,day);

            c.close();
            db.close();

        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.competition, menu);
        super.onCreateOptionsMenu(menu,menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.competition_save_btn:
                if(!name.getText().toString().equals("")) {
                    DBHelper db = new DBHelper();
                    db.insert(new Competition(0, name.getText().toString(), datePicker.getDayOfMonth() + "." + datePicker.getMonth() + "." + datePicker.getYear(), location.getText().toString(), 0));
                    db.close();
                }else{
                    Toast.makeText(getActivity(),"A name would be helpful, thanks",Toast.LENGTH_SHORT).show();
                }

                //getFragmentManager().popBackStack();
                break;
            case R.id.competition_clear_btn:
                name.setText("");
                location.setText("");
                break;
        }




        return true;
    }

}
