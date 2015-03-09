package split.timing;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;
import java.text.SimpleDateFormat;

import split.timing.helpers.Controller;
import split.timing.helpers.DBHelper;
import split.timing.helpers.Timer;
import split.timing.items.Sportsmen;
import split.timing.items.Startgroup;
import split.timing.items.Startlist;
import split.timing.items.Timed;

/**
 * Created by Antec on 10.12.2014.
 */
public class TimedDetailDialog extends DialogFragment {

    SimpleDateFormat sdf = new SimpleDateFormat("@ HH:mm:ss");//new SimpleDateFormat("dd.MM.yyyy @ HH:mm:ss");
    Date date;
    Controller controller = Controller.getInstance();
    int number;
    int timedId;
    Sportsmen sportsmen;
    Startlist startlist;
    Startgroup startgroup;
    Timed timedObject;
    boolean tooEarly = false;

    public TimedDetailDialog() {
        // Required empty public constructor
    }

    public static TimedDetailDialog newInstance(int timedId, int number, int startlistId) {
        TimedDetailDialog fragment = new TimedDetailDialog();
        Bundle args = new Bundle();
        args.putInt("timed", timedId);
        args.putInt("number", number);
        args.putInt("startlist", startlistId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        number = getArguments().getInt("number");
        timedId = getArguments().getInt("timed");
        int startlistId = getArguments().getInt("startlist");

        final View v = getActivity().getLayoutInflater().inflate(R.layout.timing_timed_detail_dialog, null);
        final LinearLayout firstLayout = (LinearLayout) v.findViewById(R.id.timed_dialog_first_layout);
        final LinearLayout lastLayout = (LinearLayout) v.findViewById(R.id.timed_dialog_last_layout);
        final EditText editText = (EditText) v.findViewById(R.id.timed_dialog_edit);
        final FrameLayout numberFrame = (FrameLayout) v.findViewById(R.id.timed_dialog_number_framelayout);
        final CardView cardView = (CardView)v.findViewById(R.id.timed_dialog_card);
        TextView pos = (TextView) v.findViewById(R.id.timed_dialog_number);
        TextView num = (TextView) v.findViewById(R.id.timed_dialog_info);
        ImageView trikot = (ImageView) v.findViewById(R.id.timed_dialog_trikot);
        TextView diff = (TextView) v.findViewById(R.id.timed_dialog_difference);
        final TextView group = (TextView) v.findViewById(R.id.timed_dialog_groupname);
        TextView run = (TextView) v.findViewById(R.id.timed_dialog_run);
        final TextView info = (TextView) v.findViewById(R.id.timed_dialog_lap);
        TextView timed = (TextView) v.findViewById(R.id.timed_dialog_timed);
        TextView name = (TextView) v.findViewById(R.id.timed_dialog_name);
        TextView club = (TextView) v.findViewById(R.id.timed_dialog_club);
        TextView fed = (TextView) v.findViewById(R.id.timed_dialog_federation);

        timedObject = controller.getTimedObject(timedId);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (timedObject == null) {

            builder.setMessage("Element not found");

            return builder.create();
        }

        if(startlistId < 0){

            pos.setText("00");
            trikot.setVisibility(View.GONE);
            group.setText(getString(R.string.noGroup));
            run.setText("--:--:--");
            info.setText("");
            date = new Date(timedObject.getTimedInMillis());
            timed.setText(sdf.format(date));
            name.setText(getString(R.string.noNumber));
            club.setText("");
            fed.setText(""+controller.getCompetitions().get(controller.getSelectedCompetition()).getName());

            pos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    numberFrame.setVisibility(View.GONE);
                    cardView.setVisibility(View.VISIBLE);
                    editText.setText("");
                    editText.requestFocus();

                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.toggleSoftInputFromWindow(v.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);

                }
            });

//            editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
//                @Override
//                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//
//                    if (!v.getText().toString().equals("")) {
//                        lastLayout.setVisibility(View.GONE);
//                        group.setText("");
//                        info.setText("");
//                    }
//
//                    return false;
//                }
//            });

        }else {


            sportsmen = controller.getNumbers().get(number);
            startlist = controller.getStartlistElements().get(number);
            startgroup = controller.getGroups().get(startlist.getStartId());

            String dates = controller.getCompetitions().get(controller.getSelectedCompetition()).getDate();
            int[] arr = controller.parseDate(dates);
            int day = arr[0];
            int month = arr[1];
            int year = arr[2];

            date = new Date(timedObject.getTimedInMillis());
            long start = Timer.dateToMillis(year, month, day, startgroup.getStartHour(), startgroup.getStartMinute(), startgroup.getStartSecond());
            Date startDate = new Date(start);

            if(startDate.after(date)){
                Toast.makeText(getActivity(),"Attention: Started before starttime!",Toast.LENGTH_SHORT).show();
                tooEarly = true;
            }

            if(sportsmen == null){
                sportsmen = controller.getNumbers().get(0);
            }

            pos.setText(number + "");

            if (startlist.isJersey()) {
                trikot.setVisibility(View.VISIBLE);
            } else {
                trikot.setVisibility(View.GONE);
            }

//        if (cursor.getInt(2) == 1) {
//            diff.setText("+/-" + Timer.secondsToTime(0).format("%H:%M:%S"));
//        } else {
//            diff.setText("+" + Timer.secondsToTime(cursor.getLong(5) / 1000).format("%H:%M:%S"));
//        }

            group.setText(startgroup.getName());
            run.setText("" + Timer.secondsToTime(timedObject.getRunInMillis() / 1000).format("%d d %H:%M:%S"));
            info.setText("Lap: " + timedObject.getLap());
            timed.setText(sdf.format(date));
            if(tooEarly){
                timed.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                tooEarly = false;
            }
            name.setText(sportsmen.getLastName() + ", " + sportsmen.getName());
            club.setText(sportsmen.getClub());

            if(sportsmen.getFederation().length()==0){
                fed.setText(controller.getCompetitions().get(controller.getSelectedCompetition()).getName());
            }else {
                fed.setText(sportsmen.getFederation());
            }

            pos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    numberFrame.setVisibility(View.GONE);
                    cardView.setVisibility(View.VISIBLE);
                    editText.setText("" + number);
                    editText.requestFocus();

                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.toggleSoftInputFromWindow(v.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);

                }
            });

//            editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
//                @Override
//                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//
//                    if (!v.getText().toString().equals("" + number)) {
//                        lastLayout.setVisibility(View.GONE);
//                        group.setText("");
//                        info.setText("");
//                    }
//
//                    return false;
//                }
//            });

        }

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(count < before) {
                    lastLayout.setVisibility(View.GONE);
                    group.setText("");
                    info.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setCustomTitle(null)
                .setView(v)
                        // Specify the list array, the items to be selected by default (null for none),
                        // and the listener through which to receive callbacks when items are selected
                        // Add action buttons
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        controller.cutLapCounter(number);

                        DBHelper db =new DBHelper();
                        db.delete("Timed", " _id=" + timedId);
                        db.close();
                        mCallbacks.onDialogDelete(number);
                    }
                })
                .setNeutralButton("Ready", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCallbacks.onDialogCancel();
                    }
                });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        final AlertDialog dialog = (AlertDialog)getDialog();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final LinearLayout lastLayout = (LinearLayout) dialog.findViewById(R.id.timed_dialog_last_layout);
                        final EditText editText = (EditText) dialog.findViewById(R.id.timed_dialog_edit);
                        TextView name = (TextView) dialog.findViewById(R.id.timed_dialog_name);
                        TextView club = (TextView) dialog.findViewById(R.id.timed_dialog_club);
                        TextView fed = (TextView) dialog.findViewById(R.id.timed_dialog_federation);
                        final CardView cardView = (CardView)dialog.findViewById(R.id.timed_dialog_card);

                        if(cardView.getVisibility() == View.GONE){
                            dismiss();
                        }

                        if (editText.getText().length() > 0) {

                            int num = Integer.parseInt(editText.getText().toString());
                            name.setTextColor(getResources().getColor(R.color.gruen));
                            cardView.setCardBackgroundColor(getResources().getColor(R.color.gruen));

                            if (num == 0) {
                                num = -1;
                            }

                            if (!controller.getStartlistElements().containsKey(num) && num > 0) {
                                Toast.makeText(getActivity(), "This is no valid number, please try another.", Toast.LENGTH_SHORT).show();
                            }

                            if (num > 0) {
                                if (num != timedObject.getNumber()) {

                                    controller.cutLapCounter(number);
                                    controller.putLapCounter(num);

                                    startlist = controller.getStartlistElements().get(num);
                                    startgroup = controller.getGroups().get(startlist.getStartId());

                                    String date = controller.getCompetitions().get(controller.getSelectedCompetition()).getDate();
                                    int[] arr = controller.parseDate(date);
                                    int day = arr[0];
                                    int month = arr[1];
                                    int year = arr[2];

                                    long start = Timer.dateToMillis(year, month, day, startgroup.getStartHour(), startgroup.getStartMinute(), startgroup.getStartSecond());
                                    start = start + startlist.getDifference() * 1000;

                                    timedObject.setNumber(num);
                                    timedObject.setRunInMillis(timedObject.getTimedInMillis() - start);
                                    timedObject.setLap(controller.getLapCounter().get(num));

                                    DBHelper dbHelper = new DBHelper();
                                    ContentValues values = new ContentValues();
                                    values.put("number", num);
                                    values.put("timed", timedObject.getTimedInMillis() + "");
                                    values.put("run", timedObject.getRunInMillis() + "");
                                    values.put("lap", timedObject.getLap());
                                    values.put("competitionId", timedObject.getCompetitionId());
                                    dbHelper.update("Timed", values, " _id=" + timedObject.getId());

                                    mCallbacks.onDialogSave(num);
                                }

                            } else {
                                controller.cutLapCounter(number);
                                controller.putLapCounter(num);

                                DBHelper dbHelper = new DBHelper();
                                ContentValues values = new ContentValues();
                                values.put("number", -1);
                                values.put("timed", timedObject.getTimedInMillis() + "");
                                values.put("run", 0);
                                values.put("lap", 0);
                                values.put("competitionId", timedObject.getCompetitionId());
                                dbHelper.update("Timed", values, " _id=" + timedObject.getId());

                                mCallbacks.onDialogSave(-1);
                            }


                        }else{
                            lastLayout.setVisibility(View.VISIBLE);
                            name.setText(getString(R.string.number_required));
                            name.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                            club.setText("");
                            fed.setText("");
                            cardView.setCardBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
                            editText.requestFocus();
                        }
                    }
                });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (Callbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    private Callbacks mCallbacks = sCallbacks;

    public interface Callbacks {
        public void onDialogSave(int i);
        public void onDialogDelete(int number);
        public void onDialogCancel();
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sCallbacks = new Callbacks() {

        @Override
        public void onDialogSave(int i) {

        }

        @Override
        public void onDialogDelete(int number) {

        }

        @Override
        public void onDialogCancel() {

        }
    };
}
