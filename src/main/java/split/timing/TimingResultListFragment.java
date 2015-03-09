package split.timing;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;

import split.timing.helpers.Controller;
import split.timing.helpers.DBHelper;
import split.timing.helpers.ResultExpandableAdapter;
import split.timing.items.ResultGroup;
import split.timing.items.ResultItem;
import split.timing.items.Sportsmen;
import split.timing.items.Startlist;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link split.timing.TimingResultListFragment.Callbacks} interface
 * to handle interaction events.
 * Use the {@link TimingResultListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimingResultListFragment extends Fragment {

    private Callbacks mCallbacks;

    private ExpandableListView lv;
    private ResultExpandableAdapter adapter;
    private ArrayList<ResultGroup> groups;
    private ArrayList<ResultItem> items;


    int lap;
    int group;
    int state;
    int compId;

    Controller controller = Controller.getInstance();

    public static TimingResultListFragment newInstance(int lap, int group, int state) {
        TimingResultListFragment fragment = new TimingResultListFragment();
        Bundle args = new Bundle();
        args.putInt("lap", lap);
        args.putInt("group", group);
        args.putInt("state", state);
        fragment.setArguments(args);
        return fragment;
    }

    public TimingResultListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timing_resultlist, container, false);
        lv = (ExpandableListView) rootView.findViewById(R.id.timing_result_expandable);

        lap = getArguments().getInt("lap");
        group = getArguments().getInt("group");
        state = getArguments().getInt("state");
        compId = controller.getSelectedCompetition();

        groups = new ArrayList<>();

        DBHelper db = new DBHelper();

        if (state == 0 && group == -1) {

            Cursor groupCursor = db.select("SELECT DISTINCT startgroupId FROM Results WHERE competitionId=" + compId);

            while (groupCursor.moveToNext()) {
                group = groupCursor.getInt(0);
                Cursor lapCursor = db.select("SELECT * FROM Results WHERE competitionId = " + compId + " AND lap=" + lap + " AND startgroupId=" + group + " ORDER BY position ASC");

                if (lapCursor.getCount() < 1){
                    continue;
                }
                ResultGroup g = new ResultGroup();

                while (lapCursor.moveToNext()) {

                    if (lapCursor.isFirst()) {
                        g.setName(controller.getGroups().get(lapCursor.getInt(8)).getName());
                        g.setLap(lap);
                        g.setGroup(group);
                        items = new ArrayList<>();
                    }

                    Startlist startlist = controller.getStartlistElements().get(lapCursor.getInt(1));
                    Sportsmen sportsmen = controller.getNumbers().get(startlist.getSportsmenId());

                    ResultItem item = new ResultItem();
                    item.setId(lapCursor.getInt(0));
                    item.setName(sportsmen.getName()+" "+sportsmen.getLastName());
                    item.setNumber(lapCursor.getInt(1)+"");
                    item.setPosition(lapCursor.getInt(2));
                    item.setRun(lapCursor.getLong(4));
                    item.setTimed(lapCursor.getLong(3));
                    item.setDifference(lapCursor.getLong(5));
                    item.setLap(lap);
                    item.setGroup(group);
                    item.setSportsmen(sportsmen);
                    item.setStartlistItem(startlist);
                    items.add(item);
                    g.setList(items);
                }

                    lapCursor.close();
                    groups.add(g);
            }

            groupCursor.close();
        }

        if(state == 1 && lap == -1){
            Cursor lapCursor = db.select("Select * FROM Results WHERE competitionId = " + compId + " AND startgroupId=" + group + " ORDER BY lap, position ASC");

            int lap = -1;
            ResultGroup g = new ResultGroup();

            while (lapCursor.moveToNext()) {

                if(lap != lapCursor.getInt(6)){
                    lap = lapCursor.getInt(6);

                    if(g.getChildrenSize() > 0){
                        groups.add(g);
                    }

                    g = new ResultGroup();
                    g.setName("Lap "+lap);
                    g.setLap(lap);
                    g.setGroup(group);
                    items = new ArrayList<>();
                }else{

                    Startlist startlist = controller.getStartlistElements().get(lapCursor.getInt(1));
                    Sportsmen sportsmen = controller.getNumbers().get(startlist.getSportsmenId());

                    ResultItem item = new ResultItem();
                    item.setId(lapCursor.getInt(0));
                    item.setName(sportsmen.getName()+" "+sportsmen.getLastName());
                    item.setNumber(lapCursor.getInt(1)+"");
                    item.setPosition(lapCursor.getInt(2));
                    item.setRun(lapCursor.getLong(4));
                    item.setTimed(lapCursor.getLong(3));
                    item.setDifference(lapCursor.getLong(5));
                    item.setLap(lap);
                    item.setGroup(group);
                    item.setSportsmen(sportsmen);
                    item.setStartlistItem(startlist);
                    items.add(item);
                    g.setList(items);
                }

                lapCursor.close();
            }
        }

        db.close();

        adapter = new ResultExpandableAdapter(getActivity(), groups,state);
        adapter.setAct(getActivity());
        lv.setAdapter(adapter);



        return rootView;
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

    public interface Callbacks {
    }

}
