package split.timing.helpers;

import android.graphics.Color;

/**
 * Created by Antec on 06.04.2014.
 */
public class ColorSetter {
    public ColorSetter(){

    }

    public static int newInstance(int mode){
        switch(mode){
            case 0:
                return Color.parseColor("#ff9800");
            case 1:
                return Color.parseColor("#259b24");
            case 2:
                return Color.parseColor("#e51c23");
            case 3:
                return Color.parseColor("#03a9f4");
            case 4:
                return Color.parseColor("#ff5722");
            case 11:
                return Color.parseColor("#AAAAAA");
            default:
                return Color.parseColor("#607d8b");
        }

    }

    public static int colorRoulette(int position) {

        position = position%9;

        switch (position) {
            case 0:
                return Color.parseColor("#03A9F4");
            case 1:
                return Color.parseColor("#00BCD4");
            case 2:
                return Color.parseColor("#009688");
            case 3:
                return Color.parseColor("#4CAF50");
            case 4:
                return Color.parseColor("#8BC34A");
            case 5:
                return Color.parseColor("#CDDC39");
            case 6:
                return Color.parseColor("#FFEB3B");
            case 7:
                return Color.parseColor("#FFC107");
            case 8:
                return Color.parseColor("#FF9800");
            case 9:
                return Color.parseColor("#FF5722");
            default:
                return Color.parseColor("#607D8B");
        }
    }
}
