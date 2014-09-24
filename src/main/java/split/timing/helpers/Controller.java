package de.novasib.ttsib5.resources.ui.internal;

import java.util.ArrayList;

public class Controller {

	private ArrayList startgroups;
	private ArrayList sportsmen;
	private ArrayList<StartlistElement> startlistElements;
	private ArrayList<Integer> numbers;
	private int selectedCompetition;
	private int selectedStartgroup;

	private static final class InstanceHolder {
		// Die Initialisierung von Klassenvariablen geschieht nur einmal
		// und wird vom ClassLoader implizit synchronisiert

		static final Controller INSTANCE = new Controller();
	}

	// Verhindere die Erzeugung des Objektes über andere Methoden
	private Controller() {
		startgroups = new ArrayList();
		sportsmen = new ArrayList();
		startlistElements = new ArrayList<StartlistElement>();
		numbers = new ArrayList<Integer>();
	}

	// Eine nicht synchronisierte Zugriffsmethode auf Klassenebene.
	public static Controller getInstance() {
		return InstanceHolder.INSTANCE;
	}

	public void setData(ArrayList startgroups, ArrayList sportsmen,
			ArrayList<StartlistElement> startlistElements,
			ArrayList<Integer> numbers, int selectedCompetition,
			int selectedStartgroup) {
		this.startgroups = startgroups;
		this.sportsmen = sportsmen;
		this.startlistElements = startlistElements;
		this.numbers = numbers;
		this.selectedCompetition = selectedCompetition;
		this.selectedStartgroup = selectedStartgroup;

	}

	public ArrayList getStartgroups() {
		return startgroups;
	}

	public void setStartgroups(ArrayList startgroups) {
		this.startgroups = startgroups;
	}

	public ArrayList getSportsmen() {
		return sportsmen;
	}

	public void setSportsmen(ArrayList sportsmen) {
		this.sportsmen = sportsmen;
	}

	public ArrayList getStartlistElements() {
		return startlistElements;
	}

	public void setStartlistElements(
			ArrayList<StartlistElement> startlistElements) {
		this.startlistElements = startlistElements;

		for (StartlistElement e : startlistElements) {
			numbers.add(e.getNumber());
		}
	}

	public void addStartlistElement(StartlistElement e) {
		startlistElements.add(e);
		numbers.add(e.getNumber());
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

	public ArrayList<Integer> getNumbers() {
		return numbers;
	}

}
