package analiseSemantica;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


public class ANSemantica {

	private static void buscaEmPilha(ArrayList<Variavel> pilhaVar, String nome, int nivel){
		int ultimoEscopoEncontrado = 0;
		int escopoAtual = 0;
		for(Variavel var : pilhaVar){
			if(var.getNome().equals("$")){
				escopoAtual++;
			}else if(nome.equals(var.getNome())){
				if(escopoAtual == ultimoEscopoEncontrado && escopoAtual == nivel){
					System.out.println("Variavel " + nome + " declarada mais de uma vez no mesmo escopo");
				} else {
					ultimoEscopoEncontrado = escopoAtual;
				}
			}
		}
		
	}

	private static void analiseDeEscopo(ArrayList<Token> tokens, ArrayList<Variavel> pilhaVar, int count, int nivel){
		boolean flagVarEncontrado = false;
		String atual = "";
		
		for(; count < tokens.size(); count++){
			atual = tokens.get(count).getNome();
			if(atual.equals("procedure")){
				analiseDeEscopo(tokens, pilhaVar, count, ++nivel);
			} else if(atual.equals("var")){
				flagVarEncontrado = true;
			} else if(flagVarEncontrado = true) {
				pilhaVar.add(new Variavel(tokens.get(count).getNome(), tokens.get(count+2).getNome()));
				count+=2;
			} else if(atual.equals("begin")){
				flagVarEncontrado = false;
			} else if(tokens.get(count).getSimbolo().equals("identificador") && !flagVarEncontrado){
				buscaEmPilha(pilhaVar, atual, nivel);
				//TODO: CRIAR ESTRATÉGIA PARA ANÁLISE DE TIPOS (E OPERAÇÕES)
			}
			
			//TODO: PENSAR EM CASOS ONDE PROCEDURES TEM PARÂMETROS
			//TODO: CHAMADAS DE PROCEDURES DEVEM SER ANALISADAS NOVAMENTE?
			
			
		}
		
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
		ArrayList<Variavel> pilhaVar = new ArrayList<Variavel>();
		
		String path = args[0];
	
		listaTokens = obterTokens(path);
		analiseDeEscopo(listaTokens, pilhaVar, 0, 0);
		
		
		
	}
}
