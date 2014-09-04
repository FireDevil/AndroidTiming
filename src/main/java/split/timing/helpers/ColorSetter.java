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
                return Color.parseColor("#ff983c");
            case 1:
                return Color.parseColor("#99CC00");
            case 2:
                return Color.parseColor("#CC0000");
            case 3:
                return Color.parseColor("#0099CC");
            case 4:
                return Color.parseColor("#9933CC");
            case 11:
                return Color.parseColor("#AAAAAA");
            default:
                return Color.parseColor("#ff983c");
        }

    }
}
