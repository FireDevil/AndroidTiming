package split.timing.items;

import java.util.ArrayList;

/**
 * Created by Antec on 25.03.2014.
 */
public class Group {

    private int id=-1;
    private String name;
    private ArrayList<Sportsmen> sportsmens;

    public Group(){

    }

    public Group(int id, String name) {
        this.id = id;
        this.name = name;
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

    public ArrayList<Sportsmen> getSportsmens() {
        return sportsmens;
    }

    public void setSportsmens(ArrayList<Sportsmen> sportsmens) {
        this.sportsmens = sportsmens;
    }

    public int getCount(){
        return sportsmens.size();
    }

    @Override
    public String toString(){
        return getName();
    }
}
