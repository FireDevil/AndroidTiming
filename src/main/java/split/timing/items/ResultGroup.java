package split.timing.items;

import java.util.ArrayList;

/**
 * Created by Antec on 14.01.14.
 */
public class ResultGroup {

    String mName;
    int lap;
    int group;

    String mInfo;
    ArrayList<ResultItem> mList;

    public String getInfo() {
        return mInfo;
    }

    public void setInfo(String mInfo) {
        this.mInfo = mInfo;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public ArrayList<ResultItem> getChildren() {
        return mList;
    }

    public void setList(ArrayList<ResultItem> mList) {
        this.mList = mList;
    }

    public int getLap() {
        return lap;
    }

    public void setLap(int lap) {
        this.lap = lap;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public int getChildrenSize(){
        return mList.size();
    }
}
