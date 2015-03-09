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
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

import split.timing.R;
import split.timing.items.Competition;
import split.timing.items.Group;
import split.timing.items.Sportsmen;
import split.timing.items.Startgroup;

/**
 * This is a custom array adapter used to populate the listview whose items will
 * expand to display extra content in addition to the default display.
 */
public class CustomArrayAdapter extends ArrayAdapter<String> {

    private int mLayoutViewResourceId;
    int mode;
    ArrayList mData;

    public CustomArrayAdapter(Context context, int layoutViewResourceId,
                               ArrayList list, int listMode) {
        super(context, layoutViewResourceId);
        mData = list;
        mLayoutViewResourceId = layoutViewResourceId;
        mode = listMode;
    }

    /**
     * Populates the item in the listview cell with the appropriate data. This method
     * sets the thumbnail image, the title and the extra text. This method also updates
     * the layout parameters of the item's view so that the image and title are centered
     * in the bounds of the collapsed view, and such that the extra text is not displayed
     * in the collapsed state of the cell.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
        if (convertView == null) {
            convertView = inflater.inflate(mLayoutViewResourceId, parent, false);
        }

        TextView year = (TextView)convertView.findViewById(R.id.startlist_element_num);
        TextView age = (TextView)convertView.findViewById(R.id.startlist_element_pos);
        TextView name = (TextView)convertView.findViewById(R.id.startlist_element_name);
        TextView club = (TextView)convertView.findViewById(R.id.startlist_element_info);
        TextView fed = (TextView)convertView.findViewById(R.id.startlist_element_extra);
        TableRow tab = (TableRow)convertView.findViewById(R.id.startlist_element_numTableRow);

        LayerDrawable bgDrawable = (LayerDrawable)convertView.getBackground();
        final GradientDrawable shape = (GradientDrawable) bgDrawable.findDrawableByLayerId(R.id.simple_card_border);
        shape.setColor(ColorSetter.newInstance(mode));

        switch (mode){
            case 0:
                Sportsmen s = (Sportsmen)mData.get(position);
                String temp = ""+s.getYear();

                name.setText(s.getName() + " " + s.getLastName().substring(0, 1));
                year.setText(temp.substring(2,4));
                age.setText(""+s.getAge());
                club.setText(s.getClub());
                fed.setText(s.getFederation());
                break;
            case 1:
                Group g = (Group)mData.get(position);
                tab.setVisibility(View.GONE);
                name.setText(g.getName());
                name.setTextSize(30);
                club.setVisibility(View.GONE);
                fed.setVisibility(View.GONE);
                break;
            case 2:
                Startgroup sg = (Startgroup)mData.get(position);
                tab.setVisibility(View.GONE);
                name.setText(sg.getName());
                name.setTextSize(30);
                String m =""+sg.getStartMinute();
                String h=""+sg.getStartHour();

                if(sg.getStartHour()< 10){
                    h = "0"+sg.getStartHour();
                }

                if(sg.getStartMinute() < 10){
                    m = "0"+sg.getStartMinute();
                }

                if(sg.getStartHour() == 0 && sg.getStartMinute() == 0){

                }else{
                    club.setText(h+":"+m);
                }

                fed.setText(sg.getDistance()+" km");
                break;
            case 3:
                Competition c = (Competition)mData.get(position);

                tab.setVisibility(View.GONE);
                name.setTextSize(30);
                name.setText(c.getName());
                club.setText(c.getDate());
                fed.setText(c.getLocation());
                
                break;
        }

        return convertView;
    }

    /*
     *
     * Crops a circle out of the thumbnail photo.
        */
    @Override
    public int getCount() {
        return mData.size();
    }

}