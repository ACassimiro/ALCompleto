package analiseLexica;

import java.lang.String;
import java.io.*;

%% 

%standalone
%class Main
%line 


%{
 String token = ""; 
 String ErrorLog = "";
 String estadoAtual = "";
 String temp = "";
 int flagComent = 0;
 int initComent = 0;
 
%}

%eof{ 
	if(flagComent == 1) {
		ErrorLog += "Erro na linha " + initComent + ": Comentario de bloco nao fechado \n";
	}

	try{
	    FileWriter out = new FileWriter("src/analiseLexica/output.txt");
	
	    out.write("Numeros de linhas = " + (yyline+1) + "\n");
		out.write("\n" + token);
		out.write("\n" + ErrorLog);
	
	    out.close();
	} catch (Exception e){
	    // e.printStackTrace();
	    System.out.println("Nao foi possivel operar no arquivo de output\n");
	}

	System.out.println("Numeros de linhas = " + (yyline+1));
	System.out.println("\n" + token);
	System.out.println("\n" + ErrorLog);
%eof}

FimDeLinha = \r|\n|\r\n
Caracteres = a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|x|z
Numeros = 0|1|2|3|4|5|6|7|8|9
Operadores = [+|-|/|*|<|>]|"<="|">="|":="
Ident = {Caracteres}+({Numeros}|{Caracteres}|_)*
EspacoEmBranco = {FimDeLinha} | [ \t\f]

%state PAL_RESERVADA, N_1, N_2, N_3, ERR, VAR, COM_LINHA, COM_BLOCO

%%

<YYINITIAL>{
	"program" { 
			token += "Token (Linha " + String.valueOf(yyline+1) + "): {" + yytext() + " - Palavra Reservada}\n";
			yybegin(PAL_RESERVADA); }
	"var" { 
			token += "Token (Linha " + String.valueOf(yyline+1) + "): {" + yytext() + " - Palavra Reservada}\n";
			yybegin(PAL_RESERVADA); }
	"integer" { 
			token += "Token (Linha " + String.valueOf(yyline+1) + "): {" + yytext() + " - Palavra Reservada}\n";
			yybegin(PAL_RESERVADA); }
	"if" { 
			token += "Token (Linha " + String.valueOf(yyline+1) + "): {" + yytext() + " - Palavra Reservada}\n";
			yybegin(PAL_RESERVADA); }
	"then" { 
			token += "Token (Linha " + String.valueOf(yyline+1) + "): {" + yytext() + " - Palavra Reservada}\n";
			yybegin(PAL_RESERVADA); }
	"else" { 
			token += "Token (Linha " + String.valueOf(yyline+1) + "): {" + yytext() + " - Palavra Reservada}\n";
			yybegin(PAL_RESERVADA); }
	"end" { 
			token += "Token (Linha " + String.valueOf(yyline+1) + "): {" + yytext() + " - Palavra Reservada}\n";
			yybegin(PAL_RESERVADA); }
	"real" { 
			token += "Token (Linha " + String.valueOf(yyline+1) + "): {" + yytext() + " - Palavra Reservada}\n";
			yybegin(PAL_RESERVADA); }
	"while" { 
			token += "Token (Linha " + String.valueOf(yyline+1) + "): {" + yytext() + " - Palavra Reservada}\n";
			yybegin(PAL_RESERVADA); }
	"not" { 
			token += "Token (Linha " + String.valueOf(yyline+1) + "): {" + yytext() + " - Palavra Reservada}\n";
			yybegin(PAL_RESERVADA); }
	"do" { 
			token += "Token (Linha " + String.valueOf(yyline+1) + "): {" + yytext() + " - Palavra Reservada}\n";
			yybegin(PAL_RESERVADA); }
	"procedure" { 
			token += "Token (Linha " + String.valueOf(yyline+1) + "): {" + yytext() + " - Palavra Reservada}\n";
			yybegin(PAL_RESERVADA); }
	"boolean" { 
			token += "Token (Linha " + String.valueOf(yyline+1) + "): {" + yytext() + " - Palavra Reservada}\n";
			yybegin(PAL_RESERVADA); }
	"begin" { 
			token += "Token (Linha " + String.valueOf(yyline+1) + "): {" + yytext() + " - Palavra Reservada}\n";
			yybegin(PAL_RESERVADA); }
	
	{Ident} { 
			token += "Token (Linha " + String.valueOf(yyline+1) + "): {" + yytext() + " - Identificador}\n";
			yybegin(VAR);
		}
	
	{Numeros} { temp = yytext(); yybegin(N_1); }
	
	"//" { yybegin(COM_LINHA); }
	
	"{" { estadoAtual = "YYINITIAL"; initComent = yyline + 1; flagComent = 1; yybegin(COM_BLOCO); }
	
	{Operadores} { token += "Token (Linha " + String.valueOf(yyline+1) + "): {" + yytext() + " - Delimitador}\n";	}

	[.|;|:|,|(|)] { 
			token += "Token (Linha " + String.valueOf(yyline+1) + "): {" + yytext() + " - Delimitador}\n";
			//ErrorLog += "Erro na linha " + String.valueOf(yyline+1) +": Ponto avulso\n";
			//yybegin(ERR); 
		}

	{EspacoEmBranco} { }

	[^] {
			ErrorLog += "Erro na linha " + String.valueOf(yyline+1) + ": Caractere '" + yytext() + "' invalido\n"; 
		}	
	
}


<PAL_RESERVADA>{
	[.|;|:|,|(|)] { 
			token += "Token (Linha " + String.valueOf(yyline+1) + "): {" + yytext() + " - Delimitador}\n";
			yybegin(YYINITIAL);
		}
		
	{EspacoEmBranco} { 
			yybegin(YYINITIAL);
		}
		
	[^] {
			ErrorLog += "Erro na linha " + String.valueOf(yyline+1) +": Caractere desconhecido\n";
		}
}

<VAR> {
	[.|;|:|,|(|)] { 
			token += "Token (Linha " + String.valueOf(yyline+1) + "): {" + yytext() + " - Delimitador}\n";
			yybegin(YYINITIAL);
			//ErrorLog += "Erro na linha " + String.valueOf(yyline+1) +": Identificadores nao podem conter delimitadores \n";
			//yybegin(ERR); 
		}
		
	{Operadores} { 
			token += "Token (Linha " + String.valueOf(yyline+1) + "): {" + yytext() + " - Operador}\n";
			yybegin(YYINITIAL);
		}
		
	{EspacoEmBranco} { 
			yybegin(YYINITIAL);
		}
		
	[^] {
			ErrorLog += "Erro na linha " + String.valueOf(yyline+1) +": Caractere desconhecido\n";
		}
}

<N_1> {
	{Operadores} {
			token += "Token (Linha " + String.valueOf(yyline+1) + "): {" + temp + " - Numero Inteiro}\n";
			token += "Token (Linha " + String.valueOf(yyline+1) + "): {" + yytext() + " - Operador}\n";
			yybegin(YYINITIAL);
		}
	
	[.] { 
			yybegin(N_2);
		}
		
	[;|:|,|(|)] {
			token += "Token (Linha " + String.valueOf(yyline+1) + "): {" + temp + " - Numero Inteiro}\n";
			token += "Token (Linha " + String.valueOf(yyline+1) + "): {" + yytext() + " - Delimitador}\n";
			yybegin(YYINITIAL);
		}
	
	{EspacoEmBranco} {
			yybegin(YYINITIAL);
		}
	
	[^] {
			ErrorLog += "Erro na linha " + String.valueOf(yyline+1) + ": Identificadores nao podem comecar com numeros \n";
			yybegin(ERR);
		}
}

<N_2> {
	[.|;|:|,|(|)] {
			token += "Token (Linha " + String.valueOf(yyline+1) + "): {" + yytext() + " - Delimitador}\n";
			yybegin(YYINITIAL);
		}
		
	{Operadores} {
			ErrorLog += "Erro na linha " + String.valueOf(yyline+1) + ": Delimitadores em um numero tipo float incompleto \n";
			token += "Token (Linha " + String.valueOf(yyline+1) + "): {" + yytext() + " - Operador}\n";
			yybegin(YYINITIAL);
		}
		
	{Numeros} {
			token += "Token (Linha " + String.valueOf(yyline+1) + "): {" + temp + "." + yytext() + " - Numero Inteiro}\n";
			yybegin(YYINITIAL);
		}
		
	[^] {
			ErrorLog += "Erro na linha " + String.valueOf(yyline+1) + ": Identificadores nao podem começar com numeros ou conter pontos\n";
			yybegin(ERR);
		}
}




<ERR> {
	{EspacoEmBranco} { 
			yybegin(YYINITIAL);
		}
		
	[^] { }
}

<COM_LINHA> {
	[^] { }
	
	[\n] {
			yybegin(YYINITIAL);
		}
}

<COM_BLOCO> {
	"}" { flagComent = 0; yybegin(YYINITIAL); }
	
	[^] { }
}