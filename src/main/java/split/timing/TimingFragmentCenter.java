package split.timing;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import split.timing.helpers.CircleButton;
import split.timing.helpers.Controller;
import split.timing.helpers.DBHelper;
import split.timing.helpers.Timer;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link split.timing.TimingFragmentCenter.Callbacks} interface
 * to handle interaction events.
 * Use the {@link TimingFragmentCenter#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimingFragmentCenter extends Fragment {

    private Callbacks mCallbacks;
    private ArrayAdapter adapter;

    private MultiAutoCompleteTextView multi;
    private TextView backgroundNumber;
    private TextView position;
    private TextView time;
    private TextView pre;
    private TextView back;
    private CircleButton set_btn;

    private Controller controller = Controller.getInstance();

    public static TimingFragmentCenter newInstance() {
        TimingFragmentCenter fragment = new TimingFragmentCenter();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public TimingFragmentCenter() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timing_center_alt, container, false);
        // Inflate the layout for this fragment

        adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, controller.getStartlistElements().keySet().toArray());

        multi = (MultiAutoCompleteTextView) rootView.findViewById(R.id.timing_center_input);
        multi.setAdapter(adapter);
        multi.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        multi.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if(multi.getText().toString().length() > 0) {
                    if (keyCode == KeyEvent.KEYCODE_0 ||
                            keyCode == KeyEvent.KEYCODE_1 ||
                            keyCode == KeyEvent.KEYCODE_2 ||
                            keyCode == KeyEvent.KEYCODE_3 ||
                            keyCode == KeyEvent.KEYCODE_4 ||
                            keyCode == KeyEvent.KEYCODE_5 ||
                            keyCode == KeyEvent.KEYCODE_6 ||
                            keyCode == KeyEvent.KEYCODE_7 ||
                            keyCode == KeyEvent.KEYCODE_8 ||
                            keyCode == KeyEvent.KEYCODE_9) {
                    }

                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        setTime(readInputField());
                        return true;
                    }

                }

                return false;
            }
        });
        multi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("" + id, "" + position);
            }
        });
        multi.requestFocus();

        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(rootView.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);

        set_btn = (CircleButton) rootView.findViewById(R.id.timing_input_set_btn);
        set_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime(readInputField());
            }
        });

        backgroundNumber = (TextView) rootView.findViewById(R.id.timing_numberlabel);
        position = (TextView) rootView.findViewById(R.id.timing_poslabel);
        time = (TextView) rootView.findViewById(R.id.timing_timelabel);
        pre = (TextView) rootView.findViewById(R.id.timing_frontlabel);
        back = (TextView) rootView.findViewById(R.id.timing_backlabel);

        return rootView;
    }

    private void setTime(int number) {

        if(!controller.getStartlistElements().containsKey(number) && number > 0){
            return;
        }

        multi.getText().clear();

        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 200 milliseconds
        v.vibrate(200);

        mCallbacks.newTiming(number);
    }

    private int readInputField(){

        if(multi.getText().toString().length() < 1){
            return -1;
        }

        return Integer.parseInt(multi.getText().toString());
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (Callbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public int extractInput(){

        int result = -1;

        if(multi.getText().toString().length()<1 || multi.getText().toString().equals(" ")){

        }else{
            result = readInputField();
        }

        multi.getText().clear();

        return result;
    }


    public void setContent(int number, int lap, int pos){

        DBHelper db = new DBHelper();
        Cursor cursor = db.select("SELECT * FROM Resultlist WHERE number="+number+" AND lap="+lap);
        cursor.moveToFirst();

        String diff = Timer.secondsToTime(cursor.getLong(5)/1000).format("%H:%M:%S");
        String times = Timer.secondsToTime(cursor.getLong(4)/1000).format("%d:%H:%M:%S");
        int startgroup = cursor.getInt(10);

        backgroundNumber.setText(""+number);

        time.setText("Time: "+times);
        if(pos == 1) {
            pre.setText("Difference: -" + diff);
        }else{
            pre.setText("Difference: +" + diff);
        }
        back.setText("Lap: "+lap);

        Cursor countCursor = db.select("SELECT * FROM Resultlist WHERE startgroupId="+startgroup+" AND lap="+lap);
        position.setText("Position" +": "+pos+"/"+countCursor.getCount());

        countCursor.close();
        cursor.close();
        db.close();
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface Callbacks {
        public void newTiming(int number);
    }

}
