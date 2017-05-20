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
	
	private static void erro(String msg){
		System.out.println(msg);
		System.exit(0);
	}
	
	private static int fator(ArrayList<Token> tokens, int count){
		if(tokens.get(count).getSimbolo().equals("identificador")){
			count++;
			if(tokens.get(count).getNome().equals("(")){					
				//count = lista_de_expressoes(tokens,count++);
				if(!tokens.get(count++).getNome().equals(")"))
					erro("Esperado token fecha parentese na linha " + tokens.get(--count).getLinha());
			}
		} else if (tokens.get(count).getSimbolo().equals("")){
			//TODO: ADICIONAR TODAS AS OPCOES DE FATORES
		}
		return count;
	}
	
	private static int termo(ArrayList<Token> tokens, int count){
		ArrayList<String> termoSim = new ArrayList<String>();
		ArrayList<String> termoNome = new ArrayList<String>();
		ArrayList<String> op_mult = new ArrayList<String>();
		
		termoSim.add("identificador"); termoSim.add("num_int"); termoSim.add("num_real");
		termoNome.add("true"); termoNome.add("false"); termoNome.add("("); termoNome.add("not");
		op_mult.add("*"); op_mult.add("/"); op_mult.add("and");
		
		String atual = "";
		
		while (true){
			if( ( termoSim.contains(tokens.get(count).getSimbolo()) || termoNome.contains(tokens.get(count).getNome()) ) && !atual.equals("fator") ){
				atual = "fator";
				count = fator(tokens, count);
			} else if ( ( termoSim.contains(tokens.get(count).getSimbolo()) || termoNome.contains(tokens.get(count).getNome()) ) && atual.equals("fator") ){
				erro("Dois fatores seguidos na linha " + tokens.get(count).getLinha());
			} else if(op_mult.contains(tokens.get(count).getNome())){
				atual = "op_mult";
				count++;
			}			
		
			break;
		}
		
		if(atual.equals("op_mult")){
			erro("Fator esperado na linha " + tokens.get(count).getLinha());
		}
		
		return count;
	}
	
	private static int expressao_simples(ArrayList<Token> tokens, int count){
		ArrayList<String> termoSim = new ArrayList<String>();
		ArrayList<String> termoNome = new ArrayList<String>();
		ArrayList<String> sinal = new ArrayList<String>();
		ArrayList<String> op_adt = new ArrayList<String>();
		String atual = "";
		
		//TODO: RESOLVER QUESTAO DO OP_ADITIVO NA EXPRESSAO_SIMPLES
		
		termoSim.add("identificador"); termoSim.add("num_int"); termoSim.add("num_real");
		termoNome.add("true"); termoNome.add("false"); termoNome.add("("); termoNome.add("not");
		sinal.add("+"); sinal.add("-");
		op_adt.add("+"); op_adt.add("-"); op_adt.add("or");
		
		while(true){
			if( termoSim.contains(tokens.get(count).getSimbolo()) || termoNome.contains(tokens.get(count).getNome())){
				count = termo(tokens, count);
				continue;
			} else if (sinal.contains(tokens.get(count).getNome())){
				count++;
				if( !termoSim.contains(tokens.get(count).getSimbolo()) || !termoNome.contains(tokens.get(count).getNome()))
					erro("Esperado fator depois de sinal na linha " + tokens.get(count).getLinha());
				
				count = termo(tokens, count);	
				continue;
			} else if (op_adt.contains(tokens.get(count).getNome())){
				
			} else {
				break;
			}
		}
		
		
		
		
		return count;
	}
	
	private static int expressao(ArrayList<Token> tokens, int count){
		
		count = expressao_simples(tokens, count);
		
		return count;
	}
	
	private static int comando(ArrayList<Token> tokens, int count) {
		String aux = "";
				
		if(tokens.get(count).getSimbolo().equals("identificador")){
			count++;
			aux = tokens.get(count++).getNome();
			if(aux.equals(":=")){
				count = expressao(tokens, count);
			} else if (aux.equals("(")){
				
			} else { 
				erro("Token nao esperado na linha " + tokens.get(--count).getLinha());
			}
		} else if (tokens.get(count).getNome().equals("if")){
			//TODO: FINALIZAR CONSTRUCAO DE COMANDO
		}
		
		return count;
	
	}
	
	private static int lista_de_comandos(ArrayList<Token> tokens, int count) {
		ArrayList<String> nomes = new ArrayList<String>();
		ArrayList<String> simbolos = new ArrayList<String>();
		
		nomes.add("if"); nomes.add("while"); nomes.add("begin");
		simbolos.add("identificador");
		
		while( nomes.contains(tokens.get(count).getNome()) || simbolos.contains(tokens.get(count).getSimbolo()) ){
			count = comando(tokens, count);
			
			if(tokens.get(count).getNome().equals(";")){
				count++;
			} else {
				break;
			}
		} 
		
		return count;
	}
	
	
	private static int comandos_opcionais(ArrayList<Token> tokens, int count) {
		ArrayList<String> nomes = new ArrayList<String>();
		ArrayList<String> simbolos = new ArrayList<String>();
		
		nomes.add("if"); nomes.add("while"); nomes.add("begin");
		simbolos.add("identificador");
		
		if( nomes.contains(tokens.get(count).getNome()) || simbolos.contains(tokens.get(count).getSimbolo()) ){
			count = lista_de_comandos(tokens, count);
		} 
		
		return count;
		
	}
	
	
	
	private static int comando_composto(ArrayList<Token> tokens, int count) {
		
		if(tokens.get(count++).getNome().equals("begin")){
			count = comandos_opcionais(tokens, count);
			
			if(!tokens.get(count++).getNome().equals("end"))
				erro("Esperado end no procedimento na linha " + tokens.get(--count).getLinha());
		} else {
			erro("Esperado begin no procedimento na linha " + tokens.get(--count).getLinha());
		}
		
				
		return count;
	
	}
	
	
	private static int lista_de_parametros(ArrayList<Token> tokens, int count){
		String atual = "";
		
		
		while(true){
			
			if(!tokens.get(count).getSimbolo().equals("identificador") && atual.equals(";")){
				erro("Identificador esperado depois do ';' na linha" + tokens.get(count).getLinha());
			} 
						
			if(tokens.get(count++).getSimbolo().equals("identificador") && (atual.isEmpty() || !atual.equals("identificador"))){
				atual = "identificador";
				continue;
			} else {
				erro("Token nao esperado na linha " + tokens.get(--count).getLinha());
			}
			
			if(tokens.get(count++).getNome().equals(",") && atual.equals("identificador")){
				atual = ",";
				continue;
			} else {
				erro("Token nao esperado na linha " + tokens.get(--count).getLinha());
			}
			
			if(tokens.get(count++).getNome().equals(":") && atual.equals("identificador")){
				atual = ":";
				continue;
			} else {
				erro("Token nao esperado antes de ':' na linha " + tokens.get(--count).getLinha());
			}
			
			if((tokens.get(count++).getNome().equals("integer") || tokens.get(count++).getNome().equals("real") || tokens.get(count++).getNome().equals("boolean")) && atual.equals("identificador")){
				atual = ":";
				continue;
			} else {
				erro("Token nao esperado antes de ':' na linha " + tokens.get(--count).getLinha());
			}
			
			
			if(tokens.get(count).getNome().equals(";") && atual.equals("identificador")){
				atual = ";";
				count++;
				continue;
			} else {
				break;
			}
		}
		
		return count;
	
	}
	
	private static int decl_de_subprog(ArrayList<Token> tokens, int count){
		
		
		while(true) {
			if(!tokens.get(count).getNome().equals("procedure")){
				break;
			}		
			
			count++;
			
			if(tokens.get(count++).getSimbolo().equals("identificador")){
				if(tokens.get(count++).getNome().equals("(")){
					count = lista_de_parametros(tokens, count);
					if(!tokens.get(count++).getNome().equals(")"))
						erro("Fechamento de parentese esperado na linha " + tokens.get(--count).getLinha());
				} 
				
				if(!tokens.get(count++).getNome().equals(";")){
					erro("Esperado token ';' no fim da declaracao de procedimento na linha " + tokens.get(--count).getLinha());
				}
					
			} else {
				erro("Identificador esperado na linha " + tokens.get(--count).getLinha());
			}
		}
		
		count = dec_variaveis(tokens, count);
		count = decl_de_subprog(tokens, count);
		count = comando_composto(tokens, count);
				
		return count;
		
	}
	
	
	private static int lista_decl_variaveis(ArrayList<Token> tokens, int count){
		String atual;
		
		while(true){
			atual = "";
			
			if(atual.equals("") || !tokens.get(count).getSimbolo().equals("identificador")){
				break;
			}
			
			if (tokens.get(count++).getSimbolo().equals("identificador") && (atual.isEmpty() || !atual.equals("identificador")) ){
				atual = "identificador";
				continue;
			} else {
				erro("Token posicionado incorretamente na linha " + tokens.get(--count).getLinha());
			}
			
			if (tokens.get(count++).getNome().equals(",")  && !atual.equals(",") && !atual.isEmpty()){
				atual = ",";
				continue;
			}else{
				erro("Token posicionado incorretamente na linha " + tokens.get(--count).getLinha());
			}
			
			if(tokens.get(count++).getNome().equals(":") && atual.equals("identificador")){
				if(tokens.get(count++).getNome().equals("real") || tokens.get(count++).getNome().equals("boolean") || tokens.get(count++).getNome().equals("integer")){
					if(tokens.get(count++).getNome().equals(";")){
						continue;
					} else {
						erro("Delimitador ; esperado na linha" + tokens.get(--count).getLinha());
					}
				} else {
					erro("Palavra reservada indicadora de tipo esperada na linha " + tokens.get(--count).getLinha());
				}
			} else {
				erro("Token posicionado incorretamente na linha " + tokens.get(--count).getLinha());
			}
				

			break;
		}
		
		return count;
	}
	
	private static int dec_variaveis(ArrayList<Token> tokens, int count){
		
		if(tokens.get(count).getNome().equals("var")){
			count++;
			
			lista_decl_variaveis(tokens, count);
		}
		
		return count;
	}
	
	private static void inicioPrograma(ArrayList<Token> tokens, int count){
		 
		if(!tokens.get(count++).getNome().equals("program"))
			erro("Programa nao iniciado com program - linha " + tokens.get(--count).getLinha());
		
		if(!tokens.get(count++).getSimbolo().equals("identificador")) 
			erro("program nao seguido por identificador - linha " + tokens.get(--count).getLinha());
			
		if(tokens.get(count++).getNome().equals(";"))
			erro("Programa não iniciado com program - linha " + tokens.get(--count).getLinha());
		
		count = dec_variaveis(tokens, count);
			 
		//TODO: INSERIR DECLARAÇÕES_DE_SUBPROGRAMAS 
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
		
		inicioPrograma(listaTokens, 0);
		
		
	}
}
