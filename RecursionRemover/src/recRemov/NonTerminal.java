package recRemov;

import java.util.ArrayList;

public class NonTerminal {
	private char name; // Non-terminal name
	private ArrayList<String> generates; //Keeps what the non-terminal generates
	private ArrayList<Character> leadsTo; //Keeps the non-terminals that this nt leads to

	//***************************************
	// CONSTRUCTOR
	// **************************************
	public NonTerminal (char name){
		this.name = name;
		generates = new ArrayList<String>();
		leadsTo = new ArrayList<Character>();
	}
	
	//***************************************
	// Add itens and check if they are already
	// contained in "leadsTo"
	// **************************************
	public boolean leadsToContains(char character){
		if (leadsTo.contains(character)){
			return true;
		} else {		
			return false;
		}
	}
	
	public void leadsToAdd(char character){
		leadsTo.add(character);
	}
	
	//***************************************
	// Add itens and check if they are already
	// contained in "generates "
	// **************************************
	public boolean generatesContains(String sentence){
		if (generates.contains(sentence)){
			return true;
		} else {		
			return false;
		}
	}
	
	public void generatesAdd(String sentence){
		generates.add(sentence);
	}
	
	//***************************************
	// GETTERS AND SETTERS
	// **************************************
	public ArrayList<String> getGenerates() {
		return generates;
	}
	
	public ArrayList<Character> getLeadsTo() {
		return leadsTo;
	}
	
	public void setGenerates(ArrayList<String> generates) {
		this.generates = generates;
	}	

	public void setLeadsTo(ArrayList<Character> leadsTo) {
		this.leadsTo = leadsTo;
	}

	public char getName() {
		return name;
	}

	public void setName(char name) {
		this.name = name;
	}
	
}
