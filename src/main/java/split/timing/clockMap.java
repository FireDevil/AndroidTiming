package split.timing;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.Time;
import android.view.Menu;
import android.view.View;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class clockMap extends Activity {

    Calendar cal;
    long t = 0;
    long r = 0;
    SimpleDateFormat sdf;
    SimpleDateFormat sd;
    TextView time;
    TextView span;
    TextView ref;
    Chronometer tick;
    Time a;
    Time b;
    Time comp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        a = new Time();

        /*tick = (Chronometer)findViewById(R.id.chrono);

        time = (TextView)findViewById(R.id.Time);
        span = (TextView)findViewById(R.id.Span);
        ref = (TextView)findViewById(R.id.Reference);
        Chronometer tick = (Chronometer)findViewById(R.id.chrono);*/

        tick.setVisibility(View.GONE);
        tick.setOnChronometerTickListener(new OnChronometerTickListener(){

            @Override
            public void onChronometerTick(Chronometer arg0) {
                b = new Time();
                b.setToNow();
                span.setText(formatTime(a.toMillis(true)+30000,b.toMillis(true)));
                ref.setText(b.format("%H:%M:%S"));
            }
        });
    }


    public void start(View v){
        a.setToNow();
        time.setText(a.format("%H:%M:%S"));

        tick.start();
    }

    public String formatTime(long first, long second){

        long tmp = (second-first)/1000;
        String pre =" ";

        if(tmp < 0){
            tmp = - tmp;
            pre ="-";
        }

        int m = (int) (tmp / 60)%60;
        int h = (int) tmp / 3600;
        int s = (int) tmp % 60;

        String hour="";
        String minute="";
        String seconds="";

        if(h < 10){
            hour = "0"+h;
        }else{
            hour = h+"";
        }

        if(m < 10){
            minute ="0"+m;
        }else{
            minute =""+m;
        }

        if(s < 10){
            seconds ="0"+s;
        }else{
            seconds = ""+s;
        }

        return hour+":"+minute+":"+seconds+pre;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
