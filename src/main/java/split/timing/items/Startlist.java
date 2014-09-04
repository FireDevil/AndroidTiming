package split.timing.items;

import java.util.ArrayList;

/**
 * Created by Antec on 01.04.2014.
 */
public class Startlist {

    int id;
    int number;
    boolean jersey;
    int compId;
    int startId;
    int sportsmenId;
    int startposition;
    ArrayList<Startgroup> startgroups;

    public Startlist(){

    }

    public Startlist(int id, int number, boolean jersey, int compId, int startId, int sportsmenId, int startposition) {
        this.id = id;
        this.number = number;
        this.jersey = jersey;
        this.compId = compId;
        this.startId = startId;
        this.sportsmenId = sportsmenId;
        this.startposition = startposition;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isJersey() {
        return jersey;
    }

    public void setJersey(boolean jersey) {
        this.jersey = jersey;
    }

    public int getCompId() {
        return compId;
    }

    public void setCompId(int compId) {
        this.compId = compId;
    }

    public int getStartId() {
        return startId;
    }

    public void setStartId(int startId) {
        this.startId = startId;
    }

    public int getSportsmenId() {
        return sportsmenId;
    }

    public void setSportsmenId(int sportsmenId) {
        this.sportsmenId = sportsmenId;
    }

    public int getStartposition() {
        return startposition;
    }

    public void setStartposition(int startposition) {
        this.startposition = startposition;
    }

    public ArrayList<Startgroup> getStartgroups() {
        return startgroups;
    }

    public void setStartgroups(ArrayList<Startgroup> startgroups) {
        this.startgroups = startgroups;
    }
}
