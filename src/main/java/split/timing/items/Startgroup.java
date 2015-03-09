package split.timing.items;

import java.util.ArrayList;

/**
 * Created by Antec on 25.03.2014.
 */
public class Startgroup extends Group {

    private int id = -1;
    private String name;
    private int interval;
    private int startHour;
    private int startMinute;
    private int startSecond;
    private int startNum;
    private int jerseyNum;
    private int mode = 0;
    private float distance;
    private ArrayList<Sportsmen> sportsmens;

    public Startgroup() {

    }

    public Startgroup(int id, String name, int startHour, int startMinute, int startSecond, int mode, float distance, int interval, int num) {
        this.id = id;
        this.name = name;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.mode = mode;
        this.distance = distance;
        this.interval = interval;
        this.startNum = num;
        this.startSecond = startSecond;
//        setJerseyNum(jerseyNum);
        this.sportsmens = new ArrayList<Sportsmen>();
    }

//    public void add(ArrayList<Sportsmen> additional) {
//        if (additional != null) {
//            this.sportsmens.addAll(additional);
//        }
//    }
//
//    public void add(Sportsmen single) {
//        this.sportsmens.add(single);
//    }
//
//    public void add(int emptyAdditional) {
//        Sportsmen men;
//        for (int i = 0; i < emptyAdditional; i++) {
//            men = new Sportsmen();
//            men.setName("Starter " + getCount());
//            men.setLastName("#");
//            this.sportsmens.add(men);
//        }
//    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public ArrayList<Sportsmen> getSportsmens() {
        return sportsmens;
    }

    public void setSportsmens(ArrayList<Sportsmen> sportsmens) {
        this.sportsmens = sportsmens;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStartNum() {
        return startNum;
    }

    public void setStartNum(int startNum) {
        this.startNum = startNum;
    }

//    public int getJerseyNum() {
//        return jerseyNum;
//    }
//
//    public void setJerseyNum(int jerseyNum) {
//        if (jerseyNum < startNum && jerseyNum >= 0) {
//            this.jerseyNum = startNum;
//        } else {
//            this.jerseyNum = jerseyNum;
//        }
//    }

    public int getStartSecond() {
        return startSecond;
    }

    public void setStartSecond(int startSecond) {
        this.startSecond = startSecond;
    }

    @Override
    public int getCount() {
        return sportsmens.size();
    }

    @Override
    public String toString(){
        return getId()+"";
    }
}
