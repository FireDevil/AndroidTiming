package split.timing.items;

/**
 * Created by Antec on 25.03.2014.
 */
public class Sportsmen {

    int id = -1;
    String name;
    String lastName;
    String birthday;
    int year = 0;
    int age = 0;
    String club;
    String Federation;

    public Sportsmen(){

    }

    public Sportsmen(int id, String name, String lastname, String birthday, int year, int age, String club, String federation){
        setId(id);
        setName(name);
        setLastName(lastname);
        setBirthday(birthday);
        setYear(year);
        setAge(age);
        setClub(club);
        setFederation(federation);
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBirthday() {
        if(birthday.equals("")){
            return "1.1";
        }
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getYear(){
        if(year == 0){
            return 2014;
        }
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getClub() {
        return club;
    }

    public void setClub(String club) {
        this.club = club;
    }

    public String getFederation() {
        return Federation;
    }

    public void setFederation(String federation) {
        Federation = federation;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString(){
        return getName()+" "+getLastName();
    }

}
