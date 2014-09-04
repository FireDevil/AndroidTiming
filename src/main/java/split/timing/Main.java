package split.timing;


import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import split.timing.helpers.DBHelper;
import split.timing.helpers.DialogArrayAdapter;
import split.timing.items.Groupmember;

public class Main extends FragmentActivity implements
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        NEWListFragment.Callbacks,
        SportsFragment.Callbacks,
        StartgroupAddDialog.OnDialogInteractionListener,
        GroupFragment.Callbacks,
        CompetitionSetupFragment.Callbacks,
        DialogArrayAdapter.Callbacks,
        StartgroupAddDialogPager.Callbacks,
StartlistAddEmptyPositionsFragment.Callbacks,
        StartgroupEditDialog.SaveStartgroupCallback{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    public static int mCurrentSelectedPosition = 0;

    HashMap<Integer, Integer> mId;

    int dialogMode;

    int mStackLevel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deleteDatabase("timing.db");

        mId = new HashMap<Integer, Integer>();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        mNavigationDrawerFragment.selectItem(mCurrentSelectedPosition);


    }

    boolean mTwoPane;

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments

        FragmentManager fragmentManager = getSupportFragmentManager();
        NEWListFragment frag;
        Bundle args;

        switch (position) {
            case 0:
                OverViewFragment fragment = new OverViewFragment();
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.container, fragment)
                        .addToBackStack(null)
                        .commit();
                break;
            case 1:
                frag = new NEWListFragment();
                args = new Bundle();
                args.putString("Sports", "");
                frag.setArguments(args);
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.container, frag)
                        .addToBackStack(null)
                        .commit();
                break;
            case 2:
                frag = new NEWListFragment();
                args = new Bundle();
                args.putString("Group", "");
                frag.setArguments(args);
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.container, frag)
                        .addToBackStack(null)
                        .commit();
                break;
            case 3:
                frag = new NEWListFragment();
                args = new Bundle();
                args.putString("Competition", "");
                frag.setArguments(args);
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.container, frag)
                        .addToBackStack(null)
                        .commit();
                break;

            case 4:
                Intent in = new Intent(this,Timing.class);
                startActivity(in);
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    private int groupPos = 0;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();

        menu.add("-> ITEM " + groupPos);
        inflater.inflate(R.menu.main, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Toast.makeText(getApplicationContext(), "ContextMenu" + groupPos, Toast.LENGTH_SHORT).show();
        return super.onContextItemSelected(item);
    }


    @Override
    public void onGroupSelected(int id) {
        GroupFragment group = new GroupFragment();
        Bundle args = new Bundle();
        args.putInt("Groupmember", id);
        group.setArguments(args);
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.pop_in, R.anim.pop_out)
                .addToBackStack(null)
                .replace(R.id.container, group, "Group")
                .commit();
    }

    @Override
    public void onSportsSelected(int id) {
        if (id < 0) {
            SportsFragment sf = new SportsFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.pop_in, R.anim.pop_out)
                    .addToBackStack(null)
                    .replace(R.id.container, sf)
                    .commit();
        } else {
            SportsFragment sf = new SportsFragment();
            Bundle args = new Bundle();
            args.putInt("sportsmenId", id);
            sf.setArguments(args);
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.pop_in, R.anim.pop_out)
                    .addToBackStack(null)
                    .replace(R.id.container, sf)
                    .commit();
        }
    }

    @Override
    public void onListSelected(int id) {
        CompetitionSetupFragment frag = new CompetitionSetupFragment();
        Bundle args = new Bundle();
        args.putInt("startgroupId", id);
        frag.setArguments(args);
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.pop_in, R.anim.pop_out)
                .addToBackStack(null)
                .replace(R.id.container, frag, "Startlist")
                .commit();
    }

    @Override
    public void onStartgroupSelected(int id, int competitionId) {
        this.compId = competitionId;

        CompetitionSetupFragment frag = new CompetitionSetupFragment();
        Bundle args = new Bundle();
        args.putInt("startgroupId", id);
        args.putInt("competitionId", competitionId);
        frag.setArguments(args);
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.container, frag)
                .commit();

    }

    @Override
    public void onResultSelected(int id) {

    }

    @Override
    public void onCompetitionSelected(int id) {
        CompetitionSetupFragment fragm = new CompetitionSetupFragment();
        Bundle args = new Bundle();
        args.putInt("competitionId", id);
        fragm.setArguments(args);
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.pop_in, R.anim.pop_out)
                .addToBackStack(null)
                .replace(R.id.container, fragm)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void backToList() {
        getSupportFragmentManager().popBackStack()
                /*.beginTransaction().setCustomAnimations(R.anim.pop_back_in, R.anim.pop_back_out).addToBackStack(null)
                .replace(R.id.container, list).commit()*/;
    }

    @Override
    public void showStartlist(int competitionId) {
        StartlistPager fragm = new StartlistPager();
        Bundle args = new Bundle();
        args.putInt("competitionId", competitionId);
        fragm.setArguments(args);
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.pop_in, R.anim.pop_out)
                .addToBackStack(null)
                .replace(R.id.container, fragm)
                .commit();
    }

    @Override
    public void onDialogInteraction(ArrayList<Integer> selectedItems, HashMap<Integer, Integer> idMap) {

        int groupId = ((GroupFragment) getSupportFragmentManager().findFragmentByTag("Group")).groupId;

        DBHelper db = new DBHelper();
        for (int item : selectedItems) {
            int id = idMap.get(item);


            db.insert(new Groupmember(groupId, id));

        }
        db.close();

        GroupFragment group = new GroupFragment();
        Bundle args = new Bundle();
        args.putInt("Groupmember", groupId);
        group.setArguments(args);
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .setCustomAnimations(R.anim.pop_in, R.anim.pop_out)
                .replace(R.id.container, group, "Group")
                .commit();
    }

    @Override
    public void onDialogCallback(ArrayList<Integer> selectedItems, HashMap<Integer, Integer> idMap, int competitionId) {

        DBHelper db = new DBHelper();
        for (int item : selectedItems) {
            int id = idMap.get(item);


            ContentValues values = new ContentValues();
            values.put("competitionId", competitionId);
            values.put("startgroupId", id);
            db.insert("Competitionmember", values);

        }
        db.close();

        CompetitionSetupFragment group = new CompetitionSetupFragment();
        Bundle args = new Bundle();
        args.putInt("competitionId", competitionId);
        group.setArguments(args);
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .setCustomAnimations(R.anim.pop_in, R.anim.pop_out)
                .replace(R.id.container, group)
                .commit();
    }

    int compId;
    ArrayList<Integer> addIds;

    @Override
    public void callStartgroupAddDialog(ArrayList<Integer> ids, int startgroup) {
        mStackLevel++;
        addIds=ids;

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");

        if(prev!=null){
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        StartgroupAddDialogPager newFragment = StartgroupAddDialogPager.newInstance(ids, startgroup);
        newFragment.show(ft,"dialog");
    }

    @Override
    public void callStartgroupEditDialog(int startgroup, int competition) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment previous = getSupportFragmentManager().findFragmentByTag("editDialog");

        if(previous != null){
            ft.remove(previous);
        }

        ft.addToBackStack(null);

        StartgroupEditDialog newDialog = StartgroupEditDialog.newInstance(startgroup,competition);
        newDialog.show(ft,"editDialog");

    }

    @Override
    public void checkItem(int id, int position, int mode) {

        if (mode != dialogMode) {
            mId.clear();
        }

        if (mId.containsValue(id)) {
            mId.remove(id);
        } else {
            mId.put(id,id);
        }

        dialogMode = mode;
    }

    @Override
    public void selectionConfirmed(int startgroupId) {
        DBHelper db = new DBHelper();
        ArrayList<Integer> ids = new ArrayList<Integer>();
        ids.addAll(mId.values());

        mId = new HashMap<Integer, Integer>();

        addIds = new ArrayList<Integer>();

        switch (dialogMode) {
            case 0:
                for(int j: ids){
                    addIds.add(j);
                }
                break;
            case 1:
                for (int i : ids) {
                    Cursor cursor = db.select("SELECT * FROM Groupmember WHERE groupId=" + i);

                    while (cursor.moveToNext()) {
                        addIds.add(cursor.getInt(2));
                    }
                    cursor.close();
                }
                break;
            case 2:
                for(int n = 1; n <= additional;n++){
                    addIds.add(-1);
                }
                break;
        }

        db.close();

        CompetitionSetupFragment frag = new CompetitionSetupFragment();
        Bundle args = new Bundle();
        args.putInt("startgroupId", startgroupId);
        args.putInt("competitionId",compId);
        args.putIntegerArrayList("added", addIds);
        frag.setArguments(args);
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.container, frag)
                .commit();
    }

    int additional=0;

    @Override
    public void onValueChanged(int value) {
        dialogMode = 2;
        additional=value;

    }
}