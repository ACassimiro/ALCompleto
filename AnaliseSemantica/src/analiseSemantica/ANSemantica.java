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
	
	
	private static int analiseDeParametros(ArrayList<Token> tokens, ArrayList<Variavel> pilhaVar, String nome, int count, int nivel){
		ArrayList<String> listOp = new ArrayList<String>();
		ArrayList<String> pilhaTipos = new ArrayList<String>();
		ArrayList<String> tiposEsperados = new ArrayList<String>();
		String atual = "";
		String tipoAtual = "";
		String auxTipoAtual = "";
		boolean flagErro = false;
		
		int i = 0;
		listOp.add("="); listOp.add("<"); listOp.add(">"); listOp.add("<="); listOp.add(">="); listOp.add("<>"); //OP_RELACIONAIS
		listOp.add("+"); listOp.add("-"); //OP_ADITIVOS
		listOp.add("*"); listOp.add("/"); //OP_MULTIPLICATIVOS
		listOp.add("and"); listOp.add("or");
		
		
		
		for(i = pilhaVar.size() - 1; i < 0; i--){
			if(pilhaVar.get(i).getNome().equals(nome)){
				break;
			}
		}
		
		while(!pilhaVar.get(++i).getNome().equals("-")){
			pilhaTipos.add(pilhaVar.get(i).getTipo());
		}
		
		i = 0;
		atual = tokens.get(++count).getNome();
		while(!atual.equals(")")){
			
			if(atual.equals(",")){
				if(!pilhaTipos.get(0).equals("real") && tipoAtual.equals("integer") && auxTipoAtual.equals("integer")){
					System.out.println("Erro na paremetrizacao: Operacao aritmetica nao permitida na linha " + tokens.get(count).getLinha());
					flagErro = true;
					break;
				} 

				pilhaTipos.remove(0);
				atual = tokens.get(++count).getNome();
				continue;

			}
			
			
			if(tokens.get(count).getSimbolo().equals("identificador")){
				if(!buscaEmPilha(pilhaVar, atual, nivel)){
					System.out.println("Variavel " + atual + " nao declarada no escopo na linha " + tokens.get(count).getLinha());
				} else if (checkVarInicializada(pilhaVar, atual)) {
					System.out.println("Variavel " + atual + " nao inicializada no escopo na linha " + tokens.get(count).getLinha());					
				}
				
				auxTipoAtual = getVarTipo(pilhaVar, atual);
				
			} else if (atual.equals("num_int")){
				auxTipoAtual = "integer";
			} else if (atual.equals("num_real")){
				auxTipoAtual = "realr";
			} else if (atual.equals("boolean")) {
				auxTipoAtual = "boolean";
			} else if (listOp.contains(atual)){
				if((atual.equals("or") || atual.endsWith("and")) && !tipoAtual.equals("boolean")){
					System.out.println("Erro na paremetrizacao: Tipo nao booleano em operacao logica na linha " + tokens.get(count).getLinha());
					flagErro = true;
					break;
				} else if((!atual.equals("or") && !atual.endsWith("and")) && tipoAtual.equals("boolean")) {
					System.out.println("Erro na paremetrizacao: Tipo booleano em operacao aritmetica na linha " + tokens.get(count).getLinha());
					flagErro = true;
					break;
				}
				
				atual = tokens.get(++count).getNome();
				continue;
			}
			
			if(tipoAtual.isEmpty()){
				tipoAtual = auxTipoAtual;
			} else if(tipoAtual.equals("integer") && auxTipoAtual.equals("real")){
				tipoAtual = auxTipoAtual;
			} 
			
			if(pilhaTipos.get(0).equals("boolean")){
				if(!tipoAtual.equals("boolean") || !auxTipoAtual.equals("boolean")){
					System.out.println("Erro na paremetrizacao: Tipo nao booleano em um parametro que espera booleano na linha " + tokens.get(count).getLinha());
					flagErro = true;
					break;
				}
			} else if(pilhaTipos.get(0).equals("integer")){
				if(tipoAtual.equals("real") || !auxTipoAtual.equals("integer")){
					System.out.println("Erro na paremetrizacao: Operacao aritmetica nao permitida na linha " + tokens.get(count).getLinha());
					flagErro = true;
					break;
				}
			} else if(pilhaTipos.get(0).equals("real")){
				if(tipoAtual.equals("real") || !auxTipoAtual.equals("integer")){
					System.out.println("Erro na paremetrizacao: Operacao aritmetica nao permitida na linha " + tokens.get(count).getLinha());
					flagErro = true;
					break;
				}
			}
						
			atual = tokens.get(++count).getNome();
		}
		
		if(!flagErro){
			if(tipoAtual.isEmpty()){
				tipoAtual = auxTipoAtual;
			} else if(tipoAtual.equals("integer") && auxTipoAtual.equals("real")){
				tipoAtual = auxTipoAtual;
			} 
			
			if(pilhaTipos.get(0).equals("boolean")){
				if(!tipoAtual.equals("boolean") || !auxTipoAtual.equals("boolean")){
					System.out.println("Erro na paremetrizacao: Tipo nao booleano em um parametro que espera booleano na linha " + tokens.get(count).getLinha());
					flagErro = true;
				}
			} else if(pilhaTipos.get(0).equals("integer")){
				if(tipoAtual.equals("real") || !auxTipoAtual.equals("integer")){
					System.out.println("Erro na paremetrizacao: Operacao aritmetica nao permitida na linha " + tokens.get(count).getLinha());
					flagErro = true;
				}
			} else if(pilhaTipos.get(0).equals("real")){
				if(tipoAtual.equals("real") || !auxTipoAtual.equals("integer")){
					System.out.println("Erro na paremetrizacao: Operacao aritmetica nao permitida na linha " + tokens.get(count).getLinha());
					flagErro = true;
				}
			} else if(!pilhaTipos.get(0).equals("real") && tipoAtual.equals("integer") && auxTipoAtual.equals("integer")){
				System.out.println("Erro na paremetrizacao: Operacao aritmetica nao permitida na linha " + tokens.get(count).getLinha());
				flagErro = true;
			} 
		}
		
		if(flagErro) {
			while(!atual.equals(")")){
				atual = tokens.get(++count).getNome();
			}
		}
		

		return count;
				
	}
	

	private static void analiseDeEscopo(ArrayList<Token> tokens, ArrayList<Variavel> pilhaVar, ArrayList<String> listaProc, int count, int nivel){
		boolean flagVarEncontrado = false;
		String atual = "";
		
		for(; count < tokens.size(); count++){
			atual = tokens.get(count).getNome();
			if(atual.equals("procedure")){
				//CASO: NOVO ESCOPO SENDO DECLARADO
				if(!buscaEmPilha(pilhaVar, tokens.get(++count).getNome(), nivel)){
					listaProc.add(tokens.get(count).getNome());		
					pilhaVar.add(new Variavel("$", "$"));
					pilhaVar.add(new Variavel(tokens.get(count).getNome(), "proc"));
				} else {
					System.out.println("Procedimento com identificador ja utilizado no mesmo escopo na linha " + tokens.get(count).getLinha());
				}
				analiseDeEscopo(tokens, (ArrayList<Variavel>) pilhaVar.clone(), (ArrayList<String>) listaProc.clone(), ++count, nivel + 1);
			
			} else if(atual.equals("var")){
				//CASO: VAR ENCONTRADO, BUSCAR OBTER VARI�VEIS
				flagVarEncontrado = true;
			
			} else if(flagVarEncontrado = true) {
				//CASO: VAR ENCONTRADO, OBTENDO VARI�VEIS
				if(buscaEmPilha(pilhaVar, tokens.get(count).getNome(), nivel)) {
					System.out.println("Variavel ja declarada neste escopo");
				} else {
					pilhaVar.add(new Variavel(tokens.get(count).getNome(), tokens.get(count+2).getNome()));
				}
				count+=2;
			
			} else if(atual.equals("begin")){
				//CASO: BEGIN ENCONTRADO, PARAR DE OBTER VARI�VEIS
				flagVarEncontrado = false;
				
			} else if(tokens.get(count).getSimbolo().equals("identificador") && !flagVarEncontrado && !listaProc.contains(atual)){
				//CASO: VARIAVEL ENCONTRADA NO PROGRAMA
				count = analiseDeLinha(tokens, pilhaVar, listaProc, count, nivel);
				
			} else if(atual.equals("(") && !tokens.get(count - 1).getNome().equals("if") && !tokens.get(count - 1).getNome().equals("if") && tokens.get(count - 1).getSimbolo().equals("identificador") && count >= 1){
				//CASO: CHAMADA DE PROCEDIMENTO
				if(buscaEmPilha(pilhaVar, tokens.get(count - 1).getNome(), nivel)){
					count = analiseDeParametros(tokens, pilhaVar, tokens.get(count - 1).getNome(), count, nivel);
					count++;	
				} else {
					System.out.println("Chamada de procedimento com identificador desconhecido na linha " + tokens.get(count++ - 1).getLinha());
					while(!atual.equals(")")){
						atual = tokens.get(count++).getNome();
					}
				}
				
			
			} else if(atual.equals("(") && tokens.get(count - 1).getSimbolo().equals("identificador") && tokens.get(count - 2).getNome().equals("procedure") && count >= 2){
				//CASO: DECLARE��O DE PROCEDIMENTO
				while(!atual.equals(")")){
					ArrayList<String> auxNomeParam = new ArrayList<String>();
					
					if(tokens.get(count).getSimbolo().equals("identificador")){
						auxNomeParam.add(tokens.get(count).getNome());
					} else if (atual.equals("integer") || atual.equals("boolean") || atual.equals("real")) {
						for(String nome : auxNomeParam){
							pilhaVar.add(new Variavel(nome, atual));
						}
					} else if(atual.equals(";")){
						auxNomeParam = new ArrayList<String>();
					}
					
					atual = tokens.get(++count).getNome();
				}

				pilhaVar.add(new Variavel("-", "-"));
				
				count++;
			
			} else if(atual.equals("if") || atual.equals("while")){
				//CASO: EXPRESSAO CONDICIONAL
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
        System.out.println("Arquivo aberto com sucesso");
        
        String str = "";
       
        while(input.hasNextLine()){
            str = input.nextLine();
        	if(str.equals("Tabela:")){
        		break;            	
            }
        }
        
        System.out.println("Processando lista de tokens");
        
        while(input.hasNextLine()){
            str = input.nextLine();
        	
            if(str == null || str.isEmpty()) { break; }
            
            listaTokens.add(new Token(str));
        }
        
        input.close();
        
        System.out.println("Tokens obtidos com sucesso");
        
        return listaTokens;
	}
	
	public static void main (String[] args){
		ArrayList<Token> listaTokens = new ArrayList<Token>();
		ArrayList<Variavel> pilhaVar = new ArrayList<Variavel>();
		ArrayList<String> listaProc = new ArrayList<String>();
		
		String path = args[0];

		System.out.println("OBTENDO ESTRUTURA DE TOKENS:");
		listaTokens = obterTokens(path);
		
		System.out.println("\nINICIANDO ANALISE SEMANTICA:");
		analiseDeEscopo(listaTokens, pilhaVar, listaProc, 3, 0);
		System.out.println("\nAnalise semantica encerrada.");
		
		
	}
}
