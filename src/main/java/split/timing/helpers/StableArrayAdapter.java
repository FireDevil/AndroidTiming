/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package split.timing.helpers;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import split.timing.R;
import split.timing.items.Competition;
import split.timing.items.Sportsmen;
import split.timing.items.Startgroup;

public class StableArrayAdapter extends ArrayAdapter<String> {

    final int INVALID_ID = -1;

    ArrayList contentList;
    HashMap<Object, Integer> mIdMap = new HashMap<Object, Integer>();

    Startgroup startgroup;
    Competition competition;

    int saveMinute;
    int mode;

    public StableArrayAdapter(Context context, int textViewResourceId, Object parent, int mode) {
        super(context, textViewResourceId);

        ArrayList objects = new ArrayList();

        if (parent instanceof Competition) {
            competition = (Competition) parent;
            objects = competition.getStartgroups();
        }

        if (parent instanceof Startgroup) {
            startgroup = (Startgroup) parent;
            objects = startgroup.getSportsmens();
        }

        for (int i = 0; i < objects.size(); ++i) {
            if (mode == 0) {
                mIdMap.put(objects.get(i).toString(), i);
            } else {
                mIdMap.put(objects.get(i).toString(), i);
            }

        }
        contentList = objects;
        this.mode = mode;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.startlist_element, parent, false);
        }

        LayerDrawable bgDrawable = (LayerDrawable) convertView.getBackground();
        GradientDrawable shape = (GradientDrawable) bgDrawable.findDrawableByLayerId(R.id.card_border);

        Object element = contentList.get(position);

        TextView year = (TextView) convertView.findViewById(R.id.startlist_element_num);
        TextView age = (TextView) convertView.findViewById(R.id.startlist_element_pos);
        TextView name = (TextView) convertView.findViewById(R.id.startlist_element_name);
        TextView time = (TextView) convertView.findViewById(R.id.startlist_element_time);
        TextView club = (TextView) convertView.findViewById(R.id.startlist_element_info);
        TextView fed = (TextView) convertView.findViewById(R.id.startlist_element_extra);
        TableRow tab = (TableRow) convertView.findViewById(R.id.startlist_element_numTableRow);

        switch (mode) {
            case 0:
                Sportsmen s = (Sportsmen) element;
                name.setText(s.getName());
                club.setText(s.getClub());
                fed.setText(s.getFederation());
                shape.setColor(ColorSetter.newInstance(0));
                age.setText("" + (position + 1));
                year.setText("" + (startgroup.getStartNum() + position));

                String timetext;

                int tmp = startgroup.getInterval() * contentList.indexOf(element);

                int hour = (tmp / 3600);
                int minute = (tmp % 3600) / 60;
                int second = +tmp % 60;

                if (contentList.indexOf(element) == 0) {
                    timetext = startgroup.getStartHour() + ":";

                    if (startgroup.getStartMinute() < 10) {
                        timetext = timetext + "0" + startgroup.getStartMinute() + ":";
                    } else {
                        timetext = timetext + startgroup.getStartMinute() + ":";
                    }

                    if (startgroup.getStartSecond() < 10) {
                        timetext = timetext + "0" + startgroup.getStartSecond();
                    } else {
                        timetext = timetext + startgroup.getStartSecond() + "";
                    }
                } else {


                    if((startgroup.getStartNum()+position)%5 == 0){
                        hour=hour+startgroup.getStartHour();
                        minute = minute+startgroup.getStartMinute();
                        second = second+startgroup.getStartSecond();
                        timetext = hour+":";
                    }else{
                        if(hour > 0){
                            timetext = "+"+hour+":";
                        }else {
                            timetext ="+";
                        }
                    }




                    if (minute < 10) {
                        timetext = timetext + "0" +minute + ":";
                    } else {
                        timetext = timetext + minute + ":";
                    }

                    if (second < 10) {
                        timetext = timetext + "0" + second;
                    } else {
                        timetext = timetext + second + "";
                    }
                }


                time.setText(timetext);


                break;
            case 1:
                Startgroup list = (Startgroup) element;

                DBHelper db = new DBHelper();
                Cursor cursor = db.select("SELECT * FROM Startlist WHERE startgroupId=" + list.getId());

                year.setText("" + cursor.getCount());
                year.setTextColor(ColorSetter.newInstance(11));

                age.setText("" + (position + 1));

                cursor.close();
                db.close();

                name.setText(list.getName());
                String m = "" + list.getStartMinute();
                String h = "" + list.getStartHour();

                if (list.getStartMinute() < 10) {
                    m = "0" + list.getStartMinute();
                }

                if (list.getStartHour() == 0 && list.getStartMinute() == 0) {

                } else {
                    club.setText(h + ":" + m);
                }
                fed.setText(list.getDistance() + " km");
                shape.setColor(ColorSetter.newInstance(2));
                break;
        }


        return convertView;
    }

    @Override
    public int getCount() {
        return contentList.size();
    }

    @Override
    public String getItem(int position) {
        return "" + contentList.get(position).toString();
    }

    @Override
    public long getItemId(int position) {
        if (position < 0 || position >= contentList.size()) {
            return INVALID_ID;
        }

        String item = getItem(position);
        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


}
