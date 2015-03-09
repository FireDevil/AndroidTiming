package split.timing;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

import split.timing.helpers.Controller;
import split.timing.helpers.DBHelper;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link split.timing.TimingResultFragment.Callbacks} interface
 * to handle interaction events.
 * Use the {@link TimingResultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimingResultFragment extends Fragment {

    private ViewPager mViewPager;
    private SectionsPagerAdapter mPagerAdapter;
    Controller controller = Controller.getInstance();
    ArrayList<TimingResultListFragment> items;
    HashMap<Integer,Integer> positions;
    int state = 0;

    public static TimingResultFragment newInstance() {
        TimingResultFragment fragment = new TimingResultFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public TimingResultFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_timing_result, container, false);

        items = new ArrayList<>();
        positions = new HashMap<>();

        mViewPager = (ViewPager)rootView.findViewById(R.id.TimingResultPager);
        PagerTitleStrip strip = (PagerTitleStrip)mViewPager.findViewById(R.id.TimingResultPager_Strip);
        mPagerAdapter = new SectionsPagerAdapter();
        mViewPager.setAdapter(mPagerAdapter);

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

    public void refreshDisplay(int state){
        this.state = state;
        mPagerAdapter = new SectionsPagerAdapter();
        mViewPager.setAdapter(mPagerAdapter);
    }

    public void findElement(int pos, int lap, int group){

    }

    private Callbacks mCallbacks;

    public interface Callbacks {
    }


    public class SectionsPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

        ArrayList<String> titles;

        int compId;
        int highLap;
        int lap;
        int group;


        public SectionsPagerAdapter() {
            super(getChildFragmentManager());

            compId = controller.getSelectedCompetition();
            positions = new HashMap<>();

            DBHelper db = new DBHelper();

            if(state == 0) {
                Cursor lapCursor = db.select("SELECT DISTINCT MAX(lap), startgroupId FROM Results WHERE competitionId =" + compId);
                lapCursor.moveToFirst();

                highLap = lapCursor.getInt(0);
                lap = 1;

                titles = new ArrayList<>();

                while (lap <= highLap) {
                    titles.add("Lap " + lap);
                    positions.put(lap-1, lap);
                    lap++;
                }

                lapCursor.close();
                group = -1;
            }

            if(state == 1){
                Cursor groupCursor = db.select("SELECT DISTINCT startgroupId FROM Results WHERE competitionId =" + compId);

                titles = new ArrayList<>();
                items = new ArrayList<>();

                while (groupCursor.moveToNext()){
                    int id = groupCursor.getInt(0);
                    titles.add(controller.getGroups().get(id).getName());
                    positions.put(groupCursor.getPosition(),id);
                }

                groupCursor.close();
                lap =-1;
            }
            db.close();
        }

        @Override
        public Fragment getItem(int position) {

           if(state==0){
               lap = positions.get(position);
               return TimingResultListFragment.newInstance(lap,-1,state);
           }

           if(state == 1){

               group = positions.get(position);

               return TimingResultListFragment.newInstance(-1, group, state);
           }

            return null;

        }

        @Override
        public int getCount() {
            return positions.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }
    }
}
