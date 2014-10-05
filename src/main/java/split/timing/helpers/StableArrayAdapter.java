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
import split.timing.items.Startlist;

public class StableArrayAdapter extends ArrayAdapter<String> {

    final int INVALID_ID = -1;

    ArrayList contentList;
    HashMap<Object, Integer> mIdMap = new HashMap<Object, Integer>();
    HashMap<Integer, Startlist> startlistMap = new HashMap<Integer, Startlist>();
    HashMap<Integer, Sportsmen> sportsmenMap = new HashMap<Integer, Sportsmen>();
    HashMap<Integer, Startgroup> startgroupMap = new HashMap<Integer, Startgroup>();

    Startgroup startgroup;
    Competition competition;

    int saveMinute;
    int mode;

    Controller controller = Controller.getInstance();

    public StableArrayAdapter(Context context, int textViewResourceId,ArrayList objects, int mode) {
        super(context, textViewResourceId);

        contentList = objects;


        if (mode == 0) {
            startgroup = controller.getGroups().get(controller.getSelectedStartgroup());
            sportsmenMap = controller.getNumbers();


        }else if (mode == 1) {
            competition = controller.getCompetitions().get(controller.getSelectedCompetition());
        }

        int i = 0;
        for (Object o : objects) {
            mIdMap.put(o.toString(), i);
            i++;
        }

        this.mode = mode;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.startlist_element, parent, false);
        }

        LayerDrawable bgDrawable = (LayerDrawable) convertView.getBackground();
        GradientDrawable shape = (GradientDrawable) bgDrawable.findDrawableByLayerId(R.id.simple_card_border);

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
                Startlist start = ((Startlist) element);
                Sportsmen s = sportsmenMap.get(start.getSportsmenId());
                name.setText(s.getName()+" "+s.getLastName());
                club.setText(s.getClub());
                fed.setText(s.getFederation());
                shape.setColor(ColorSetter.newInstance(0));
                age.setText("" + (start.getStartposition() + 1));
                year.setText("" + start.getNumber());
                year.setTextColor(ColorSetter.newInstance(0));

                String timetext;

                int tmp = start.getDifference();

                int hour = (tmp / 3600);
                int minute = (tmp % 3600) / 60;
                int second = +tmp % 60;


                if (position % 5 == 0) {
                    hour = hour + startgroup.getStartHour();
                    minute = minute + startgroup.getStartMinute();
                    second = second + startgroup.getStartSecond();
                    timetext = hour + ":";
                } else {
                    if (hour > 0) {
                        timetext = "+" + hour + ":";
                    } else {
                        timetext = "+";
                    }
                }


                if (minute < 10) {
                    timetext = timetext + "0" + minute + ":";
                } else {
                    timetext = timetext + minute + ":";
                }

                if (second < 10) {
                    timetext = timetext + "0" + second;
                } else {
                    timetext = timetext + second + "";
                }


                time.setText(timetext);


                break;
            case 1:
                Startgroup list = (Startgroup)element;

                DBHelper db = new DBHelper();
                Cursor cursor = db.select("SELECT * FROM Startlist WHERE startgroupId=" + list.getId());

                year.setText("" + cursor.getCount());
                year.setTextColor(ColorSetter.newInstance(2));

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
