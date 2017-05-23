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
	    FileWriter out = new FileWriter("src/files/output.txt");
	
	    out.write("linhas = " + (yyline+1) + "\n");
	    out.write("\nTabela:");
		out.write("\n" + token);
		out.write("\n" + ErrorLog);
		
	    out.close();
	} catch (Exception e){
	    // e.printStackTrace();
	    System.out.println("Nao foi possivel operar no arquivo de output\n");
	}

	System.out.println("Numeros de linhas = " + (yyline+1));
	System.out.println("\nTabela:");
	System.out.println(token);
	System.out.println("\n" + ErrorLog);
	
%eof}

FimDeLinha = \r|\n|\r\n
Caracteres = a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|x|z|y|w
Numeros = 0|1|2|3|4|5|6|7|8|9
Operadores = [+|-|/|*|<|>]|"<="|">="|":="
Ident = {Caracteres}+({Numeros}|{Caracteres}|_)*
EspacoEmBranco = {FimDeLinha} | [ \t\f]

%state PAL_RESERVADA, N_1, N_2, N_3, ERR, VAR, COM_LINHA, COM_BLOCO

%%

<YYINITIAL>{
	"program" { 
			token +=   String.valueOf(yyline+1) + " : " + yytext() + " - pal_res\n";
			yybegin(PAL_RESERVADA); }
	"var" { 
			token +=   String.valueOf(yyline+1) + " : " + yytext() + " - pal_res\n";
			yybegin(PAL_RESERVADA); }
	"integer" { 
			token +=   String.valueOf(yyline+1) + " : " + yytext() + " - pal_res\n";
			yybegin(PAL_RESERVADA); }
	"if" { 
			token +=   String.valueOf(yyline+1) + " : " + yytext() + " - pal_res\n";
			yybegin(PAL_RESERVADA); }
	"then" { 
			token +=   String.valueOf(yyline+1) + " : " + yytext() + " - pal_res\n";
			yybegin(PAL_RESERVADA); }
	"else" { 
			token +=   String.valueOf(yyline+1) + " : " + yytext() + " - pal_res\n";
			yybegin(PAL_RESERVADA); }
	"end" { 
			token +=   String.valueOf(yyline+1) + " : " + yytext() + " - pal_res\n";
			yybegin(PAL_RESERVADA); }
	"real" { 
			token +=   String.valueOf(yyline+1) + " : " + yytext() + " - pal_res\n";
			yybegin(PAL_RESERVADA); }
	"while" { 
			token +=   String.valueOf(yyline+1) + " : " + yytext() + " - pal_res\n";
			yybegin(PAL_RESERVADA); }
	"not" { 
			token +=   String.valueOf(yyline+1) + " : " + yytext() + " - pal_res\n";
			yybegin(PAL_RESERVADA); }
	"do" { 
			token +=   String.valueOf(yyline+1) + " : " + yytext() + " - pal_res\n";
			yybegin(PAL_RESERVADA); }
	"procedure" { 
			token +=   String.valueOf(yyline+1) + " : " + yytext() + " - pal_res\n";
			yybegin(PAL_RESERVADA); }
	"boolean" { 
			token +=   String.valueOf(yyline+1) + " : " + yytext() + " - pal_res\n";
			yybegin(PAL_RESERVADA); }
	"begin" { 
			token +=   String.valueOf(yyline+1) + " : " + yytext() + " - pal_res\n";
			yybegin(PAL_RESERVADA); }
	"true" { 
			token +=   String.valueOf(yyline+1) + " : " + yytext() + " - boolean\n";
			yybegin(PAL_RESERVADA); }
	"false" { 
			token +=   String.valueOf(yyline+1) + " : " + yytext() + " - boolean\n";
			yybegin(PAL_RESERVADA); }
	
	
	
	{Ident} { 
			token += String.valueOf(yyline+1) + " : " + yytext() + " - identificador\n";
			yybegin(VAR);
		}
	
	{Numeros}+ { temp = yytext(); yybegin(N_1); }
	
	"//" { yybegin(COM_LINHA); }
	
	"{" { estadoAtual = "YYINITIAL"; initComent = yyline + 1; flagComent = 1; yybegin(COM_BLOCO); }
	
	{Operadores} { token +=   String.valueOf(yyline+1) + " : " + yytext() + " - delimitador\n";	}

	[.|;|:|,|(|)] { 
			token +=   String.valueOf(yyline+1) + " : " + yytext() + " - delimitador\n";
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
			token +=   String.valueOf(yyline+1) + " : " + yytext() + " - delimitador\n";
			yybegin(YYINITIAL);
		}
		
	{EspacoEmBranco} { 
			yybegin(YYINITIAL);
		}
	
	{Operadores} { 
			token +=   String.valueOf(yyline+1) + " : " + yytext() + " - delimitador\n";	
			yybegin(YYINITIAL);
		}
	
		
	[^] {
			ErrorLog += "Erro na linha " + String.valueOf(yyline+1) +": Caractere desconhecido\n";
		}
}

<VAR> {
	[.|;|:|,|(|)] { 
			token +=   String.valueOf(yyline+1) + " : " + yytext() + " - delimitador\n";
			yybegin(YYINITIAL);
			//ErrorLog += "Erro na linha " + String.valueOf(yyline+1) +": identificadores nao podem conter delimitadores \n";
			//yybegin(ERR); 
		}
		
	{Operadores} { 
			token += String.valueOf(yyline+1) + " : " + yytext() + " - operador\n";
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
			token += String.valueOf(yyline+1) + " : " + temp + " - num_int\n";
			token += String.valueOf(yyline+1) + " : " + yytext() + " - operador\n";
			yybegin(YYINITIAL);
		}
	
	[.] { 
			yybegin(N_2);
		}
		
	[;|:|,|(|)] {
			token += String.valueOf(yyline+1) + " : " + temp + " - num_int\n";
			token += String.valueOf(yyline+1) + " : " + yytext() + " - delimitador\n";
			yybegin(YYINITIAL);
		}
	
	{EspacoEmBranco} {
			yybegin(YYINITIAL);
		}
	
	[^] {
			ErrorLog += "Erro na linha " + String.valueOf(yyline+1) + ": identificadores nao podem comecar com numeros (" + temp + " - " + yytext() + ")\n";
			yybegin(ERR);
		}
}

<N_2> {
	[.|;|:|,|(|)] {
			token += String.valueOf(yyline+1) + " : " + yytext() + " - delimitador\n";
			yybegin(YYINITIAL);
		}
		
	{Operadores} {
			ErrorLog += "Erro na linha " + String.valueOf(yyline+1) + ": delimitadores em um numero tipo float incompleto \n";
			token += String.valueOf(yyline+1) + " : " + yytext() + " - delimitador\n";
			yybegin(YYINITIAL);
		}
		
	{Numeros}+ {
			token += String.valueOf(yyline+1) + " : " + temp + "." + yytext() + " - num_real\n";
			yybegin(YYINITIAL);
		}
		
	[^] {
			ErrorLog += "Erro na linha " + String.valueOf(yyline+1) + ": identificadores nao podem começar com numeros ou conter pontos\n";
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