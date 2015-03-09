package split.timing;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.ImageView;
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

/**
 * Created by Antec on 09.12.2014.
 */
public class ResultDetailDialog extends DialogFragment {

    SimpleDateFormat sdf = new SimpleDateFormat("@ HH:mm:ss");//new SimpleDateFormat("dd.MM.yyyy @ HH:mm:ss");
    Date date;
    Controller controller = Controller.getInstance();
    boolean tooEarly = false;

    public ResultDetailDialog() {
        // Required empty public constructor
    }

    public static ResultDetailDialog newInstance(int resultId, int number, int startlistId) {
        ResultDetailDialog fragment = new ResultDetailDialog();
        Bundle args = new Bundle();
        args.putInt("result", resultId);
        args.putInt("number", number);
        args.putInt("startlist", startlistId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int number =getArguments().getInt("number");
        int resultId = getArguments().getInt("result");
        int startlistId = getArguments().getInt("startlist");


        Sportsmen sportsmen = controller.getNumbers().get(number);
        Startlist startlist = controller.getStartlistElements().get(number);
        Startgroup startgroup = controller.getGroups().get(startlist.getStartId());

        if(sportsmen == null){
            sportsmen = controller.getNumbers().get(0);
        }

        DBHelper db = new DBHelper();
        Cursor cursor = db.select("SELECT * FROM Resultlist WHERE _id="+resultId);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (cursor.getCount() == 0) {

            builder.setMessage("Element not found");

            return builder.create();
        }else{
            cursor.moveToFirst();
        }


        String dates = controller.getCompetitions().get(controller.getSelectedCompetition()).getDate();
        int[] arr = controller.parseDate(dates);
        int day = arr[0];
        int month = arr[1];
        int year = arr[2];

        date = new Date(cursor.getLong(3));
        long start = Timer.dateToMillis(year, month, day, startgroup.getStartHour(), startgroup.getStartMinute(), startgroup.getStartSecond());
        Date startDate = new Date(start);

        if(startDate.after(date)){
            Toast.makeText(getActivity(), "Attention: Started before starttime!", Toast.LENGTH_SHORT).show();
            tooEarly = true;
        }

        View v = getActivity().getLayoutInflater().inflate(R.layout.timing_result_detail_dialog, null);
        TextView pos = (TextView) v.findViewById(R.id.result_dialog_position);
        pos.setText(cursor.getInt(2)+"");

        TextView num = (TextView) v.findViewById(R.id.result_dialog_number);
        num.setText("( "+number+" )");

        ImageView trikot = (ImageView)v.findViewById(R.id.result_dialog_trikot);
        if(startlist.isJersey()){
            trikot.setVisibility(View.VISIBLE);
        }else{
            trikot.setVisibility(View.GONE);
        }

        TextView diff = (TextView) v.findViewById(R.id.result_dialog_difference);
        if(cursor.getInt(2) == 1){
            diff.setText("+/-"+ Timer.secondsToTime(0).format("%H:%M:%S"));
        }else{
            diff.setText("+"+ Timer.secondsToTime(cursor.getLong(5)/1000).format("%H:%M:%S"));
        }


        TextView group = (TextView) v.findViewById(R.id.result_dialog_groupname);
        group.setText(startgroup.getName());

        TextView run = (TextView) v.findViewById(R.id.result_dialog_run);
        run.setText(""+Timer.secondsToTime(cursor.getLong(4)/1000).format("%H:%M:%S"));

        TextView info = (TextView) v.findViewById(R.id.result_dialog_info);
        info.setText("Lap: "+cursor.getInt(6));

        TextView timed = (TextView) v.findViewById(R.id.result_dialog_timed);
        date = new Date(cursor.getLong(3));
        timed.setText(sdf.format(date));
        if(tooEarly){
            timed.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            tooEarly = false;
        }


        TextView name = (TextView) v.findViewById(R.id.result_dialog_name);
        name.setText(sportsmen.getLastName()+", "+sportsmen.getName());

        TextView club = (TextView) v.findViewById(R.id.result_dialog_club);
        club.setText(sportsmen.getClub());

        TextView fed = (TextView) v.findViewById(R.id.result_dialog_federation);
        if(sportsmen.getFederation().length()==0){
            fed.setText(controller.getCompetitions().get(controller.getSelectedCompetition()).getName());
        }else {
            fed.setText(sportsmen.getFederation());
        }

        cursor.close();
        db.close();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setCustomTitle(null)
               .setView(v)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                        // Add action buttons
                .setPositiveButton("Fertig", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
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
