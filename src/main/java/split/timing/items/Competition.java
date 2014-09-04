package split.timing.items;

import java.util.Calendar;

/**
 * Created by Antec on 25.03.2014.
 */
public class Competition {

    private int id = -1;
    private String name;
    private String date;
    private String location;
    private boolean finished;

    public void Startlist(){

    }

    public Competition(){
    }

    public Competition(String name, String date, String location) {
        this.name = name;
        this.date = date;
        this.location = location;
    }

    public Competition(int id, String name, String date, String location,int finished) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.location = location;
        if(finished == 0){
            this.finished = false;
        }else{
            this.finished = true;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {

        if(date.equals("")) {
            Calendar cal = Calendar.getInstance();
            return ""+cal.DAY_OF_MONTH+"."+cal.MONTH+"."+cal.YEAR;
        }
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    @Override
    public String toString(){
        return getName();
    }
}
