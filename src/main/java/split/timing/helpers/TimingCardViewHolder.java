package split.timing.helpers;

import android.content.Context;
import android.os.Vibrator;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import split.timing.R;

/**
 * Created by Antec on 27.02.2015.
 */
public class TimingCardViewHolder extends RecyclerView.ViewHolder {

    private TextView mTextView;
    private CardView cardView;

    public TimingCardViewHolder(View itemView) {
        super(itemView);

        mTextView = (TextView) itemView.findViewById(R.id.timing_center_item_text);
        cardView = (CardView)itemView.findViewById(R.id.timing_center_item_layout);

//        itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try{
//                    Vibrator vib = (Vibrator) ACP.getContext().getSystemService(Context.VIBRATOR_SERVICE);
//                    // Vibrate for 200 milliseconds
//                    vib.vibrate(200);
//                    mCallbacks.newTiming(Integer.getInteger(v.toString()));
//                }catch (Exception e){
//                    Log.e("",e.toString());
//                }
//            }
//        });

//        TextView t = (TextView) convertView.findViewById(R.id.timing_center_item_text);
//        t.setText(contentList[position]+"");
//
//        boolean jersey = controller.getStartlistElements().get(contentList[position]).isJersey();
//
//        if(jersey){
//            t.setBackgroundResource(R.drawable.trikot_transparent);
//        }
//
//
//        if(controller.getLapCounter().containsKey(contentList[position])) {
//            int color = controller.getLapCounter().get(contentList[position]);
//            color++;
//            cardView.setCardBackgroundColor(ColorSetter.colorRoulette(color));
//        }else{
//            cardView.setCardBackgroundColor(ColorSetter.colorRoulette(1));
//        }
    }

    public void bindItem(String text) {
        mTextView.setText(text);
    }

    @Override
    public String toString() {
        return mTextView.getText().toString();
    }

    private Callbacks mCallbacks = sCallbacks;

    public interface Callbacks {
        public void newTiming(int number);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sCallbacks = new Callbacks() {
        @Override
        public void newTiming(int number) {

        }
    };
}
