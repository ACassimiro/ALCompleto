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
	
	private static int lista_de_expressoes(ArrayList<Token> tokens, int count){
		ArrayList<String> termoSim = new ArrayList<String>();
		ArrayList<String> termoNome = new ArrayList<String>();
		ArrayList<String> sinal = new ArrayList<String>();
		ArrayList<String> op_adt = new ArrayList<String>();
		String atual = "";
				
		termoSim.add("identificador"); termoSim.add("num_int"); termoSim.add("num_real");
		termoNome.add("true"); termoNome.add("false"); termoNome.add("("); termoNome.add("not"); termoNome.add(",");
		sinal.add("+"); sinal.add("-");
		op_adt.add("+"); op_adt.add("-"); op_adt.add("or"); //op_adt.add("->");
		
		while (true) {
			
			if( termoSim.contains(tokens.get(count).getSimbolo()) || termoNome.contains(tokens.get(count).getNome()) || sinal.contains(tokens.get(count).getNome())) {
				if((atual.isEmpty() || atual.equals(",")) && !tokens.get(count).getNome().equals(",")){
					count = expressao_simples(tokens, count);
					atual = "expressao";
				} else if(!atual.equals("expressao") && tokens.get(count).getNome().equals(",")) {
					erro("Expressao esperada na linha " + tokens.get(count));
				} else if(atual.equals("expressao") && !tokens.get(count).getNome().equals(",")) {
					erro("Token de separacao esperado na linha " + tokens.get(count));
				} else if(atual.equals("expressao") && tokens.get(count).getNome().equals(",")) {
					count++;
					atual = ",";
				} 
			} 
			
			break;
		}
		
		
		return count;
	}
	
	private static int fator(ArrayList<Token> tokens, int count){
		if(tokens.get(count).getSimbolo().equals("identificador")){
			count++;
			if(tokens.get(count).getNome().equals("(")){					
				count = lista_de_expressoes(tokens,count++);
				if(!tokens.get(count++).getNome().equals(")"))
					erro("Esperado token fecha parentese na linha " + tokens.get(--count).getLinha());
			}
		} else if (tokens.get(count).getSimbolo().equals("num_int") || tokens.get(count).getSimbolo().equals("num_real")){
			count++;
		} else if ( tokens.get(count).getNome().equals("true") || tokens.get(count).getNome().equals("false") ){
			count++;			
		} else if ( tokens.get(count).getNome().equals("true") || tokens.get(count).getNome().equals("false") ){
			count++;			
		} else if ( tokens.get(count).getNome().equals("not") ) {
			count = fator(tokens, ++count);
		} else if ( tokens.get(count).getNome().equals("(") ) {
			count = expressao(tokens, ++count);
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
		// System.out.println("Chamando expressao_simples");
		
		ArrayList<String> termoSim = new ArrayList<String>();
		ArrayList<String> termoNome = new ArrayList<String>();
		ArrayList<String> sinal = new ArrayList<String>();
		ArrayList<String> op_adt = new ArrayList<String>();
		String atual = "";
		
		termoSim.add("identificador"); termoSim.add("num_int"); termoSim.add("num_real");
		termoNome.add("true"); termoNome.add("false"); termoNome.add("("); termoNome.add("not");
		sinal.add("+"); sinal.add("-");
		op_adt.add("+"); op_adt.add("-"); op_adt.add("or");
		
		while(true){
			if(atual.equals("termo") && termoSim.contains(tokens.get(count).getSimbolo()) || termoNome.contains(tokens.get(count).getNome())){
				erro("Termo seguido de termo na linha " + tokens.get(count).getLinha());
			} else if(atual.equals("op_adt") && op_adt.contains(tokens.get(count).getNome()) ){
				erro("Operador aditivo seguido de operador aditivo na linha " + tokens.get(count).getLinha());
			}
						
			if( termoSim.contains(tokens.get(count).getSimbolo()) || termoNome.contains(tokens.get(count).getNome()) && (atual.isEmpty() || atual.equals("op_adt")) ){
				count = termo(tokens, count);
				atual = "termo";
				continue;
			} 
			
			if (sinal.contains(tokens.get(count).getNome())){
				count++;
				if( termoSim.contains(tokens.get(count).getSimbolo()) || termoNome.contains(tokens.get(count).getNome())){
					;
				} else {
					erro("Esperado fator depois de sinal na linha " + tokens.get(count).getLinha());
				}
				
				count = termo(tokens, count);	
				atual = "termo";
				continue;
			} 
			
			if (op_adt.contains(tokens.get(count).getNome())){
				count++;
				if( !termoSim.contains(tokens.get(count).getSimbolo()) || !termoNome.contains(tokens.get(count).getNome()))
					erro("Esperado fator depois de sinal na linha " + tokens.get(count).getLinha());
				
				count = termo(tokens, count);
				atual = "op_adt";
				continue;
			}
			
			break;
			
		}
		
		return count;
	}
	
	private static int expressao(ArrayList<Token> tokens, int count){
		// System.out.println("Chamando expressao");
		String atual = "";
		ArrayList<String> op_rel = new ArrayList<String>();
		op_rel.add("="); op_rel.add("<"); op_rel.add(">"); op_rel.add("<="); op_rel.add(">="); op_rel.add("<>");      
		
		op_rel.add("+"); op_rel.add("-"); //OP_ADITIVOS
		op_rel.add("*"); op_rel.add("/"); //OP_MULTIPLICATIVOS
		op_rel.add("and"); op_rel.add("or"); op_rel.add("->");
		
		//TODO: MOSTRAR NOVO OPERADOR
		
		int auxCount;

		
		while(true){
			

			auxCount = count;
			
			if(tokens.get(count).getSimbolo().equals("pal_res")){
				break;
			}			
			

			if(op_rel.contains(tokens.get(count).getNome()) && (atual.equals("op_rel") && atual.isEmpty())){
				erro("Token nao esperado na linha " + tokens.get(count).getLinha());
			} else if (op_rel.contains(tokens.get(count).getNome()) && !atual.equals("expressao")) {
				erro("Token nao esperado na linha " + tokens.get(count).getLinha());
			} else if (tokens.get(count).getNome().equals(";") && !atual.equals("expressao")) {
				erro("Token nao esperado na linha " + tokens.get(count).getLinha());
			}
			
			if(op_rel.contains(tokens.get(count).getNome()) && (!atual.equals("op_rel") && !atual.isEmpty())){
				atual = "op_rel";
				count++;
			} else {
				atual = "expressao";
				count = expressao_simples(tokens, ++count);	
			}
			
			if(auxCount == count)
				break;
		}
		
		return count;
	}
	
	private static int parte_else(ArrayList<Token> tokens, int count) {
		
		if(tokens.get(count).getNome().equals("else")){
			count = comando(tokens, ++count);
		}
		
		return count;
	}
	
	private static int comando(ArrayList<Token> tokens, int count) {
		// System.out.println("Chamando comando");
		String aux = "";
	
		if(tokens.get(count).getSimbolo().equals("identificador")){
			count++;
			aux = tokens.get(count++).getNome();
			if(aux.equals(":=")){
				count = expressao(tokens, count);
			} else if (aux.equals("(")){
				count = lista_de_expressoes(tokens,count);
				
				if(!tokens.get(count++).getNome().equals(")"))
					erro("Fecha parentese esperado na linha " + tokens.get(--count).getLinha());
			} else { 
				erro("Token nao esperado na linha " + tokens.get(--count).getLinha());
			}
		} else if (tokens.get(count).getNome().equals("if")){
			count++;
			count = expressao(tokens,count);
			
			if (!tokens.get(count++).getNome().equals("then")){
				erro("Token then esperado na linha " + tokens.get(--count).getLinha());
			}
			
			count = comando(tokens, count);
			count = parte_else(tokens, count);			
		} else if(tokens.get(count).getNome().equals("while")) {
			count = expressao(tokens, ++count);
			
			if(!tokens.get(count++).getNome().equals("do"))
				erro("Token 'do' esperado na linha " + tokens.get(--count).getLinha());
			
			count = comando(tokens, count);
			
		} else {
			erro("Expressao de comando esperada na linha " + tokens.get(count).getLinha());
		}
		
		return count;
	
	}
	
	private static int lista_de_comandos(ArrayList<Token> tokens, int count) {
		// System.out.println("Chamando lista_de_comandos");
		
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
		// System.out.println("Chamando comandos_opcionais");
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
		// System.out.println("Chamando comando_composto");
		
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
		// System.out.println("Chamando lista_de_parametros");
		String atual = "";
		
		
		while(true){
			
			if(tokens.get(count).getNome().equals(")")){
				break;
			}
			
			if(!tokens.get(count).getSimbolo().equals("tipo") && atual.equals(";")){
				erro("Identificador esperado depois do ';' na linha " + tokens.get(count).getLinha());
			} else if(tokens.get(count).getNome().equals(";") && atual.equals("tipo")){
				atual = ";";
				count++;
				continue;
			} 
			
			if(tokens.get(count).getSimbolo().equals("identificador") && (atual.isEmpty() || !atual.equals("identificador"))){
				count++;
				atual = "identificador";
				continue;
			} else if(tokens.get(count).getNome().equals(",") && atual.equals("identificador")){
				count++;
				atual = ",";
				continue;
			} else if(tokens.get(count).getNome().equals(":") && atual.equals("identificador")){
				count++;
				atual = ":";
				continue;
			} else if((tokens.get(count).getNome().equals("integer") || tokens.get(count).getNome().equals("real") || tokens.get(count).getNome().equals("boolean")) && atual.equals(":")){
				count++;
				atual = "tipo";
				continue;
			} else {
				erro("Token nao esperado na linha " + tokens.get(--count).getLinha());
			}
			
		}
		
		return count;
	
	}
	
	private static int decl_de_subprog(ArrayList<Token> tokens, int count){
		// System.out.println("Chamando decl_de_subprog");
		
		if(!tokens.get(count).getNome().equals("procedure")){
			return count;
		}	
		
		while(true) {			
			if(!tokens.get(count).getNome().equals("procedure")){
				break;
			}		
			
			count++;
			if(tokens.get(count++).getSimbolo().equals("identificador")){
				if(tokens.get(count).getNome().equals("(")){
					count++;
					count = lista_de_parametros(tokens, count);
					if(!tokens.get(count++).getNome().equals(")"))
						erro("Fechamento de parentese esperado na linha  " + tokens.get(--count).getLinha());
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
		
		if(tokens.get(count).getNome().equals("procedure")){
			count = decl_de_subprog(tokens, count);
		}
		
		return count;
		
	}
	
	
	private static int lista_decl_variaveis(ArrayList<Token> tokens, int count){
		String atual;

		atual = "";
		
		while(true){
			if(tokens.get(count).getNome().equals("procedure")){
				break;
			}
				
			if((atual.equals("") || atual.equals(";")) && !tokens.get(count).getSimbolo().equals("identificador")){
				break;
			}
			
			if (tokens.get(count).getSimbolo().equals("identificador") && (atual.isEmpty() || !atual.equals("identificador")) ){
				count++;
				atual = "identificador";
				continue;
			} else if(tokens.get(count).getSimbolo().equals("identificador") && atual.equals("identificador")) {
				erro("Token posicionado incorretamente na linha " + tokens.get(count).getLinha());
			}
			
			if (tokens.get(count).getNome().equals(",")  && !atual.equals(",") && !atual.isEmpty()){
				count++;
				atual = ",";
				continue;
			}else if (tokens.get(count).getNome().equals(",")  && (atual.equals(",") || atual.isEmpty())){
				erro("Token posicionado incorretamente na linha " + tokens.get(count).getLinha());
			}
			
			if(tokens.get(count).getNome().equals(":") && atual.equals("identificador")){
				count++;
				if(tokens.get(count).getNome().equals("real") || tokens.get(count).getNome().equals("boolean") || tokens.get(count).getNome().equals("integer")){
					count++;
					if(tokens.get(count++).getNome().equals(";")){
						atual = ";";
						continue;
					} else {
						erro("Delimitador ; esperado na linha " + tokens.get(count).getLinha());
					}
				} else {
					erro("Palavra reservada indicadora de tipo esperada na linha " + tokens.get(count).getLinha());
				}
			} else if (tokens.get(count++).getNome().equals(":") && !atual.equals("identificador")){
				erro("Token posicionado incorretamente na linha " + tokens.get(--count).getLinha());
			}
				

			break;
		}
				
		return count;
	}
	
	private static int dec_variaveis(ArrayList<Token> tokens, int count){
		
		if(tokens.get(count).getNome().equals("var")){
			count++;
			count = lista_decl_variaveis(tokens, count);
		}
		
		return count;
	}
	
	private static void inicioPrograma(ArrayList<Token> tokens, int count){
		// System.out.println("Chamando Inicio do programa"); 
		if(!tokens.get(count++).getNome().equals("program"))
			erro("Programa nao iniciado com program - linha " + tokens.get(--count).getLinha());
		
		if(!tokens.get(count++).getSimbolo().equals("identificador")) 
			erro("program nao seguido por identificador - linha " + tokens.get(--count).getLinha());
			
		if(!tokens.get(count++).getNome().equals(";"))
			erro("Identificador não seguido por ; - linha " + tokens.get(--count).getLinha());
		
		count = dec_variaveis(tokens, count);
		count = decl_de_subprog(tokens, count);
		count = comando_composto(tokens, count);

		if(!tokens.get(count++).getNome().equals(".")){
			erro("Token '.' esperado na linha " + tokens.get(--count).getLinha());
		}
		
	}

	private static ArrayList<Token> obterTokens (String path){
		Scanner input = null;
		ArrayList<Token> listaTokens = new ArrayList<Token>();
		

		System.out.println("Criando Scanner de leitura da tabela de tokens");
		
        try
        {
        	input = new Scanner(new File(path));
        }
        catch(FileNotFoundException e)
        {
            System.out.println("Erro: Arquivo nao encontrado");
            return null;
        }

        System.out.println("Iniciando leitura da tabela");
        
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

        System.out.println("Tokens obtidos com sucesso, encerrando Scanner");
        
        input.close();
                
        return listaTokens;
	}
	
	public static void main (String[] args){
		ArrayList<Token> listaTokens = new ArrayList<Token>();
		String path = args[0];
	
		System.out.println("OBTENDO TOKENS: ");
		listaTokens = obterTokens(path);
		
		System.out.println("\nREALIZANDO ANALISE SINTATICA:");

		inicioPrograma(listaTokens, 0);
				
		System.out.println("Analise realizada com sucesso. Nenhum erro foi detectado.");
		
	}
}
