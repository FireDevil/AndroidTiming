package split.timing;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.Calendar;

import split.timing.helpers.DBHelper;
import split.timing.items.Sportsmen;

public class SportsFragment extends Fragment {

    private Callbacks mCallbacks = sDummyCallbacks;

    int mId;
    public interface Callbacks {
        public void backToList();
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void backToList() {

        }
    };


    public SportsFragment() {
        // Required empty public constructor
    }

    MenuInflater menuInflater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.sports, menu);
        super.onCreateOptionsMenu(menu,menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sports_save_btn:
                DBHelper db = new DBHelper();

                if(name.getText().toString().equals("") || lastName.getText().toString().equals("")){
                    Toast.makeText(getActivity(),getResources().getString(R.string.name_required),Toast.LENGTH_SHORT).show();
                    return false;
                }else{

                    String birthday = dpView.getDayOfMonth()+"."+dpView.getMonth();

                    Calendar cal = Calendar.getInstance();
                    int y = cal.get(Calendar.YEAR);
                    int m = cal.get(Calendar.MONTH);
                    int d = cal.get(Calendar.DAY_OF_MONTH);
                    int a = y - dpView.getYear();

                    if(!birthday.equals("")){
                        try {
                            d = d - Integer.parseInt(birthday.substring(0, birthday.indexOf(".")));
                            m = m - Integer.parseInt(birthday.substring(birthday.indexOf(".") + 1, birthday.length()));
                        }catch(NumberFormatException nfe){
                            d = 1;
                            m = 0;
                        }
                    }

                    if(m < 0){
                        a--;
                    }

                    if(m == 0 && d < 0){
                        a--;
                    }

                    Sportsmen s = new Sportsmen(0,name.getText().toString(),lastName.getText().toString(),birthday,dpView.getYear(),a,club.getText().toString(),fed.getText().toString());

                    if(getArguments()!= null){
                        ContentValues values = new ContentValues();
                        values.put("name",s.getName());
                        values.put("lastname",s.getLastName());
                        values.put("birthday",s.getBirthday());
                        values.put("age",s.getAge());
                        values.put("year",s.getYear());
                        values.put("club",s.getClub());
                        values.put("federation",s.getFederation());
                        db.update("Sportsmen",values,"_id="+mId);
                    }else {
                        db.insert(s);
                    }

                }

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);

                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.RESULT_UNCHANGED_SHOWN);

                mCallbacks.backToList();
                db.close();
                break;
            case R.id.sports_clear_btn:
                name.setText("");
                lastName.setText("");
                club.setText("");
                fed.setText("");
                break;
            default:
                break;
        }

        return false;
    }
    EditText name ;
    EditText lastName;
    EditText club ;
    EditText fed ;
    DatePicker dpView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sports, container, false);

        name = (EditText)rootView.findViewById(R.id.sports_name_edit);
        lastName = (EditText)rootView.findViewById(R.id.sports_surname_edit);
        club = (EditText)rootView.findViewById(R.id.sports_club_edit);
        fed = (EditText)rootView.findViewById(R.id.sports_federation_edit);

        dpView = (DatePicker) rootView.findViewById(R.id.sports_datePicker);
        LinearLayout llFirst = (LinearLayout) dpView.getChildAt(0);
        LinearLayout llSecond = (LinearLayout) llFirst.getChildAt(0);
        for (int i = 0; i < llSecond.getChildCount(); i++) {
            NumberPicker picker = (NumberPicker) llSecond.getChildAt(i); // Numberpickers in llSecond
            // reflection - picker.setDividerDrawable(divider); << didn't seem to work.
            Field[] pickerFields = NumberPicker.class.getDeclaredFields();
            for (Field pf : pickerFields) {
                if (pf.getName().equals("mSelectionDivider")) {
                    pf.setAccessible(true);
                    try {
                        pf.set(picker, getResources().getDrawable(android.R.color.holo_orange_dark));
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
        dpView.updateDate(cal.get(Calendar.YEAR), 0, 1);

        if(getArguments()!= null){
            int id = getArguments().getInt("sportsmenId");
            mId = id;
            if(id >= 0) {
                DBHelper db = new DBHelper();
                Cursor c = db.select("SELECT * FROM Sportsmen WHERE _id=" + id);
                c.moveToFirst();

                int day = 1;
                int month = 0;
                if (!c.getString(3).equals("")) {
                    try {
                        day = Integer.parseInt(c.getString(3).substring(0, c.getString(3).indexOf(".")));
                        month = Integer.parseInt(c.getString(3).substring(c.getString(3).indexOf(".") + 1, c.getString(3).length()));
                    } catch (NumberFormatException nfe) {
                        day = 1;
                        month = 0;
                    }
                }

                name.setText(c.getString(1));
                lastName.setText(c.getString(2));
                club.setText(c.getString(6));
                fed.setText(c.getString(7));
                dpView.updateDate(c.getInt(4), month, day);

                c.close();
                db.close();
            }
        }

        return rootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            //mListener = (OnDialogInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnDialogInteractionListener");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
        mCallbacks = sDummyCallbacks;
    }

    /*public interface OnDialogInteractionListener {
        public void onDialogInteraction(Uri uri);
    }*/

}
