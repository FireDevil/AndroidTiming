package split.timing.helpers;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.HashMap;

import split.timing.R;
import split.timing.items.Group;
import split.timing.items.Sportsmen;


/**
 * Created by Antec on 15.04.2014.
 */
public class DialogArrayAdapter extends ArrayAdapter<String> {

    private Callbacks mCallbacks = sCallbacks;

    public interface Callbacks {
        public void checkItem(int id,int position, int mode);

    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sCallbacks = new Callbacks() {
        @Override
        public void checkItem(int id,int posititon, int mode) {

        }
    };


    ArrayList mData;
    HashMap<Integer,Integer> mId;
    ViewHolder holder;
   CheckBox t;
    int mode;

    public DialogArrayAdapter(Context context, int resource, ArrayList objects, int mode, Activity activity) {
        super(context, resource);

        mCallbacks = (Callbacks)activity;
        mData = objects;
        mId = new HashMap<Integer, Integer>();
        this.mode = mode;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.checkbox_row, parent, false);
            holder.cb = (CheckBox)convertView.findViewById(android.R.id.checkbox);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        t = (CheckBox)convertView.findViewById(R.id.checkBox);

        switch (mode){
            case 0:
                t.setText(((Sportsmen)mData.get(position)).getName()+" "+((Sportsmen)mData.get(position)).getLastName()+" ("+((Sportsmen)mData.get(position)).getYear()+")");
                break;
            case 1:
                t.setText(((Group)mData.get(position)).getName());
                break;
        }

        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if(mId.containsKey(position)){
                        mId.remove(position);
                    }else{
                        mId.put(position,mode);
                    }


                    switch (mode){
                        case 0:
                            mCallbacks.checkItem(((Sportsmen) mData.get(position)).getId(),position,mode);
                            break;
                        case 1:
                            mCallbacks.checkItem(((Group) mData.get(position)).getId(),position,mode);
                            break;
                    }

            }
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    class ViewHolder{
        CheckBox cb;
    }
}
