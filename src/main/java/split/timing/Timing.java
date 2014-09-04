package split.timing;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.nineoldandroids.view.animation.AnimatorProxy;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import split.timing.helpers.ACP;
import split.timing.helpers.CircleButton;


public class Timing extends FragmentActivity implements
        TimingDrawerLeft.NavigationDrawerCallbacks,
        TimingDrawerRight.NavigationDrawerCallbacks{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private TimingDrawerLeft mDrawerLeft;
    private TimingDrawerRight mDrawerRight;
    private SlidingUpPanelLayout mLayout;

    public static final String SAVED_STATE_ACTION_BAR_HIDDEN = "saved_state_action_bar_hidden";

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_timing);

        final Animation animRotate = AnimationUtils.loadAnimation(ACP.getContext(), R.anim.anim_rotate_180);
        final CircleButton btn = (CircleButton) findViewById(R.id.slideUpCircleButton);

        mLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        mLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
                setActionBarTranslation(mLayout.getCurrentParalaxOffset());

            }

            @Override
            public void onPanelCollapsed(View view) {
                btn.startAnimation(animRotate);
                btn.setImageResource(R.drawable.ic_action_up);
            }

            @Override
            public void onPanelExpanded(View view) {
                btn.startAnimation(animRotate);
                btn.setImageResource(R.drawable.ic_action_down);
            }

            @Override
            public void onPanelAnchored(View view) {

            }

            @Override
            public void onPanelHidden(View view) {

            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLayout.isPanelExpanded()){
                    mLayout.collapsePanel();
                }else{
                    mLayout.expandPanel();
                }
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

        if(savedInstanceState == null){
            final TextView textView = (TextView) findViewById(R.id.slideUpText);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textView.setSelected(true);
                }
            });
            textView.setSelected(true);
        }



        FragmentManager fm = getSupportFragmentManager();
        SlideUpFragment f = new SlideUpFragment();
        fm.beginTransaction().replace(R.id.slideUpContainer,f).commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_STATE_ACTION_BAR_HIDDEN, mLayout.isPanelExpanded());
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mDrawerLeft.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            //getMenuInflater().inflate(R.menu.timing, menu);
            restoreActionBar();
            return true;
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if(item.getItemId()==R.id.action_example){
            mDrawerRight.openRightDrawer();
            mDrawerLeft.closeLeftDrawer();
            return super.onOptionsItemSelected(item);
        }

        if(item.getItemId()==android.R.id.home){
            mDrawerRight.setOpen(false);
            mDrawerLeft.openLeftDrawer();
            mDrawerRight.closeRightDrawer();
        }

        return super.onOptionsItemSelected(item);
    }

    private int getActionBarHeight(){
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_timing, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((Timing) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
