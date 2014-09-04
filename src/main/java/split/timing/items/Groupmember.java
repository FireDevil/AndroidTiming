package split.timing.items;

/**
 * Created by Antec on 01.04.2014.
 */
public class Groupmember  {

    private int id;
    private int groupId;
    private int sportsmenId;

    public Groupmember(){}

    public Groupmember(int id, int groupId, int sportsmenId) {
        this.id = id;
        this.groupId = groupId;
        this.sportsmenId = sportsmenId;
    }

    public Groupmember(int groupId, int sportsmenId) {
        this.groupId = groupId;
        this.sportsmenId = sportsmenId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getSportsmenId() {
        return sportsmenId;
    }

    public void setSportsmenId(int sportsmenId) {
        this.sportsmenId = sportsmenId;
    }
}
