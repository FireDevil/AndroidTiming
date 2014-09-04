package split.timing;

import android.app.Activity;
import android.app.FragmentManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import split.timing.helpers.CircleButton;
import split.timing.helpers.ColorSetter;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StartgroupAddDialogPager.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class StartgroupAddDialogPager extends DialogFragment {

    private Callbacks mCallbacks = sCallbacks;

    public interface Callbacks {
        public void selectionConfirmed(int startgroupId);
    }

    private static Callbacks sCallbacks = new Callbacks() {

        @Override
        public void selectionConfirmed(int startgroupId) {

        }
    };

    public StartgroupAddDialogPager() {
    }

    static StartgroupAddDialogPager newInstance(ArrayList<Integer> ids, int startgroupId) {
        StartgroupAddDialogPager pager = new StartgroupAddDialogPager();
        Bundle args = new Bundle();
        args.putInt("startgroupId", startgroupId);
        args.putIntegerArrayList("IDs", ids);
        pager.setArguments(args);

        return pager;
    }

    SectionsPagerAdapter mSectionsPagerAdapter;

    ViewPager mViewPager;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        // safety check
        if (getDialog() == null) {
            return;
        }

        int dialogWidth = FrameLayout.LayoutParams.WRAP_CONTENT; // specify a value here
        int dialogHeight = 750; // specify a value here

        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);

        // ... other stuff you want to do in your onStart() method
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_pager, container, false);
        mViewPager = (ViewPager) rootView.findViewById(R.id.pagerFragment);
        CircleButton confirm = (CircleButton) rootView.findViewById(R.id.startgroup_add_btn);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        if (getArguments().containsKey("startgroupId")) {
            mSectionsPagerAdapter = new SectionsPagerAdapter(getActivity().getFragmentManager(), 1);
            final PagerTitleStrip strip = (PagerTitleStrip) mViewPager.findViewById(R.id.pager_title_strip);
            strip.setBackgroundColor(new Color().parseColor("#000000"));
            mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    switch (position) {
                        case 0:
                            strip.setBackgroundColor(new Color().parseColor("#000000"));
                            break;
                        case 1:
                            strip.setBackgroundColor(ColorSetter.newInstance(0));
                            break;
                        case 2:
                            strip.setBackgroundColor(ColorSetter.newInstance(1));
                            break;
                        default:

                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
        mViewPager.setCurrentItem(1, true);

        if (getArguments() != null) {
            mSectionsPagerAdapter.setIds(getArguments().getIntegerArrayList("IDs"));
        }

        // Set up the ViewPager with the sections adapter.


        mViewPager.setAdapter(mSectionsPagerAdapter);


        confirm.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           mCallbacks.selectionConfirmed(getArguments().getInt("startgroupId"));
                                       }
                                   }
        );

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


    /**
     * A {@link android.support.v13.app.FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

        ArrayList<Integer> id;
        int mode;

        public SectionsPagerAdapter(FragmentManager fm, int mode) {
            super(getChildFragmentManager());
            this.mode = mode;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            switch (position) {
                case 1:
                    return StartgroupAddDialogFragment.newInstance(id, 0);
                case 2:
                    return StartgroupAddDialogFragment.newInstance(id, 1);
                default:
                    return StartlistAddEmptyPositionsFragment.newInstance();
            }


        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            int ret = 0;

            ret = 3;
            return ret;
        }

        public void setIds(ArrayList<Integer> ids) {
            id = ids;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();

            switch (position) {
                case 1:
                    return "Sportsmen";
                case 2:
                    return "Groups";
                case 0:
                    return "Empty positions";
            }

            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends android.support.v4.app.Fragment {
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

        ArrayList<Integer> id = new ArrayList<Integer>();

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_startgroup_dialog, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));


            return rootView;
        }
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
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

}
