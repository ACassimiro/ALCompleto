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
	
	public static void main (String []args){ 
		String content;
		ArrayList<NonTerminal> ntList = new ArrayList<NonTerminal>();
		ArrayList<Character> path = new ArrayList<Character>();
		ArrayList<String> beta = new ArrayList<String>();
		
		content = getInput("src/files/input2.txt");

		ntParser(content, ntList);

		// printNTList(ntList);

		ntList = findPath(ntList);
		
		Collections.reverse(ntList);
		 
		if (ntList.size() > 0) {
			
			for(NonTerminal nonTerm : ntList){
				
				for(String generates : nonTerm.getGenerates()){
					
					if(generates.indexOf(ntList.get(ntList.size() - 1).getName()) >= 0){
						for(String gen : nonTerm.getGenerates()){
							if(gen.indexOf(ntList.get(ntList.size() - 1).getName()) >= 0){
								;
							} else {
								beta.add(generates.substring(generates.indexOf(ntList.get(ntList.size() - 1).getName()) + 1, generates.length()));
							}

						}
						
					} 
					
				}
				
			}	
			
		}
		
		System.out.println(":");
		for(String b : beta){
			System.out.print(b + " ");
		}
		
		  
		NonTerminal aux = null;
		
	}
}


