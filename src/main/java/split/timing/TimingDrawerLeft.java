package split.timing;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.format.Time;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import split.timing.helpers.ColorSetter;
import split.timing.helpers.Controller;
import split.timing.helpers.StartlistAdapter;
import split.timing.helpers.Timer;
import split.timing.items.Competition;
import split.timing.items.Startgroup;
import split.timing.items.Startlist;

/**
 * Created by Antec on 03.06.2014.
 */
public class TimingDrawerLeft extends Fragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private LeftDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    long r = 0;
    TextView ref;
    TextView defaulte;
    Chronometer tick;
    Time a;
    Time b;
    int change = 0;
    boolean delete = false;

    Competition competition;
    String date;
    ArrayList<Startlist> mData;

    StartlistAdapter adapter;

    Controller controller = Controller.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(false);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.timing_drawer_left, container, false);
        mDrawerListView = (ListView) rootView.findViewById(R.id.timing_drawer_left_listView);
        mDrawerListView.setVisibility(View.VISIBLE);

        mData = new ArrayList<Startlist>();

        defaulte = (TextView)rootView.findViewById(R.id.timing_drawer_left_default_text);
        defaulte.setVisibility(View.GONE);
        ref = (TextView) rootView.findViewById(R.id.timing_drawer_left_chronoText);
        ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });


        tick = (Chronometer) rootView.findViewById(R.id.timing_drawer_left_chrono);
        tick.setVisibility(View.GONE);
        tick.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {

            @Override
            public void onChronometerTick(Chronometer arg0) {

                if (delete && mData.size() > 0) {
                    mData.remove(0);
                    adapter.notifyDataSetChanged();
                    delete = false;
                    if(mData.size() > 0) {
                        b = null;
                    }
                }

                if (mData.size() > 0) {

                    Startlist startlist = mData.get(0);

                    if(b == null){

                        Startgroup startgroup = controller.getGroups().get(startlist.getStartId());

                        int difference = startlist.getDifference() + (startgroup.getStartHour() * 3600) + (startgroup.getStartMinute() * 60) + startgroup.getStartSecond();
                        int hour = difference / 3600;
                        int minute = (difference % 3600) / 60;
                        int second = (difference % 3600) % 60;

                        int[] arr = controller.parseDate(date);
                        int day = arr [0];
                        if(hour > 23){
                            day = day + hour/24;
                            hour = hour%24;
                        }
                        int month = arr [1];
                        int year = arr [2];

                        b = new Time();
                        b.setToNow();
                        b.year = year;
                        b.month = month;
                        b.monthDay = day;
                        b.hour = hour;
                        b.minute = minute;
                        b.second = second;
                    }

                    a.setToNow();
                    String timeText;

                    if (a.before(b)) {

                        if( b.toMillis(true) - a.toMillis(true) < 11000){
                            ref.setTextColor(ColorSetter.newInstance(2));
                        }else{
                            ref.setTextColor(getResources().getColor(android.R.color.white));
                        }

                        timeText = "-" + Timer.substractTimes(b.toMillis(true), a.toMillis(true)).format("%d:%H:%M:%S");

                    } else {


                        timeText = Timer.substractTimes(b.toMillis(true), a.toMillis(true)).format("%d:%H:%M:%S");
                        ref.setTextColor(ColorSetter.newInstance(1));
                        change++;

                        if (change >= startlist.getDifference()/2 || startlist.getDifference() < 5) {
                            delete = true;
                            change = 0;
                        }
                    }


                    ref.setText(timeText);
                } else {
                    a.setToNow();
                    ref.setTextColor(getResources().getColor(android.R.color.white));

                    mDrawerListView.setVisibility(View.GONE);
                    defaulte.setVisibility(View.VISIBLE);
                    ref.setText(Timer.substractTimes(b.toMillis(true), a.toMillis(true)).format("%d:%H:%M:%S"));
                }
            }
        });

        return rootView;
    }

    public boolean isDrawerOpen() {
        adapter.notifyDataSetChanged();
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(false);

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    public void startChrono() {
        tick.start();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (LeftDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void openDrawer(){
        mDrawerLayout.openDrawer(Gravity.START);
    }

    public void closeDrawer(){
        mDrawerLayout.closeDrawer(Gravity.START);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
    }

    private ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

    public void setupStartlist(ArrayList<Startlist> startlistElements) {
        competition = controller.getCompetitions().get(controller.getSelectedCompetition());
        date = competition.getDate();
        a = new Time();
        b = new Time();

        for (Startlist startlist : startlistElements) {


            Startgroup startgroup = controller.getGroups().get(startlist.getStartId());

            int difference = startlist.getDifference() + (startgroup.getStartHour() * 3600) + (startgroup.getStartMinute() * 60) + startgroup.getStartSecond();
            int hour = difference / 3600;
            int minute = (difference % 3600) / 60;
            int second = (difference % 3600) % 60;

            int[] arr = controller.parseDate(date);

            int day = arr[0];
            if(hour > 23){
                day = day + hour/24;
                hour = hour%24;

            }
            int month = arr[1];
            int year = arr[2];

            a.setToNow();
            b.setToNow();
            b.year = year;
            b.month = month;
            b.monthDay = day;
            b.hour = hour;
            b.minute = minute;
            b.second = second;

            if (b.after(a)) {
                mData.add(startlist);
            }

        }

        if(mData.size() > 0){
            b = null;
        }

        adapter = new StartlistAdapter(getActivity(), R.layout.timing_startlist_element, mData);
        mDrawerListView.setAdapter(adapter);

        startChrono();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface LeftDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
    }
}
