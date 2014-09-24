package split.timing.helpers;

import java.util.ArrayList;
import java.util.HashMap;

import split.timing.items.Competition;
import split.timing.items.Sportsmen;
import split.timing.items.Startgroup;
import split.timing.items.Startlist;

public class Controller {

    private Competition competition;
	private ArrayList<Startgroup> startgroups;
    private ArrayList<Integer> startNumbers;
	private ArrayList<Sportsmen> sportsmen;
	private HashMap <Integer,Startlist> startlistElements;
	private HashMap <Integer,Sportsmen> numbers;
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
		startlistElements = new HashMap<Integer,Startlist>();
		numbers = new HashMap<Integer,Sportsmen>();
        competition = null;
	}

	// Eine nicht synchronisierte Zugriffsmethode auf Klassenebene.
	public static Controller getInstance() {
		return InstanceHolder.INSTANCE;
	}

	public void setData(ArrayList startgroups, ArrayList sportsmen,
			ArrayList<Startlist> startlistElements,
			int selectedCompetition,
			int selectedStartgroup) {
		setStartgroups(startgroups);
		setSportsmen(sportsmen);
		this.selectedCompetition = selectedCompetition;
		this.selectedStartgroup = selectedStartgroup;
        setStartlistElements(startlistElements);

	}

    public void clearData(){
        startgroups = new ArrayList<Startgroup>();
        sportsmen = new ArrayList<Sportsmen>();
        startlistElements = new HashMap<Integer,Startlist>();
        numbers = new HashMap<Integer,Sportsmen>();
        selectedCompetition = -1;
        selectedStartgroup = -1;
        competition = null;
    }

	public ArrayList getStartgroups() {
		return startgroups;
	}

	public void setStartgroups(ArrayList<Startgroup> startgroups) {

        this.startgroups = startgroups;
        numbers.clear();
        startNumbers.clear();

        for(Startgroup startgroup : startgroups){
            for(Sportsmen men : startgroup.getSportsmens()){
                if(numbers.get(men.getId()) == null){
                    numbers.put(men.getId(),men);
                }

            }

            if(!startNumbers.contains(startgroup.getStartNum())) {
                startNumbers.add(startgroup.getStartNum());
            }
        }
	}

	public ArrayList getSportsmen() {
		return sportsmen;
	}

	public void setSportsmen(ArrayList<Sportsmen> sportsmen) {
        this.sportsmen = sportsmen;

        for(Sportsmen men:sportsmen){
            if(numbers.get(men.getId()) == null){
                numbers.put(men.getId(),men);
            }
        }
	}

	public HashMap<Integer,Startlist> getStartlistElements() {
		return startlistElements;
	}

	public void setStartlistElements(
			ArrayList<Startlist> startlistElements) {

        startlistElements.clear();

        for(Startlist startlist : startlistElements){
            if(startlistElements.get(startlist.getNumber()) == null) {
                this.startlistElements.put(startlist.getNumber(), startlist);
            }
        }
	}

	public void addStartlistElement(Startlist e) {

	}

	public int getSelectedCompetition() {
		return selectedCompetition;
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

    public HashMap<Integer,Sportsmen> getNumbers() {
		return numbers;
	}

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }


    public ArrayList<Integer> getStartNumbers() {
        return startNumbers;
    }

    public void setStartNumbers(ArrayList<Integer> startNumbers) {
        this.startNumbers = startNumbers;
    }


}
