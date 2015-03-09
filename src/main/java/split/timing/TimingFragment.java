package split.timing;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import split.timing.helpers.CircleButton;
import split.timing.helpers.Controller;
import split.timing.helpers.TimingGridAdapter;

public class TimingFragment extends Fragment {

    private TimingGridAdapter gridAdapter;
    private int[] arr;

    private AbsListView mSelectGrid;
    private Controller controller = Controller.getInstance();
    private CircleButton set_btn;

    public static TimingFragment newInstance() {
        TimingFragment fragment = new TimingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TimingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timing_center, container, false);

        arr = new int[controller.getStartlistElements().size()];

        int position = 0;
        for(Integer integer : controller.getStartlistElements().keySet()){
            arr[position] = integer.intValue();
            position++;
        }

        gridAdapter = new TimingGridAdapter(getActivity(),arr);

        mSelectGrid = (AbsListView)rootView.findViewById(R.id.timing_center_select_grid);
        mSelectGrid.setAdapter(gridAdapter);
        mSelectGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(id > 0) {
                    setTime((int)id);
                }
            }
        });

        set_btn = (CircleButton) rootView.findViewById(R.id.timing_grid_set_btn);
        set_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime(-1);
            }
        });

        return rootView;
    }

    private void setTime(int number) {
        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(200);

        mCallbacks.newTiming(number);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (Callbacks) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sCallbacks;
    }

    private Callbacks mCallbacks = sCallbacks;

    public interface Callbacks {
        public void newTiming(int number);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sCallbacks = new Callbacks() {
        @Override
        public void newTiming(int number) {

        }

    };

    public void refreshList(){
        gridAdapter.notifyDataSetChanged();
    }
}
