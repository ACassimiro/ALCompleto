package analiseLexica;

import java.lang.String;

%% 

%standalone
%class Main
%line 


%{
 String tokens = ""; 
 String ErrorLog = "";
%}

%eof{ 
 System.out.println("Numeros de linhas = " + (yyline+1));
 System.out.println(ErrorLog);
%eof}

FimDeLinha = \r|\n|\r\n
Caracteres = a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|x|z
Numeros = 0|1|2|3|4|5|6|7|8|9
Operadores = [+|-|/|*|<|>|<=|>=|:|:=]
Ident = {Caracteres}+({Numeros}|{Caracteres}|_)*
EspacoEmBranco = {FimDeLinha} | [ \t\f]

%state PAL_RESERVADA, N_1, N_2, ERR, VAR

%%

<YYINITIAL>{
	"program" { yybegin(PAL_RESERVADA); }
	"var" { yybegin(PAL_RESERVADA); }
	"integer" { yybegin(PAL_RESERVADA); }
	"if" { yybegin(PAL_RESERVADA); }
	"then" { yybegin(PAL_RESERVADA); }
	"else" { yybegin(PAL_RESERVADA); }
	"end" { yybegin(PAL_RESERVADA); }
	"real" { yybegin(PAL_RESERVADA); }
	"while" { yybegin(PAL_RESERVADA); }
	"not" { yybegin(PAL_RESERVADA); }
	"do" { yybegin(PAL_RESERVADA); }
	"procedure" { yybegin(PAL_RESERVADA); }
	"boolean" { yybegin(PAL_RESERVADA); }
	"begin" { yybegin(PAL_RESERVADA); }
	
	{Ident} { yybegin(VAR); }
	
	{Numeros} { yybegin(N_1); }
	
	{Operadores} { 	}

	[.] { 
			ErrorLog += "Erro na linha " + String.valueOf(yyline+1) +": Ponto avulso\n";
			yybegin(ERR); 
		}

	{EspacoEmBranco} { }

	[^] {
			ErrorLog += "Erro na linha " + String.valueOf(yyline+1) + ": Caractere '" + yytext() + "' invalido\n"; 
		}	
	
}


<PAL_RESERVADA>{
	[.] { 
			ErrorLog += "Erro na linha " + String.valueOf(yyline+1) +": Identificadores nao podem conter pontos\n"; 
		}
	{EspacoEmBranco} { 
			yybegin(YYINITIAL);
		}
	[^] {
			ErrorLog += "Erro na linha " + String.valueOf(yyline+1) +": Caractere desconhecido\n";
		}
}

<VAR> {
	[.] { 
			ErrorLog += "Erro na linha " + String.valueOf(yyline+1) +": Identificadores nao podem conter pontos \n";
			yybegin(ERR); 
		}
	{Operadores} { 
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
			yybegin(YYINITIAL);
		}
	
	[.] { 
			yybegin(N_2);
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
	[.] {
			ErrorLog += "Erro na linha " + String.valueOf(yyline+1) + ": Valor ja contem um ponto\n";
			yybegin(ERR);
		}
		
	{Operadores} {
			yybegin(YYINITIAL);
		}
		
	{Numeros} { }
	
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