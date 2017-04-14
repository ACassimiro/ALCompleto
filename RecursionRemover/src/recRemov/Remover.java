package recRemov;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

class Remover {

	private static String getInput(String path) {
		String content = "";
		
		Scanner input = null;
        
        // Trying to create scanner
        try
        {
        	input = new Scanner(new File(path));
        }
        catch(FileNotFoundException e)
        {
            System.out.println("File not found");
            return "";
        }

        // Obtaining file content
        while(input.hasNextLine())
            content += input.nextLine() + "\n";     
        
        input.close();
		return content;
	}
	
	
	private static void ntParser(String fileContent, ArrayList<NonTerminal> ntList){
		int count;
		char aux;
		String auxExp = "";
		NonTerminal ntAux = null;
		
		for(count = 0; count < fileContent.length(); count++){
			aux = fileContent.charAt(count);
			
			if(aux == '\n')
				break;
						
			ntAux = new NonTerminal(aux);
			auxExp = "";
			
			while(++count != 0){
				aux = fileContent.charAt(count);
				
				if(aux == '|'){
					if(!ntAux.generatesContains(auxExp)){
						ntAux.generatesAdd(auxExp);
					}
					auxExp = "";
				} else if(aux == '\n') {
					if(!ntAux.generatesContains(auxExp)){
						ntAux.generatesAdd(auxExp);
					}
					break;
				} else if (aux == ' ' || aux == '-'){
					;
				} else if (Character.isUpperCase(aux)){
					auxExp += aux;
					if(!ntAux.leadsToContains(aux)){
						ntAux.leadsToAdd(aux);
					}
				} else {
					auxExp += aux;
				}
			}
			
			ntList.add(ntAux);
		}
	}
	
	static void printNTList (ArrayList<NonTerminal> ntList){
		for (NonTerminal nonTerm : ntList){
			System.out.println("Nome do NT: " + nonTerm.getName());
			System.out.print("Lista de derivacoes: ");
			
			for (String exp : nonTerm.getGenerates()){
				System.out.print( exp + " ");
			}
			
			System.out.println("");
		}
	}
	
	static ArrayList<NonTerminal> findPath (ArrayList<NonTerminal> ntList){
		ArrayList<NonTerminal> path = new ArrayList<NonTerminal>();
		
		NonTerminal auxNT;
				
		auxNT = ntList.get(ntList.size() - 1); 
		char currentDeriv = ntList.get(ntList.size() - 1).getName();
		path.add(0, auxNT);		
		while(true){
			if(currentDeriv == ntList.get(0).getName())
				break;			
			
			for(NonTerminal nonTerm : ntList){
				if(nonTerm.getLeadsTo().contains(currentDeriv)){
					auxNT = nonTerm;
					currentDeriv = auxNT.getName();
					path.add(0, auxNT);					
					break;
				}
//				else {
//					for(char gen: nonTerm.getLeadsTo()){
//						System.out.print(gen + " ");
//					}
//					System.out.print("- does not generate " + currentDeriv + "\n");
//				}
			}
			
			

		}
		
		return path;
	}
	
	public static void removeRecursion(ArrayList<NonTerminal> ntList){
		String prefix = "";
		String sufix = "";
		char currentName;
		String lastRecursionSufix = "";
		NonTerminal dashedNT = new NonTerminal('X');
		NonTerminal replace = null;
		dashedNT.leadsToAdd('X');
		String auxNextPrefix;
		
		Collections.reverse(ntList);
		
		int auxSizeCounter = 0;
		boolean isFirst = true;
		if (ntList.size() > 1) {
			
			for(NonTerminal nonTerm : ntList){
				if(isFirst){
					for(String generates : nonTerm.getGenerates()){
						currentName = ntList.get(ntList.size() - 1).getName();
						if(generates.indexOf(currentName) >= 0){
							lastRecursionSufix = generates.substring(generates.indexOf(currentName) + 1, generates.length());
							break;
						} 
					}

					isFirst = false;
					continue;
				}
				
				currentName = ntList.get(auxSizeCounter).getName();
				
				for(String generates : nonTerm.getGenerates()){
					if(generates.indexOf(currentName) >= 0){
						sufix += generates.substring(generates.indexOf(currentName) + 1, generates.length());
						break;
					} 
				}

				auxSizeCounter++;
				
				if(auxSizeCounter == ntList.size())
					break;
				
			}	
			
			currentName = ntList.get(ntList.size() - 1).getName();
			
			for(String generates : ntList.get(0).getGenerates()){
				if(generates.indexOf(currentName) >= 0){
					sufix += generates.substring(generates.indexOf(currentName) + 1, generates.length());
					break;
				} 
			}
				
		}
		
		dashedNT.generatesAdd(sufix + "X");
		
		currentName = ntList.get(0).getName();
		for(String generates : ntList.get(0).getGenerates()){
			if(generates.indexOf(currentName) >= 0){
				sufix = generates.substring(generates.indexOf(currentName) + 1, generates.length());
				break;
			} 
		}
	
		dashedNT.generatesAdd(sufix + "X");
		dashedNT.generatesAdd("&" );		
				
		Collections.reverse(ntList);
		
		replace = new NonTerminal(ntList.get(ntList.size() - 1).getName());
		replace.leadsToAdd('X');
		auxSizeCounter = 1;
		prefix = "";
		if (ntList.size() > 1) {
			
			for(NonTerminal nonTerm : ntList){
				auxNextPrefix = "";
				currentName = ntList.get(auxSizeCounter).getName();
				for(String generates : nonTerm.getGenerates()){
					if(generates.indexOf(currentName) < 0){
						replace.generatesAdd(generates + prefix + lastRecursionSufix + "X");
					} else {
						auxNextPrefix = generates.substring(generates.indexOf(currentName) + 1, generates.length());
					}			
				}
				
				for(char leadsTo : nonTerm.getLeadsTo()){
					if(leadsTo == currentName || replace.leadsToContains(leadsTo)){
						continue;
					} else {
						replace.leadsToAdd(leadsTo);
					}
				}
				
				if(auxSizeCounter++ == ntList.size() - 1){
					break;
				}				
				
				prefix = auxNextPrefix + prefix;
			}	
			
			for(String generates : ntList.get(ntList.size() - 1).getGenerates()){
				if(generates.indexOf(ntList.get(ntList.size() - 1).getName()) < 0 || generates.indexOf(ntList.get(0).getName()) < 0){
					replace.generatesAdd(generates + "X");
				}			
			}
			
			for(char leadsTo : ntList.get(ntList.size() - 1).getLeadsTo()){
				if(leadsTo == currentName || replace.leadsToContains(leadsTo) || replace.leadsToContains(ntList.get(0).getName()) || replace.leadsToContains(ntList.get(ntList.size() - 1).getName())){
					continue;
				} else {
					replace.leadsToAdd(leadsTo);
				}
			}
			
		}
		 
		ntList.remove(ntList.size() - 1);
		
		ntList.add(replace);
		
		ntList.add(dashedNT);
	
	}
	
	
	public static void main (String []args){ 
		String content;
		ArrayList<NonTerminal> ntList = new ArrayList<NonTerminal>();
		
		content = getInput("src/files/input3.txt");

		ntParser(content, ntList);

		ntList = findPath(ntList);

		removeRecursion(ntList);
		
		printNTList(ntList);
	}
}


