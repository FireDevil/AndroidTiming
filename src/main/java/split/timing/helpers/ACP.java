package split.timing.helpers;

import android.app.Application;
import android.content.Context;

/**
 * Created by Antec on 17.01.14.
 */
public class ACP extends Application {

    /**
     * Keeps a reference of the application context
     */
    private static Context sContext;


    @Override
    public void onCreate() {
        super.onCreate();

        sContext = getApplicationContext();
    }

    public static Context getContext() {
        return sContext;
    }

}