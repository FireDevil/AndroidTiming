package split.timing;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.ArrayList;

import split.timing.helpers.DBHelper;
import split.timing.helpers.DynamicAdapter;
import split.timing.helpers.DynamicGridView;
import split.timing.items.Sportsmen;

/**
 * Created by Antec on 12.12.13.
 */
public class StartlistFragment extends Fragment {

    public StartlistFragment() {
    }

    ArrayList<Sportsmen> sportsmens;
    DynamicGridView gridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_startlist, container, false);

        gridView = (DynamicGridView) rootView.findViewById(R.id.gridview);

        int id = getArguments().getInt("startgroupId");
        sportsmens = new ArrayList<Sportsmen>();

        DBHelper db = new DBHelper();
        Cursor c = db.select("SELECT * FROM Startgroupmember JOIN Sportsmen AS sport WHERE sportsmenId=sport._id AND startgroupId =" + id);

        while (c.moveToNext()) {
            sportsmens.add(new Sportsmen(c.getInt(4), c.getString(5), c.getString(6), c.getString(7), c.getInt(8), c.getInt(9), c.getString(10), c.getString(11)));
        }

        c.close();
        db.close();

        gridView.setAdapter(new DynamicAdapter(getActivity(), sportsmens, 2));

        gridView.setOnDropListener(new DynamicGridView.OnDropListener() {
            @Override
            public void onActionDrop() {
                gridView.stopEditMode();
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                gridView.startEditMode();
                return false;
            }
        });


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), parent.getAdapter().getItem(position).toString()+" "+position,
                        Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    public void addClick() {
        Intent intent = new Intent(getActivity(), AddGroupActivity.class);
        startActivity(intent);
    }
}
