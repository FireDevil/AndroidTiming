package split.timing;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;

import split.timing.helpers.DynamicListView;
import split.timing.helpers.StableArrayAdapter;
import split.timing.helpers.UndoBarController;

public class SelectCompetitionActivity extends Activity  {

    String nameText;

    StableArrayAdapter adapter;

    int backUpPosition = -1;
    ArrayList<String> backUpList;

    boolean dismissed = false;

    ArrayList<String> contentList;

    StartgroupAddDialog sad = new StartgroupAddDialog();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_competition);
        UndoBarController mUndoBarController;

        final DynamicListView lv = (DynamicListView) findViewById(R.id.listview);

        contentList = new ArrayList<String>();
        backUpList = new ArrayList<String>();

        for (int i = 0; i < 1; ++i) {
            contentList.add("Element 1");
            backUpList.add("Element 1");
        }

        /*adapter = new StableArrayAdapter(this, R.layout.startlist_element, contentList);

        View undo = findViewById(R.id.undobar);
        final TextView header_text = (TextView) findViewById(R.id.startlist_header_text);
        final EditText header_edit = (EditText) findViewById(R.id.startlist_header_edit);
        final ImageButton confirm = (ImageButton) findViewById(R.id.startlist_header_confirm);
        final ImageButton dismiss = (ImageButton) findViewById(R.id.startlist_header_dismiss);
        final ImageButton imgBtn = (ImageButton) findViewById(R.id.startlist_footer_add);

        header_edit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                nameText = header_edit.getText().toString();
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (!nameText.equals("")) {
                        nameText = header_edit.getText().toString();
                        header_edit.setVisibility(View.GONE);
                        header_text.setVisibility(View.VISIBLE);
                        header_text.setText(nameText);
                        confirm.setVisibility(View.GONE);
                        dismiss.setVisibility(View.VISIBLE);

                    }

                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            Context.INPUT_METHOD_SERVICE);

                    imm.hideSoftInputFromWindow(v.getWindowToken(),
                            InputMethodManager.RESULT_UNCHANGED_SHOWN);
                }
                return false;
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameText = header_edit.getText().toString();

                if (!nameText.equals("")) {
                    header_edit.setVisibility(View.GONE);
                    header_text.setVisibility(View.VISIBLE);
                    header_text.setText(nameText);
                    confirm.setVisibility(View.GONE);
                    dismiss.setVisibility(View.VISIBLE);

                }

                InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);

                imm.hideSoftInputFromWindow(v.getWindowToken(),
                        InputMethodManager.RESULT_UNCHANGED_SHOWN);
            }
        });

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                header_edit.setText(nameText);
                header_edit.setVisibility(View.VISIBLE);
                header_text.setVisibility(View.GONE);
                dismiss.setVisibility(View.GONE);
                confirm.setVisibility(View.VISIBLE);
            }
        });

        mUndoBarController = new UndoBarController(undo, this);
        final UndoBarController finalMUndoBarController = mUndoBarController;
        final SwipeToDismissListener touchListener =
                new SwipeToDismissListener(
                        lv,
                        new SwipeToDismissListener.DismissCallbacks() {

                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    if (backUpPosition >= 0 && dismissed) {
                                        backUpList.remove(backUpPosition);
                                        dismissed = true;
                                    }

                                    adapter.remove(adapter.getItem(position));
                                    //contentList.remove(position);
                                    backUpPosition = position;
                                    finalMUndoBarController.showUndoBar(
                                            false,
                                            getString(R.string.undo),
                                            null);
                                }

                                adapter.notifyDataSetChanged();
                            }
                        }
                );


        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sad.show(getFragmentManager(), "SportsSelection");
            }
        });

        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                lv.getmScrollListener().onScrollStateChanged(view, scrollState);
                touchListener.setEnabled(true);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                lv.getmScrollListener().onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                touchListener.setEnabled(false);
            }
        });
        lv.setTouchListener(touchListener);
        lv.setOnTouchListener(touchListener);
        lv.setItemList(contentList);
        lv.setAdapter(adapter);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), contentList.get(position), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onDialogInteraction(ArrayList<Integer> selectedItems, HashMap<Integer, Integer> idMap) {

        for (int item : selectedItems) {
            int id = idMap.get(item);
            if (adapter.contains("One")) {
                Toast.makeText(getApplicationContext(), "Element schon da", Toast.LENGTH_SHORT).show();
            } else {
                adapter.add("One");
                //contentList.add("One");
                backUpList.add("One");

            }

        }
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onUndo(Parcelable token) {
        dismissed = false;
        Log.e("Undo " + backUpPosition, "" + contentList.size() + " backup " + backUpList.size());
        if(contentList.size() == backUpPosition){
            //contentList.add(backUpList.get(backUpPosition));
            adapter.add(backUpList.get(backUpPosition));
            adapter.notifyDataSetChanged();
        }else{
            //contentList.add(backUpPosition, backUpList.get(backUpPosition));
            adapter.add(backUpPosition, backUpList.get(backUpPosition));
            adapter.notifyDataSetChanged();
        }*/


        //adapter.notifyDataSetChanged();
    }
}
