package analiseSintatica;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;


//try{
//    FileWriter out = new FileWriter("src/files/output.txt");
//
//    out.write("linhas");
//    out.close();
//} catch (Exception e){
//    // e.printStackTrace();
//    System.out.println("Nao foi possivel operar no arquivo de output\n");
//}

public class ANSintatica {
	
	 private static void anSintatica(ArrayList<Token> tokens){
		 
	 }

	 private static ArrayList<Token> obterTokens (String path){
		Scanner input = null;
		ArrayList<Token> listaTokens = new ArrayList<Token>();
		
		 // Trying to create scanner
        try
        {
        	input = new Scanner(new File(path));
        }
        catch(FileNotFoundException e)
        {
            System.out.println("Erro: Arquivo nao encontrado");
            return null;
        }

        // Obtaining file content
        System.out.println("Comeco:");
        
        String str = "";
       
        while(input.hasNextLine()){
            str = input.nextLine();
        	if(str.equals("Tabela:")){
        		break;            	
            }
        }
        
        while(input.hasNextLine()){
            str = input.nextLine();
        	
            if(str == null || str.isEmpty()) { break; }
            
            listaTokens.add(new Token(str));
        }
        
        input.close();
        
        return listaTokens;
	}
	
	public static void main (String[] args){
		ArrayList<Token> listaTokens = new ArrayList<Token>();
		String path = args[0];
	
		listaTokens = obterTokens(path);
		
		
	}
}
