package split.timing.items;

/**
 * Created by Antec on 08.01.14.
 */
public class ResultItem {

    int id;
    private String Name;
    private String Number;
    private int position;
    private long run;
    private long difference;
    private long timed;
    private int group;
    private int lap;
    private Sportsmen sportsmen;
    private Startlist startlistItem;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNumber(String num) {
        Number = num;
    }

    public String getNumber(){
        return Number;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public long getRun() {
        return run;
    }

    public void setRun(long run) {
        this.run = run;
    }

    public long getDifference() {
        return difference;
    }

    public void setDifference(long difference) {
        this.difference = difference;
    }

    public long getTimed() {
        return timed;
    }

    public void setTimed(long timed) {
        this.timed = timed;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public int getLap() {
        return lap;
    }

    public void setLap(int lap) {
        this.lap = lap;
    }

    public Sportsmen getSportsmen() {
        return sportsmen;
    }

    public void setSportsmen(Sportsmen sportsmen) {
        this.sportsmen = sportsmen;
    }

    public Startlist getStartlistItem() {
        return startlistItem;
    }

    public void setStartlistItem(Startlist startlistItem) {
        this.startlistItem = startlistItem;
    }
}
