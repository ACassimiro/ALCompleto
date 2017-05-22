package analiseSemantica;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


public class ANSemantica {

	private static boolean buscaEmPilha(ArrayList<Variavel> pilhaVar, String nome, int nivel){
		int ultimoEscopoEncontrado = 0;
		int escopoAtual = 0;
		for(Variavel var : pilhaVar){
			if(var.getNome().equals("$")){
				escopoAtual++;
			}else if(nome.equals(var.getNome())){
				if(escopoAtual == ultimoEscopoEncontrado && escopoAtual == nivel){
					System.out.println("Variavel " + nome + " declarada mais de uma vez no mesmo escopo");
					return true;
				} else {
					ultimoEscopoEncontrado = escopoAtual;
				}
			}
		}		
		
		return false;
	}
	
	private static void setVarInicializada(ArrayList<Variavel> pilhaVar, String nome){
		int count = pilhaVar.size();
		
		while(count >= 0){
			if(pilhaVar.get(count).getNome().equals(nome)) {
				pilhaVar.get(count).setInicializada(true);
			}
		}
		
	}
	
	private static boolean checkVarInicializada(ArrayList<Variavel> pilhaVar, String nome){
		int count = pilhaVar.size();
		
		while(count >= 0){
			if(pilhaVar.get(count).getNome().equals(nome)) {
				if(pilhaVar.get(count).getInicializada())
					return true;
				else
					return false;
					
			}
		}
		
		return false;		
	}
	
	private static String getVarTipo(ArrayList<Variavel> pilhaVar, String nome){
int count = pilhaVar.size();
		
		while(count >= 0){
			if(pilhaVar.get(count).getNome().equals(nome)) {
				return pilhaVar.get(count).getTipo();					
			}
		}
		
		return "";
	}
	
	private static int analiseDeLinha(ArrayList<Token> tokens, ArrayList<Variavel> pilhaVar, ArrayList<String> listProc, int count, int nivel){
		ArrayList<String> listaOpRel = new ArrayList<String>();
		ArrayList<String> listaOpAdt = new ArrayList<String>();
		ArrayList<String> listaOpMult = new ArrayList<String>();
		ArrayList<String> listaOpLog = new ArrayList<String>();
		ArrayList<String> pilhaTipos = new ArrayList<String>();
		boolean flagErro = false;
		boolean flagSairLoop = false;

		String preVar, posVar, varAtual, tipoAtual;
		
		listaOpRel.add("="); listaOpRel.add("<"); listaOpRel.add(">"); listaOpRel.add("<="); listaOpRel.add(">="); listaOpRel.add("<>"); //OP_RELACIONAIS
		listaOpAdt.add("+"); listaOpAdt.add("-"); //OP_ADITIVOS
		listaOpMult.add("*"); listaOpMult.add("/"); //OP_MULTIPLICATIVOS
		listaOpLog.add("and"); listaOpLog.add("or");
		
		while(true){
			if(flagSairLoop){
				break;
			}
			
			preVar = tokens.get(count - 1).getNome();
			varAtual = tokens.get(count).getNome();
			posVar = tokens.get(count + 1).getNome();
					
			if(posVar.equals(";")) {
				count--;
				flagSairLoop = true;
			} 
			
			if(posVar.equals(":=")){
				pilhaTipos.add(getVarTipo(pilhaVar, varAtual));
				setVarInicializada(pilhaVar, varAtual);
				count+=2;
				continue;
			} else if (preVar.equals(":=")) {
				if(tokens.get(count).getSimbolo().equals("identificador")){
					tipoAtual = getVarTipo(pilhaVar, varAtual);
				} else {
					if(tokens.get(count).getSimbolo().equals("num_int")){
						tipoAtual = "integer";
					} else if (tokens.get(count).getSimbolo().equals("num_real")) {
						tipoAtual = "real";
					} else {
						tipoAtual = "boolean";
					}
				}
				
				if( (pilhaTipos.get(0).equals("boolean") && !tipoAtual.equals("boolean")) || (!pilhaTipos.get(0).equals("boolean") && tipoAtual.equals("boolean"))){
					System.out.println("Tipos incompativeis na atribuicao da linha " + tokens.get(count).getLinha());
					flagErro = true;
					break;
				}
				
				if( pilhaTipos.get(0).equals("real") && tipoAtual.equals("integer") && posVar.equals(";") ){
					System.out.println("Tipos incompativeis na atribuicao da linha " + tokens.get(count).getLinha());
					flagErro = true;
					break;
				}
				
				pilhaTipos.add(getVarTipo(pilhaVar, varAtual));
				
			} else {
				if(tokens.get(count).getSimbolo().equals("identificador")){
					tipoAtual = getVarTipo(pilhaVar, varAtual);
					
					if(!checkVarInicializada(pilhaVar, varAtual)){
						System.out.println("Variavel " + varAtual + " nao inicializada sendo utilizada na operacao da linha " + tokens.get(count).getLinha());
						flagErro = true;
						break;
					}
					
				} else {
					if(tokens.get(count).getSimbolo().equals("num_int")){
						tipoAtual = "integer";
					} else if (tokens.get(count).getSimbolo().equals("num_real")) {
						tipoAtual = "real";
					} else {
						tipoAtual = "boolean";
					}
				}
				
				if(listaOpLog.contains(preVar)){
					if(!tipoAtual.equals("boolean")){
						System.out.println("Variavel nao booleana " + varAtual + " na em uma operacao logica na linha " + tokens.get(count).getLinha());
						flagErro = true;
						break;
					} else {
						for(String tipo : pilhaTipos){
							if(!tipo.equals("boolean")){
								System.out.println("Operador logico incompativel na linha " + tokens.get(count).getLinha());
								flagErro = true;
								break;
							}
						}
					}
					
					pilhaTipos.add(getVarTipo(pilhaVar, varAtual));
					
				} else {
					if(tipoAtual.equals("boolean")){
						System.out.println("Variavel booleana " + varAtual + " em uma operacao aritmetica na linha " + tokens.get(count).getLinha());
						flagErro = true;
						break;
					} 
					
					pilhaTipos.add(getVarTipo(pilhaVar, varAtual));
					
					if(pilhaTipos.get(0).equals("integer")){
						if(tipoAtual.equals("integer")) {
							for(String tipo : pilhaTipos){
								if(tipo.equals("real")){
									System.out.println("Tipo incompativel da variavel " + varAtual + " em uma operacao aritmetica na linha " + tokens.get(count).getLinha());
									flagErro = true;
									break;
								}
							}
						}
					} else if (pilhaTipos.get(0).equals("real")){
						if(tipoAtual.equals("integer") && (posVar.equals(";") || !tokens.get(count + 2).getSimbolo().equals("num_int")) ) {
							System.out.println("Tipo incompativel da variavel " + varAtual + " em uma operacao aritmetica na linha " + tokens.get(count).getLinha());
							flagErro = true;
							break;
						}
					}
				}
			}
			
			count += 2;
		}	
		
		if(flagErro)
			while(!tokens.get(count++).equals(";")){ }
		
		return count;
	}

	private static void analiseDeEscopo(ArrayList<Token> tokens, ArrayList<Variavel> pilhaVar, ArrayList<String> listaProc, int count, int nivel){
		boolean flagVarEncontrado = false;
		String atual = "";
		
		for(; count < tokens.size(); count++){
			atual = tokens.get(count).getNome();
			if(atual.equals("procedure")){
				//CASO: NOVO ESCOPO SENDO DECLARADO
				listaProc.add(tokens.get(++count).getNome());
				analiseDeEscopo(tokens, (ArrayList<Variavel>) pilhaVar.clone(), (ArrayList<String>) listaProc.clone(), ++count, nivel + 1);
			
			} else if(atual.equals("var")){
				//CASO: VAR ENCONTRADO, BUSCAR OBTER VARIÁVEIS
				flagVarEncontrado = true;
			
			} else if(flagVarEncontrado = true) {
				//CASO: VAR ENCONTRADO, OBTENDO VARIÁVEIS
				if(buscaEmPilha(pilhaVar, tokens.get(count).getNome(), nivel)) {
					System.out.println("Variavel ja declarada neste escopo");
				} else {
					pilhaVar.add(new Variavel(tokens.get(count).getNome(), tokens.get(count+2).getNome()));
				}
				count+=2;
			
			} else if(atual.equals("begin")){
				//CASO: BEGIN ENCONTRADO, PARAR DE OBTER VARIÁVEIS
				flagVarEncontrado = false;
				
			} else if(tokens.get(count).getSimbolo().equals("identificador") && !flagVarEncontrado && !listaProc.contains(atual)){
				//CASO: VARIAVEL ENCONTRADA NO PROGRAMA
				count = analiseDeLinha(tokens, pilhaVar, listaProc, count, nivel);
				
			} else if(atual.equals("(") && tokens.get(count - 1).getNome().equals("procedure") && count >= 1){
				//CASO: CHAMADA DE PROCEDIMENTO
				count++;
				//TODO: REFAZER CONDIÇÃO E REALIZAR CHECAGEM DE TIPOS DE PARAMETROS
			
			} else if(atual.equals("(") && tokens.get(count - 1).getSimbolo().equals("identificador") && tokens.get(count - 2).getNome().equals("procedure") && count >= 2){
				//CASO: DECLAREÇÃO DE PROCEDIMENTO
				count++;
				//TODO: CHECAGEM DE TIPOS DE PARAMETROS
			
			}
			
			
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
		ArrayList<String> listaProc = new ArrayList<String>();
		
		String path = args[0];
	
		listaTokens = obterTokens(path);
		analiseDeEscopo(listaTokens, pilhaVar, listaProc, 3, 0);
		
		
		
	}
}
