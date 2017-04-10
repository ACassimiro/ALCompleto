package recRemov;

import java.io.*;
import java.util.ArrayList;
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
	
	
	private static void ntParser(ArrayList<NonTerminal> ntList, String fileContent){
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
				} else if (aux == ' '){
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
	
	
	public static void main (String []args){ 
		String content;
		
		content = getInput("src/files/input.txt");
		
		System.out.println(content);
		
	}
}


