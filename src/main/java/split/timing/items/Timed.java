package split.timing.items;

/**
 * Created by Antec on 09.10.2014.
 */
public class Timed {

    int id;
    int number;
    long timedInMillis;
    long runInMillis;
    int lap;
    int competitionId;

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

    public long getTimedInMillis() {
        return timedInMillis;
    }

    public void setTimedInMillis(long timedInMillis) {
        this.timedInMillis = timedInMillis;
    }

    public long getRunInMillis() {
        return runInMillis;
    }

    public void setRunInMillis(long runInMillis) {
        this.runInMillis = runInMillis;
    }

    public int getLap() {
        return lap;
    }

    public void setLap(int lap) {
        this.lap = lap;
    }

    public int getCompetitionId() {return competitionId;}

    public void setCompetitionId(int competitionId) {this.competitionId = competitionId;}
}
