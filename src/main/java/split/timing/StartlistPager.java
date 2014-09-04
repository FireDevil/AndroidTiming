package split.timing;

import android.app.Activity;
import android.app.FragmentManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Locale;

import split.timing.helpers.DBHelper;

public class StartlistPager extends Fragment {

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    int competitionId;
    ArrayList<Integer> startgroupsIds;


    public StartlistPager() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_startlist_pager, container, false);
        mViewPager = (ViewPager) rootView.findViewById(R.id.pagerFragment);
        PagerTitleStrip strip = (PagerTitleStrip)mViewPager.findViewById(R.id.pager_title_strip);
        strip.setBackgroundColor(getResources().getColor(android.R.color.black));



        if (getArguments().containsKey("competitionId")) {
            competitionId = getArguments().getInt("competitionId");


            startgroupsIds = new ArrayList<Integer>();

            DBHelper db = new DBHelper();
            Cursor c = db.select("SELECT * FROM Competitionmember WHERE competitionId=" + competitionId);

            while (c.moveToNext()) {
                startgroupsIds.add(c.getInt(2));
            }

            c.close();
            db.close();

            if(startgroupsIds.size() > 0){
                mSectionsPagerAdapter = new SectionsPagerAdapter(getActivity().getFragmentManager());
                mViewPager.setAdapter(mSectionsPagerAdapter);
            }
        }

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public class SectionsPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

        ArrayList<Integer> id;
        int mode;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(getChildFragmentManager());
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).


                StartlistFragment sf = new StartlistFragment();
                Bundle args = new Bundle();
                args.putInt("startgroupId", startgroupsIds.get(position));
                sf.setArguments(args);

                return sf;

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return startgroupsIds.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();

            DBHelper db = new DBHelper();
            Cursor c = db.select("SELECT name FROM Startgroup WHERE _id="+ startgroupsIds.get(position));
            c.moveToFirst();
            String name = c.getString(0);
            c.close();
            db.close();

            return name;
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

}
