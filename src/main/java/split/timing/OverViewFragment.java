package split.timing;

import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import split.timing.helpers.ColorSetter;

/**
 * Created by Antec on 11.12.13.
 */
public class OverViewFragment extends Fragment{

    private static final String ARG_SECTION_NUMBER = "section_number";

    private final int CELL_DEFAULT_HEIGHT = 75;
    private final int NUM_OF_CELLS = 3;

    private ExpandableListView mListView;
    private LinearLayout linear;

    private int open = 0;

    private int groupPos = 0;

    public OverViewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        LinearLayout ll = (LinearLayout)rootView.findViewById(R.id.overview_expandable);
        LayerDrawable bgDrawable = (LayerDrawable)ll.getBackground();
        GradientDrawable shape = (GradientDrawable) bgDrawable.findDrawableByLayerId(R.id.card_border);
        shape.setColor(ColorSetter.newInstance(0));

        final TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setSelected(true);
            }
        });
        textView.setSelected(true);
        //textView.setText("LABEL");

        return rootView;
    }

    public void onAttach(FragmentActivity activity) {
        super.onAttach(activity);
    }

  //  @Override
    public void onClick(View v) {
        registerForContextMenu(v);
        v.showContextMenu();

    }
}
