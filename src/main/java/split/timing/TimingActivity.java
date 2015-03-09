package split.timing;

import android.app.ActionBar;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.nineoldandroids.view.animation.AnimatorProxy;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import split.timing.helpers.CircleButton;
import split.timing.helpers.ColorSetter;
import split.timing.helpers.Controller;
import split.timing.helpers.DBHelper;
import split.timing.helpers.ResultExpandableAdapter;
import split.timing.helpers.Timer;
import split.timing.items.Competition;
import split.timing.items.Sportsmen;
import split.timing.items.Startgroup;
import split.timing.items.Startlist;
import split.timing.items.Timed;


public class TimingActivity extends FragmentActivity implements
        TimingDrawerLeft.LeftDrawerCallbacks,
        TimingDrawerRight.RightDrawerCallbacks,
        TimingFragment.Callbacks,
        TimingFragmentCenter.Callbacks,
        TimingResultFragment.Callbacks,
        TimingResultListFragment.Callbacks,
        ResultExpandableAdapter.Callbacks,
        TimedDetailDialog.Callbacks{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private TimingFragment mTimingFragment;
    private TimingFragmentCenter mCenterFragment;
    private TimingResultFragment mResultFragment;
    private TimingDrawerLeft mDrawerLeft;
    private TimingDrawerRight mDrawerRight;
    private SlidingUpPanelLayout mLayout;

    private TimedDetailDialog timedDetailDialog;

    Menu mMenu;

    public static final String SAVED_STATE_ACTION_BAR_HIDDEN = "saved_state_action_bar_hidden";

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    Controller controller = Controller.getInstance();
    int competitionId = -1;
    Competition competition;
    boolean slideUp = false;
    int selectedTimedItem = -1;
    int selectedTimedNumber = -1;
    int selectedTimedId = -1;
    Timed timedObject;

    int state = 0;

    private TextView slideup_name;
    private TextView slideup_number;
    private TextView slideup_time;
    private TextView slideup_diff;
    private TextView slideup_position;
    private CircleButton position_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_timing);

        getActionBar().setDisplayHomeAsUpEnabled(false);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayUseLogoEnabled(false);
        getActionBar().setDisplayShowCustomEnabled(false);
        getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        getActionBar().setBackgroundDrawable(new ColorDrawable(ColorSetter.newInstance(1)));


        mTimingFragment = TimingFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, mTimingFragment)
                .commit();

        mResultFragment = TimingResultFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.slideUpContainer, mResultFragment)
                .commit();

        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
                setActionBarTranslation(mLayout.getCurrentParalaxOffset());

            }

            @Override
            public void onPanelCollapsed(View view) {
                slideUp = false;
                onCreateOptionsMenu(mMenu);
                invalidateOptionsMenu();
            }

            @Override
            public void onPanelExpanded(View view) {
                slideUp = true;
                onCreateOptionsMenu(mMenu);
                invalidateOptionsMenu();
            }

            @Override
            public void onPanelAnchored(View view) {

            }

            @Override
            public void onPanelHidden(View view) {

            }
        });

        mDrawerLeft = (TimingDrawerLeft)
                getFragmentManager().findFragmentById(R.id.timing_drawer_left);

        mDrawerRight = (TimingDrawerRight)
                getFragmentManager().findFragmentById(R.id.timing_drawer_right);
        mTitle = getTitle();


        // Set up the drawer.
        mDrawerLeft.setUp(
                R.id.timing_drawer_left,
                (DrawerLayout) findViewById(R.id.timing_drawer_layout));
        mDrawerRight.setUp(
                R.id.timing_drawer_right,
                (DrawerLayout) findViewById(R.id.timing_drawer_layout));

        boolean actionBarHidden = savedInstanceState != null && savedInstanceState.getBoolean(SAVED_STATE_ACTION_BAR_HIDDEN, false);
        if (actionBarHidden) {
            int actionBarHeight = getActionBarHeight();
            setActionBarTranslation(-actionBarHeight);//will "hide" an ActionBar
        }

        if (savedInstanceState == null) {
        }

        if (getIntent().getIntExtra("Competition", 0) > 0) {

            competitionId = getIntent().getIntExtra("Competition", 0);
            controller.setSelectedCompetition(competitionId);

            ArrayList<Startgroup> startgroups = new ArrayList<Startgroup>();
            ArrayList<Startlist> startlistElements = new ArrayList<Startlist>();

            DBHelper db = new DBHelper();
            Cursor c = db.select("SELECT * FROM Competitionmember JOIN Startgroup WHERE startgroupId=Startgroup._id AND competitionId =" + competitionId);

            if (c.getCount() > 0) {

                while (c.moveToNext()) {
                    startgroups.add(new Startgroup(c.getInt(4), c.getString(5), c.getInt(6), c.getInt(7), c.getInt(8), c.getInt(9), c.getFloat(10), c.getInt(11), c.getInt(12)));
                }
            }

            controller.setStartgroups(startgroups);
            c.close();


            for (Startgroup startgroup : startgroups) {
                c = db.select("SELECT * FROM Startlist WHERE Startlist.startgroupId=" + startgroup.getId() + " ORDER BY number");
                if (c.getCount() > 0) {

                    while (c.moveToNext()) {
                        boolean jersey = Boolean.parseBoolean(c.getString(2));
                        startlistElements.add(new Startlist(c.getInt(0), c.getInt(1), jersey, c.getInt(3), c.getInt(4), c.getInt(5), c.getInt(6), c.getInt(7)));
                    }
                }
                c.close();
            }

            controller.setStartlistElements(startlistElements);

            controller.loadSportsmen();

            mDrawerLeft.setupStartlist(startlistElements);

            slideup_name = (TextView) findViewById(R.id.slideup_name);
            slideup_number = (TextView) findViewById(R.id.slideup_number);
            slideup_diff = (TextView) findViewById(R.id.slideup_difference);
            slideup_position = (TextView) findViewById(R.id.slideup_position);
            slideup_time = (TextView) findViewById(R.id.slideup_time);
            position_btn = (CircleButton) findViewById(R.id.timing_position_btn);

        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_STATE_ACTION_BAR_HIDDEN, mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED);

    }


    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        if (slideUp) {
            inflater.inflate(R.menu.timing_result,menu);
//            inflater.inflate(R.menu.timing, menu);
        } else {
            inflater.inflate(R.menu.timing, menu);
        }

        mMenu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

//        if (item.getItemId() == R.id.timing_alt_pager) {
//            resultChanged = !resultChanged;
//
//            if(resultChanged){
//                state = 0;
//                item.setIcon(R.drawable.ic_rotate_left_white_48dp);
//            }else {
//                state = 1;
//                item.setIcon(R.drawable.ic_rotate_right_grey600_48dp);
//            }
//
//            mResultFragment.refreshDisplay(state);
//        }

        if (item.getItemId() == R.id.timing_alt_layout && !slideUp) {

            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);

            if (getSupportFragmentManager().findFragmentById(R.id.container) instanceof TimingFragment) {
                mCenterFragment = TimingFragmentCenter.newInstance();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, mCenterFragment)
                        .commit();
                item.setIcon(R.drawable.ic_flip_to_front_white_48dp);
            } else {
                mTimingFragment = TimingFragment.newInstance();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, mTimingFragment)
                        .commit();
                item.setIcon(R.drawable.ic_flip_to_back_white_48dp);

            }

        }

        if (item.getItemId() == R.id.timing_startlist_list) {

            if (!mDrawerLeft.isDrawerOpen()) {
                mDrawerRight.closeDrawer();
                mDrawerLeft.openDrawer();
            } else {
                mDrawerLeft.closeDrawer();
            }
        }

        if (item.getItemId() == R.id.timing_timed_list) {
            if (!mDrawerRight.isDrawerOpen()) {
                mDrawerLeft.closeDrawer();
                mDrawerRight.openDrawer();
            } else {
                mDrawerRight.closeDrawer();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private int getActionBarHeight() {
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    public void setActionBarTranslation(float y) {
        // Figure out the actionbar height
        int actionBarHeight = getActionBarHeight();
        // A hack to add the translation to the action bar
        ViewGroup content = ((ViewGroup) findViewById(android.R.id.content).getParent());
        int children = content.getChildCount();
        for (int i = 0; i < children; i++) {
            View child = content.getChildAt(i);
            if (child.getId() != android.R.id.content) {
                if (y <= -actionBarHeight) {
                    child.setVisibility(View.GONE);
                } else {
                    child.setVisibility(View.VISIBLE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        child.setTranslationY(y);
                    } else {
                        AnimatorProxy.wrap(child).setTranslationY(y);
                    }
                }
            }
        }
    }

    @Override
    public void newTiming(int number) {

        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vib.vibrate(200);

        Timed timed = Timer.newTiming(number);

        if (number > 0) {

            Sportsmen sportsmen = controller.getNumbers().get(controller.getStartlistElements().get(timed.getNumber()).getSportsmenId());

            slideup_name.setText(sportsmen.getLastName()+", "+sportsmen.getName());
            slideup_number.setText(""+number);
            slideup_number.setBackgroundDrawable(getResources().getDrawable(R.drawable.trikot_transparent));
            new CalculateResults().execute(number);

        }else{
            slideup_name.setText(""+getResources().getString(R.string.noNumber));
            slideup_number.setText("--");
            slideup_number.setBackgroundDrawable(null);
            slideup_position.setText("X");

            SimpleDateFormat sdf = new SimpleDateFormat("@ HH:mm:ss");//new SimpleDateFormat("dd.MM.yyyy @ HH:mm:ss");
            Date date = new Date(timed.getTimedInMillis());

            slideup_time.setText(sdf.format(date));
            slideup_time.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            slideup_diff.setText("");
            slideup_diff.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

        }

        mDrawerRight.setTime(timed);
    }

    @Override
    public void refreshQuickSelect(int input) {
        if (getSupportFragmentManager().findFragmentById(R.id.container) instanceof TimingFragment) {
            mTimingFragment.refreshList();
        }
    }

    @Override
    public void showTimedDetailDialog(int timedId, int number, int startlistId, int position) {
        selectedTimedItem = position;
        selectedTimedNumber = number;
        selectedTimedId = timedId;
        timedObject = controller.getTimedObject(timedId);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment previous = getSupportFragmentManager().findFragmentByTag("timedDialog");

        if (previous != null) {
            ft.remove(previous);
        }

        ft.addToBackStack(null);

        timedDetailDialog = TimedDetailDialog.newInstance(timedId, number, startlistId);
        timedDetailDialog.show(ft, "timedDialog");

    }

    @Override
    public void onItemDelete(int number) {
        onDialogDelete(number);
    }

    @Override
    public void onDialogSave(int num) {

        if( num > 0) {
            controller.reorderTimedObjects(num);
        }

        if(selectedTimedNumber > 0 && selectedTimedNumber != num){
            controller.reorderTimedObjects(selectedTimedNumber);
        }

        mDrawerRight.changeListItem(selectedTimedItem,selectedTimedNumber,selectedTimedId);
        new CompleteCalculation().execute();

    }

    @Override
    public void onDialogDelete(int number) {

        if(number > 0){
            controller.reorderTimedObjects(number);
        }

        mDrawerRight.deleteListItem(selectedTimedItem);

        new CompleteCalculation().execute();
    }

    @Override
    public void onDialogCancel() {
        timedDetailDialog.dismiss();
    }

    @Override
    public void showResultDetail(int resultId, int number, int startlistId) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment previous = getSupportFragmentManager().findFragmentByTag("resultDialog");

        if (previous != null) {
            ft.remove(previous);
        }

        ft.addToBackStack(null);

        ResultDetailDialog resultDetailDialog = ResultDetailDialog.newInstance(resultId, number, startlistId);
        resultDetailDialog.show(ft, "resultDialog");
    }



    private class CalculateResults extends AsyncTask<Integer, Void, Integer> {

        @Override
        protected Integer doInBackground(Integer... params) {
            if (Timer.calculateResult(params[0])) {
                return params[0];
            } else {
                cancel(true);
            }

            return -1;
        }

        @Override
        protected void onPostExecute(Integer number) {

            if (number < 0) {
                Log.e("Post", "something went wrong");
            }

            DBHelper dbHelper = new DBHelper();
            Cursor result = dbHelper.select("SELECT MAX (lap),position,difference,jersey,run,startgroupId  FROM Resultlist WHERE number=" + number+" AND competitionId="+controller.getSelectedCompetition());
            result.moveToFirst();

            int position = result.getInt(1);
            int lap = result.getInt(0);
            String j = result.getString(3);
            boolean jersey = j.equals("true");

            if(jersey){
                slideup_number.setBackgroundResource(R.drawable.trikot_rot);
                slideup_number.setTextColor(getResources().getColor(android.R.color.black));
            }else{
                slideup_number.setBackgroundResource(R.drawable.trikot_transparent);
                slideup_number.setTextColor(getResources().getColor(R.color.gruen));
            }

            if (position == 1) {

                Cursor pre = dbHelper.select("SELECT run  FROM Resultlist WHERE position=2 AND lap="+lap+" AND startgroupId="+ result.getInt(5) +" AND competitionId="+controller.getSelectedCompetition());

                position_btn.setColor(getResources().getColor(R.color.magenta));
                slideup_position.setTextColor(getResources().getColor(android.R.color.white));
                slideup_time.setTextColor(getResources().getColor(R.color.gruen));

                if(pre.getCount()>0){
                    pre.moveToFirst();
                    slideup_diff.setText("to 2nd: +" + Timer.secondsToTime((result.getLong(4) - pre.getLong(0)) / 1000).format("%H:%M:%S"));
                    slideup_time.setText(getString(R.string.lap_indicator)+" " + lap);
                }else{
                    slideup_diff.setText("+/-" + Timer.secondsToTime(result.getLong(2) / 1000).format("%H:%M:%S"));
                    slideup_time.setText(getString(R.string.lap_indicator)+" " + lap);
                }
                slideup_diff.setTextColor(getResources().getColor(R.color.gruen));

                pre.close();

            } else {

                Cursor pre = dbHelper.select("SELECT position, run  FROM Resultlist WHERE position=" + (position-1)+" AND lap="+lap+" AND startgroupId="+ result.getInt(5) +" AND competitionId="+controller.getSelectedCompetition());

                position_btn.setColor(getResources().getColor(R.color.transparent_gruen));
                slideup_position.setTextColor(getResources().getColor(R.color.orange));
                slideup_time.setText("to 1: +" + Timer.secondsToTime(result.getLong(2) / 1000).format("%H:%M:%S") + "   " + getString(R.string.lap_indicator)+" " + lap);
                slideup_time.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

                if(pre.getCount()>0){
                    pre.moveToFirst();

                    long time = result.getLong(4) -pre.getLong(1);
                    if(position == 2){
                        slideup_time.setText(getString(R.string.lap_indicator)+" " + lap);
                        slideup_diff.setText("to 1st: +" + Timer.secondsToTime(result.getLong(2) / 1000).format("%H:%M:%S"));
                    }else{
                        slideup_diff.setText("to "+pre.getInt(0)+": +"+Timer.secondsToTime(time/1000).format("%H:%M:%S"));
                    }

                }else{
                    slideup_diff.setText("~ no other results yet ~");
                }

                slideup_diff.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

                pre.close();
            }
            slideup_position.setText("" + position);


            mResultFragment.refreshDisplay(state);
            mResultFragment.findElement(position,lap,controller.getStartlistElements().get(number).getStartId());

            if (getSupportFragmentManager().findFragmentById(R.id.container) instanceof TimingFragmentCenter) {
                mCenterFragment.setContent(number,lap,position);
            } else {
                mTimingFragment.refreshList();
            }

        }

    }

    ;

    private class CompleteCalculation extends AsyncTask<Integer, Void, Integer> {

        @Override
        protected Integer doInBackground(Integer... params) {
            if (Timer.completeCalculation()) {
                return 1;
            } else {
                cancel(true);
            }

            return -1;
        }

        @Override
        protected void onPostExecute(Integer number) {

            if (number < 0) {
                Log.e("Post", "something went wrong");
                position_btn.setColor(getResources().getColor(R.color.transparent_gruen));
                slideup_position.setTextColor(getResources().getColor(R.color.orange));
                slideup_time.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                slideup_position.setText("-");
                slideup_number.setText("--");
                slideup_name.setText("Times edited");
                slideup_time.setText("Failed");
                slideup_diff.setText("");
            }else{
                position_btn.setColor(getResources().getColor(R.color.transparent_gruen));
                slideup_position.setTextColor(getResources().getColor(R.color.orange));
                slideup_time.setTextColor(getResources().getColor(R.color.gruen));
                slideup_position.setText("-");
                slideup_number.setText("--");
                slideup_name.setText("Times edited");
                slideup_time.setText("Succesful");
                slideup_diff.setText("");
            }

            refreshQuickSelect(0);
            mResultFragment.refreshDisplay(state);
            if(timedDetailDialog != null){
                timedDetailDialog.dismiss();
            }

            mDrawerRight.onResume();

            if (getSupportFragmentManager().findFragmentById(R.id.container) instanceof TimingFragment) {
                mTimingFragment.refreshList();
            }
        }
    }

    ;

}
