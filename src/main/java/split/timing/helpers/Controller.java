package split.timing.helpers;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import split.timing.items.Competition;
import split.timing.items.Sportsmen;
import split.timing.items.Startgroup;
import split.timing.items.Startlist;
import split.timing.items.Timed;

public class Controller {

    private HashMap<Integer, Competition> competitions;
    private ArrayList<Startgroup> startgroups;
    private ArrayList<Integer> startNumbers;
    private ArrayList<Sportsmen> sportsmen;
    private TreeMap<Integer, Startlist> startlistElements;
    private HashMap<Integer, Sportsmen> numbers;
    private HashMap<Integer, Startgroup> groups;
    private HashMap<Integer,Integer> lapCounter;
    private int selectedCompetition = -1;
    private int selectedStartgroup = -1;

    private static final class InstanceHolder {
        // Die Initialisierung von Klassenvariablen geschieht nur einmal
        // und wird vom ClassLoader implizit synchronisiert

        static final Controller INSTANCE = new Controller();
    }

    // Verhindere die Erzeugung des Objektes über andere Methoden
    private Controller() {
        startgroups = new ArrayList<Startgroup>();
        sportsmen = new ArrayList<Sportsmen>();
        startlistElements = new TreeMap<Integer, Startlist>();
        numbers = new HashMap<Integer, Sportsmen>();
        startNumbers = new ArrayList<Integer>();
        groups = new HashMap<Integer, Startgroup>();
        competitions = new HashMap<Integer, Competition>();
        lapCounter = new HashMap<Integer, Integer>();
    }

    // Eine nicht synchronisierte Zugriffsmethode auf Klassenebene.
    public static Controller getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public void clearData() {
        startgroups = new ArrayList<Startgroup>();
        sportsmen = new ArrayList<Sportsmen>();
        startNumbers = new ArrayList<Integer>();
        startlistElements = new TreeMap<Integer, Startlist>();
        numbers = new HashMap<Integer, Sportsmen>();
        groups = new HashMap<Integer, Startgroup>();
        lapCounter = new HashMap<Integer, Integer>();
        selectedCompetition = -1;
        selectedStartgroup = -1;
        competitions.clear();
    }

    public ArrayList getStartgroups() {
        return startgroups;
    }

    public void setStartgroups(ArrayList<Startgroup> startgroups) {

        this.startgroups = startgroups;
        groups.clear();
        startNumbers.clear();

        for (Startgroup startgroup : startgroups) {

            if (!startNumbers.contains(startgroup.getStartNum())) {
                startNumbers.add(startgroup.getStartNum());
            }

            if (groups.get(startgroup.getId()) == null) {
                groups.put(startgroup.getId(), startgroup);
            }
        }
    }

    public ArrayList getSportsmen() {
        return sportsmen;
    }

    public void setSportsmen(ArrayList<Sportsmen> sportsmen) {
        this.sportsmen = sportsmen;

        for (Sportsmen men : sportsmen) {
            if (numbers.get(men.getId()) == null) {
                numbers.put(men.getId(), men);
            }
        }
    }

    public TreeMap<Integer, Startlist> getStartlistElements() {
        return startlistElements;
    }

    public void setStartlistElements(
            ArrayList<Startlist> startlistElements) {

        this.startlistElements.clear();

        for (Startlist startlist : startlistElements) {
            if (this.startlistElements.get(startlist.getNumber()) == null) {
                this.startlistElements.put(startlist.getNumber(), startlist);
            }
        }
    }

    public void addStartlistElement(Startlist e) {

    }

    public int getSelectedCompetition() {
        return this.selectedCompetition;
    }

    public void setSelectedCompetition(int selectedCompetition) {
        this.selectedCompetition = selectedCompetition;
    }

    public int getSelectedStartgroup() {
        return selectedStartgroup;
    }

    public void setSelectedStartgroup(int selectedStartgroup) {
        this.selectedStartgroup = selectedStartgroup;
    }

    public HashMap<Integer, Sportsmen> getNumbers() {
        return numbers;
    }

    public ArrayList<Integer> getStartNumbers() {
        return startNumbers;
    }

    public void setStartNumbers(ArrayList<Integer> startNumbers) {
        this.startNumbers = startNumbers;
    }

    public HashMap<Integer,Competition> getCompetitions() {
        return competitions;
    }

    public void setCompetitions(ArrayList<Competition> competitions) {

        clearData();

        for (Competition c : competitions) {
            if (this.competitions.get(c.getId()) == null) {
                this.competitions.put(c.getId(), c);
            }
        }
    }


    public HashMap<Integer, Startgroup> getGroups() {
        return groups;
    }

    public void loadSportsmen() {

        DBHelper db = new DBHelper();
        Cursor c = db.select("SELECT * FROM Sportsmen");

       this.sportsmen = new ArrayList<Sportsmen>();
        while (c.moveToNext()) {
            sportsmen.add(new Sportsmen(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getInt(4), c.getInt(5), c.getString(6), c.getString(7)));
        }


        db.close();

        setSportsmen(sportsmen);
    }

    public static int[] parseDate(String date){
        int[] result = new int[3];

        String sub = "";
        int temp = date.indexOf('.');
        result[0] = Integer.parseInt(date.substring(0, date.indexOf('.')));
        sub = date.substring(temp + 1, date.length());
        result[1] = Integer.parseInt(sub.substring(0, sub.indexOf('.'))) - 1;
        temp = sub.indexOf('.') + 1;
        sub = sub.substring(temp);
        result[2] = Integer.parseInt(sub);

        return result;
    }

    public HashMap<Integer, Integer> getLapCounter() {
        return lapCounter;
    }

    public void setLapCounter(HashMap<Integer, Integer> lapCounter) {
        this.lapCounter = lapCounter;
    }

    public void putLapCounter(int number){
        if(lapCounter.containsKey(number)){
            int lap = lapCounter.get(number);
            lap++;
            lapCounter.remove(number);
            lapCounter.put(number,lap);
        }else{
            lapCounter.put(number,1);
        }
    }

    public void cutLapCounter(int number){
        if(lapCounter.containsKey(number)){
            if(lapCounter.get(number) > 1){
                int lap = lapCounter.get(number);
                lap--;
                lapCounter.remove(number);
                lapCounter.put(number,lap);
            }else{
                lapCounter.remove(number);
            }
        }
    }

    public Timed getTimedObject(int timedId){

        Timed timed = new Timed();

        DBHelper dbHelper = new DBHelper();
        Cursor cursor = dbHelper.select("SELECT * FROM Timed WHERE _id="+timedId);

        if(cursor.getCount() == 0){
            return null;
        }

        cursor.moveToFirst();

        timed.setId(cursor.getInt(0));
        timed.setNumber(cursor.getInt(1));
        timed.setTimedInMillis(cursor.getLong(2));
        timed.setRunInMillis(cursor.getLong(3));
        timed.setLap(cursor.getInt(4));
        timed.setCompetitionId(cursor.getInt(5));

        cursor.close();
        dbHelper.close();

        return timed;
    }

    public void reorderTimedObjects(int number){
        int highLap = 0;
        DBHelper db = new DBHelper();
        Cursor cursor = db.select("SELECT * FROM Timed WHERE number="+number+" AND competitionId="+getSelectedCompetition()+" ORDER BY timed ASC");

        int lap = 1;
        while (cursor.moveToNext()){
            ContentValues values = new ContentValues();
            values.put("lap",lap);
            db.update("Timed",values," _id="+cursor.getInt(0));

            highLap = lap;
            lap++;
        }

        cursor.close();
        db.close();
    }
}
