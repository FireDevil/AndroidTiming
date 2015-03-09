package split.timing;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import split.timing.helpers.Controller;
import split.timing.helpers.DBHelper;
import split.timing.helpers.SwipeToDismissListener;
import split.timing.helpers.TimedAdapter;
import split.timing.helpers.UndoBarController;
import split.timing.items.Timed;

/**
 * Created by Antec on 03.06.2014.
 */
public class TimingDrawerRight extends Fragment implements
        UndoBarController.UndoListener{

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
    private RightDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private TextView defaultText;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    private int competitionId = -1;
    int backUpPosition = -1;
    int backId = -1;
    ArrayList<Timed> backUpList;

    boolean dismissed = false;
    ArrayList<Timed> numbers;

    private TimedAdapter adapter;
    UndoBarController undoBarController;

    Controller controller = Controller.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(false);
        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.timing_drawer_right,container,false);
        competitionId = controller.getSelectedCompetition();

        mDrawerListView = (ListView)rootView.findViewById(R.id.timing_drawer_right_listview);
        defaultText  = (TextView)rootView.findViewById(R.id.timing_drawer_right_default_text);

        createList(true);

        adapter.setNotifyOnChange(true);

        undoBarController = new UndoBarController(rootView.findViewById(R.id.timing_drawer_right_undobar), this);


        final SwipeToDismissListener dismissListener = new SwipeToDismissListener(mDrawerListView,
                new SwipeToDismissListener.DismissCallbacks() {
                    @Override
                    public boolean canDismiss(int position) {
                        return true;
                    }

                    @Override
                    public void onDismiss(ListView listView, int[] reverseSortedPositions) {


                        deleteOnDismiss();

                        for (int position : reverseSortedPositions) {

                            if(numbers.size() >= position) {

                                if (numbers.size() > 0) {
                                    backId = numbers.get(position).getId();
                                }
                                
                                String showText = numbers.get(position).getNumber()+"";
                                if(showText.equals("-1")){
                                    showText = getString(R.string.empty_time);
                                }

                                backUpPosition = position;
                                undoBarController.showUndoBar(
                                        false,
                                        showText,
                                        null);

                                dismissed = true;
                                controller.cutLapCounter(numbers.get(position).getNumber());
                                numbers.remove(position);
                                mCallbacks.refreshQuickSelect(0);
                                checkDefaultText();

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        deleteOnDismiss();
                                    }
                                }, 5000);
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }
                }
        );

        mDrawerListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                dismissListener.setEnabled(true);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                dismissListener.setEnabled(false);
            }
        });

        mDrawerListView.setOnTouchListener(dismissListener);
        mDrawerListView.setAdapter(adapter);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(numbers.get(position).getNumber() < 0){
                    mCallbacks.showTimedDetailDialog(numbers.get(position).getId(),numbers.get(position).getNumber(),-1,position);
                }else{
                    mCallbacks.showTimedDetailDialog(numbers.get(position).getId(),numbers.get(position).getNumber(),controller.getStartlistElements().get(numbers.get(position).getNumber()).getId(),position);
                }
            }
        });

        checkDefaultText();

        return rootView;
    }

    private void createList(boolean counter) {
        numbers = new ArrayList<Timed>();
        backUpList = new ArrayList<Timed>();

        DBHelper dbHelper = new DBHelper();
//        Cursor cursor = dbHelper.select("SELECT * FROM Timed JOIN Startlist WHERE Timed.number = Startlist.number OR Timed.number='-1' AND Startlist.competitionId ="+competitionId +" ORDER BY Timed.timed");
        Cursor cursor = dbHelper.select("SELECT * FROM Timed WHERE competitionId ="+competitionId +" ORDER BY timed DESC");

        while (cursor.moveToNext()){
            Timed timed = new Timed();
            timed.setId(cursor.getInt(0));
            timed.setNumber(cursor.getInt(1));
            timed.setTimedInMillis(Long.parseLong(cursor.getString(2)));
            timed.setRunInMillis(Long.parseLong(cursor.getString(3)));
            timed.setLap(cursor.getInt(4));

            numbers.add(cursor.getPosition(), timed);
            backUpList.add(cursor.getPosition(),timed);

            if(counter) {
                controller.putLapCounter(cursor.getInt(1));
            }
        }

        adapter = new TimedAdapter(getActivity(), numbers);

        cursor.close();
        dbHelper.close();
    }

    public boolean isDrawerOpen() {
        ((ArrayAdapter)mDrawerListView.getAdapter()).notifyDataSetChanged();
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
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow_right, GravityCompat.END);
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (RightDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
        deleteOnDismiss();
    }

    @Override
    public void onPause() {
        super.onPause();
        deleteOnDismiss();
    }

    @Override
    public void onResume(){
        super.onResume();

        createList(false);
        mDrawerListView.setAdapter(adapter);
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

        View focus = getActivity().getCurrentFocus();
        mDrawerLayout.openDrawer(Gravity.END);

        focus.requestFocus();
    }

    public void closeDrawer(){
        mDrawerLayout.closeDrawer(Gravity.END);
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

    private void checkDefaultText(){

        if(numbers.size()== 0){
            mDrawerListView.setVisibility(View.GONE);
            defaultText.setVisibility(View.VISIBLE);
        }else{
            mDrawerListView.setVisibility(View.VISIBLE);
            defaultText.setVisibility(View.GONE);
        }
    }

    public void setTime(Timed number){

        numbers.add(0,number);
        backUpList.add(0,number);
        checkDefaultText();
        adapter.notifyDataSetChanged();
        mCallbacks.refreshQuickSelect(number.getNumber());
    }


    public void changeListItem(int position, int number, int timedId){

        Timed t = new Timed();
        DBHelper db = new DBHelper();
        Cursor c = db.select("SELECT * FROM Timed WHERE _id="+timedId);
        c.moveToFirst();

        t.setId(timedId);
        t.setLap(c.getInt(4));
        t.setNumber(number);
        t.setRunInMillis(c.getLong(3));
        t.setTimedInMillis(c.getLong(2));

        c.close();
        db.close();

        numbers.remove(position);
        backUpList.remove(position);
        numbers.add(position,t);
        backUpList.add(position,t);

        checkDefaultText();
        adapter.notifyDataSetChanged();
    }

    public void deleteListItem(int position){
        if(!dismissed) {
            controller.cutLapCounter(numbers.get(position).getNumber());
            numbers.remove(position);
            backUpList.remove(position);
        }else {
            dismissed = false;
        }

        checkDefaultText();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onUndo(Parcelable token) {
        dismissed = false;

        if(numbers.size() == 0){
            numbers.addAll(backUpList);

        }else {

            if (numbers.size() == backUpPosition) {
                numbers.add(backUpList.get(backUpPosition));
                //adapter.addItem(backUpList.get(backUpPosition),numbers.size());
            } else {
                numbers.add(backUpPosition, backUpList.get(backUpPosition));
                //adapter.addItem(backUpList.get(backUpPosition),backUpPosition);
            }

            controller.putLapCounter(backUpList.get(backUpPosition).getNumber());
        }

        checkDefaultText();

        adapter.notifyDataSetChanged();
    }

    private void deleteOnDismiss() {
        if (backUpPosition >= 0 && dismissed) {
            DBHelper db = new DBHelper();
            db.sqlCommand("DELETE FROM Timed WHERE _id="+backUpList.get(backUpPosition).getId());
            db.close();

            mCallbacks.onItemDelete(backUpList.get(backUpPosition).getNumber());
            backUpList.remove(backUpPosition);

        }

        checkDefaultText();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface RightDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        public void refreshQuickSelect(int input);

        void showTimedDetailDialog(int timedId, int number, int startlistId, int position);

        void onItemDelete(int number);
    }
}
