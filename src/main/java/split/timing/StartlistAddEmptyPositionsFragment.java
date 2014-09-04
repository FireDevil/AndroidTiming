package split.timing;



import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class StartlistAddEmptyPositionsFragment extends Fragment {

    private Callbacks mCallbacks = sCallbacks;

    public interface Callbacks {
        public void onValueChanged(int value);
    }

    private static Callbacks sCallbacks = new Callbacks() {

        @Override
        public void onValueChanged(int value) {

        }
    };

    public static StartlistAddEmptyPositionsFragment newInstance() {
        StartlistAddEmptyPositionsFragment fragment = new StartlistAddEmptyPositionsFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add_empty_positions, container, false);
        NumberPicker picker = (NumberPicker)rootView.findViewById(R.id.numberPicker);

        picker.setMaxValue(100);
        picker.setMinValue(0);
        picker.setValue(0);
        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mCallbacks.onValueChanged(newVal);
            }
        });
        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sCallbacks;
    }



}
