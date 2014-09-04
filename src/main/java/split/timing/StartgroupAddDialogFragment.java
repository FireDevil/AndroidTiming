package split.timing;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;

import java.util.ArrayList;

import split.timing.helpers.DBHelper;
import split.timing.helpers.DialogArrayAdapter;
import split.timing.items.Group;
import split.timing.items.Sportsmen;

public class StartgroupAddDialogFragment extends ListFragment {

    private Callbacks mCallbacks = sCallbacks;

    public interface Callbacks {
    }

    private static Callbacks sCallbacks = new Callbacks() {

    };


    static int mode =0;

    public static StartgroupAddDialogFragment newInstance(ArrayList<Integer> ids, int position) {
        StartgroupAddDialogFragment fragment = new StartgroupAddDialogFragment();
        Bundle args = new Bundle();
        args.putIntegerArrayList("ID",ids);
        fragment.setArguments(args);
        mode = position;
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StartgroupAddDialogFragment() {
    }

    ArrayList mData;
    DialogArrayAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DBHelper db = new DBHelper();
        Cursor c;
        ArrayList<Integer> ids = getArguments().getIntegerArrayList("ID");

        switch(mode){
            case 0:
                mData = new ArrayList<Sportsmen>();

                c = db.select("SELECT * FROM Sportsmen ORDER BY name");

                while (c.moveToNext()){
                    if(ids.contains(c.getInt(0))){
                    }else{
                        mData.add(new Sportsmen(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getInt(4), c.getInt(5), c.getString(6), c.getString(7)));
                    }
                }
                adapter = new DialogArrayAdapter(getActivity(),R.layout.checkbox_row,mData,mode,getActivity());
                break;
            case 1:
                mData = new ArrayList<Group>();

                c = db.select("SELECT * FROM Groups ORDER BY name");

                while (c.moveToNext()){
                        mData.add(new Group(c.getInt(0), c.getString(1)));
                }

                adapter = new DialogArrayAdapter(getActivity(),R.layout.checkbox_row,mData,mode,getActivity());
                break;
        }
        db.close();
        setListAdapter(adapter);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
