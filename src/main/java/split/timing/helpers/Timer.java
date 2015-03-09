package split.timing.helpers;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.format.Time;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;

import split.timing.items.Competition;
import split.timing.items.Startgroup;
import split.timing.items.Startlist;
import split.timing.items.Timed;

/**
 * Created by Antec on 18.10.2014.
 */
public class Timer {

    public static Time substractTimes(long first, long second) {

        long tmp = (second - first) / 1000;

        if (tmp < 0) {
            tmp = -tmp;
        }

        return secondsToTime(tmp);
    }

    public static Time secondsToTime(long millis) {

        if (millis < 0) {
            millis = -millis;
        }


        int m = (int) (millis / 60) % 60;
        int h = (int) millis / 3600;
        int s = (int) millis % 60;
        int d = h / 24;

        if (h > 23) {
            h = h % 24;
        }

        Time ret = new Time();
        ret.set(s, m, h, d, 0, 0);
        return ret;
    }

    public static long dateToMillis(int year, int month, int day, int hour, int minute, int second) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(year, month, day, hour, minute, second);
        return calendar.getTimeInMillis();
    }

    public static Timed newTiming(int number) {

        Timed timed = new Timed();
        Date end = new Date(System.currentTimeMillis());
        Controller controller = Controller.getInstance();
        Competition competition = controller.getCompetitions().get(controller.getSelectedCompetition());
        int competitionId = controller.getSelectedCompetition();

        if(number < 0){
            timed.setNumber(number);
            timed.setTimedInMillis(end.getTime());
            timed.setRunInMillis(0);
            timed.setLap(0);

            DBHelper dbHelper = new DBHelper();
            ContentValues values = new ContentValues();
            values.put("number", number);
            values.put("timed", timed.getTimedInMillis() + "");
            values.put("run", timed.getRunInMillis() + "");
            values.put("lap", timed.getLap());
            values.put("competitionId",competitionId);
            dbHelper.insert("Timed", values);
            dbHelper.close();

            dbHelper = new DBHelper();
            Cursor cursor = dbHelper.select("SELECT _id FROM Timed WHERE competitionId=" + competition.getId() + " AND number='" + number+"'");

            cursor.moveToLast();
            timed.setId(cursor.getInt(0));

            cursor.close();
            dbHelper.close();

            return timed;
        }


        controller.putLapCounter(number);


        Startlist startlist = controller.getStartlistElements().get(number);
        Startgroup startgroup = controller.getGroups().get(startlist.getStartId());

        String date = competition.getDate();
        int[] arr = controller.parseDate(date);
        int day = arr[0];
        int month = arr[1];
        int year = arr[2];

        long start = Timer.dateToMillis(year, month, day, startgroup.getStartHour(), startgroup.getStartMinute(), startgroup.getStartSecond());
        start = start + startlist.getDifference() * 1000;

        if(start > end.getTime()){
            start = end.getTime();
        }

        timed.setNumber(number);
        timed.setTimedInMillis(end.getTime());
        timed.setRunInMillis(end.getTime() - start);
        timed.setLap(controller.getLapCounter().get(number));

        DBHelper dbHelper = new DBHelper();
        ContentValues values = new ContentValues();
        values.put("number", number);
        values.put("timed", timed.getTimedInMillis() + "");
        values.put("run", timed.getRunInMillis() + "");
        values.put("lap", controller.getLapCounter().get(number));
        values.put("competitionId",competitionId);
        dbHelper.insert("Timed", values);
        dbHelper.close();

        dbHelper = new DBHelper();
        Cursor cursor = dbHelper.select("SELECT Timed._id FROM Timed JOIN Startlist WHERE Startlist.startgroupId =" + startgroup.getId() + " AND Startlist.competitionId=" + competition.getId() + " AND Timed.number=" + number);

        cursor.moveToLast();
        timed.setId(cursor.getInt(0));

        cursor.close();
        dbHelper.close();

        return timed;
    }

    public static boolean calculateResult(int number) {

        Controller controller = Controller.getInstance();

        Startlist startlist = controller.getStartlistElements().get(number);
        int highLap;
        ArrayList<Timed> timings = new ArrayList<Timed>();

        DBHelper dbHelper = new DBHelper();
        Cursor time = dbHelper.select("SELECT MAX(lap) FROM Timed JOIN Startlist WHERE Timed.number=" + number + " AND Startlist._id=" + startlist.getId()+ " AND Timed.competitionId="+controller.getSelectedCompetition());

        if (time.getCount() > 0) {
            time.moveToFirst();
            highLap = time.getInt(0);
        } else {
            return false;
        }

        time.close();

        Cursor result = dbHelper.select("SELECT * FROM Timed JOIN Startlist WHERE Startlist.startgroupId=" + startlist.getStartId() + " AND Timed.lap=" + highLap + " AND Timed.number=Startlist.number AND Timed.competitionId="+controller.getSelectedCompetition());
        timings = new ArrayList<>();

        if (result.getCount() < 1) {
            return false;
        }

        while (result.moveToNext()) {
            Timed t = new Timed();
            t.setId(result.getInt(0));
            t.setNumber(result.getInt(1));
            t.setTimedInMillis(result.getLong(2));
            t.setRunInMillis(result.getLong(3));
            t.setLap(result.getInt(4));

            timings.add(t);
        }

        result.close();

        Collections.sort(timings, new Comparator<Timed>() {
            @Override
            public int compare(Timed lhs, Timed rhs) {
                return (int) (lhs.getRunInMillis() - rhs.getRunInMillis());
            }
        });

        dbHelper.sqlCommand("DELETE FROM Results WHERE lap =" + highLap + " AND startgroupId=" + startlist.getStartId());

        long diff;
        ArrayList<ContentValues> valuesArrayList = new ArrayList<>();

        for (Timed t : timings) {

            if (timings.indexOf(t) == 0 && timings.size() > 1) {
                diff = timings.get(1).getRunInMillis();
            } else {
                diff = timings.get(0).getRunInMillis();
            }

            ContentValues values = new ContentValues();
            values.put("number", t.getNumber());
            values.put("position", timings.indexOf(t) + 1);
            values.put("timed", t.getTimedInMillis());
            values.put("run", t.getRunInMillis());
            values.put("difference", t.getRunInMillis() - diff);
            values.put("lap", t.getLap());
            values.put("competitionId", controller.getSelectedCompetition());
            values.put("startgroupId", startlist.getStartId());
            valuesArrayList.add(values);
        }

        for (ContentValues values : valuesArrayList) {
            dbHelper.insert("Results", values);
        }
        dbHelper.close();

        return true;
    }

    public static boolean completeCalculation() {

        Controller controller = Controller.getInstance();
        ArrayList<Timed> timings = new ArrayList<Timed>();
        DBHelper dbHelper = new DBHelper();

        Cursor startgroups = dbHelper.select("SELECT DISTINCT startgroupId FROM Startlist WHERE competitionId="+controller.getSelectedCompetition());
        startgroups.moveToFirst();
        Cursor laps = dbHelper.select("SELECT DISTINCT lap FROM Timed WHERE competitionId="+controller.getSelectedCompetition());
        while (laps.moveToNext()){

            while(!startgroups.isAfterLast()) {
                Cursor result = dbHelper.select("SELECT * FROM Timed JOIN Startlist WHERE Startlist.startgroupId=" + startgroups.getInt(0) + " AND Timed.lap=" + laps.getInt(0) + " AND Timed.number=Startlist.number AND Timed.competitionId=" + controller.getSelectedCompetition());
                timings = new ArrayList<Timed>();

                if (result.getCount() < 1) {
                    startgroups.moveToNext();
                    result.close();
                    continue;
                }

                while (result.moveToNext()) {
                    Timed t = new Timed();
                    t.setId(result.getInt(0));
                    t.setNumber(result.getInt(1));
                    t.setTimedInMillis(result.getLong(2));
                    t.setRunInMillis(result.getLong(3));
                    t.setLap(result.getInt(4));

                    timings.add(t);
                }

                result.close();

                Collections.sort(timings, new Comparator<Timed>() {
                    @Override
                    public int compare(Timed lhs, Timed rhs) {
                        return (int) (lhs.getRunInMillis() - rhs.getRunInMillis());
                    }
                });

                dbHelper.sqlCommand("DELETE FROM Results WHERE lap =" + laps.getInt(0) + " AND startgroupId=" + startgroups.getInt(0));

                long diff = 0;
                ArrayList<ContentValues> valuesArrayList = new ArrayList<>();

                for (Timed t : timings) {

                    if (timings.indexOf(t) == 0 && timings.size() > 1) {
                        diff = timings.get(1).getRunInMillis();
                    } else {
                        diff = timings.get(0).getRunInMillis();
                    }

                    ContentValues values = new ContentValues();
                    values.put("number", t.getNumber());
                    values.put("position", timings.indexOf(t) + 1);
                    values.put("timed", t.getTimedInMillis());
                    values.put("run", t.getRunInMillis());
                    values.put("difference", t.getRunInMillis() - diff);
                    values.put("lap", t.getLap());
                    values.put("competitionId", controller.getSelectedCompetition());
                    values.put("startgroupId", startgroups.getInt(0));
                    valuesArrayList.add(values);
                }

                for (ContentValues values : valuesArrayList) {
                    dbHelper.insert("Results", values);
                }

                startgroups.moveToNext();

            }

            startgroups.moveToFirst();

        }

        startgroups.close();
        laps.close();

        dbHelper.close();

        return true;
    }
}
