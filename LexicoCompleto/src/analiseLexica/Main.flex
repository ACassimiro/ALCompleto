package analiseLexica;

%% 

%standalone
%class Main
%line 


%{
 String ident = ""; 
%}

%eof{ 
 System.out.println("Numeros de linhas = " + (yyline+1));
%eof}

FimDeLinha = \r|\n|\r\n
Caracteres = a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|x|z
Numeros = 0|1|2|3|4|5|6|7|8|9
Operadores = +|-|/|*|<|>|<=|>=|:|:=
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
			System.out.println("Erro na linha " + (yyline+1) +": Ponto avulso");
			yybegin(ERR); 
		}

	{EspacoEmBranco} { }

	[^] {
			System.out.println("Erro na linha " + (yyline+1) + ": Caractere '" + yytext() + "' invalido"); 
		}	
}

ERR {
	{EspacoEmBranco} { 
			yybegin(YYINITIAL);
		}
	[^] { }
}

<PAL_RESERVADA>{
	[.] { 
			System.out.println("Erro na linha " + (yyline+1) +": Identificadores nao podem conter pontos"); 
		}
}

VAR {
	[.] { 
			System.out.println("Erro na linha " + (yyline+1) +": Identificadores nao podem conter pontos");
			yybegin(ERR); 
		}
	{Operadores} { 
			yybegin(YYINITIAL);
		}
	{EspacoEmBranco} { 
			yybegin(YYINITIAL);
		}
	[^] {
			System.out.println("Erro na linha " + (yyline+1) +": Caractere desconhecido");
		}
}

N_1 {
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
			System.out.println("Erro na linha " + (yyline+1) + ": Identificadores nao podem começar com numeros");
			yybegin(ERR);
		}
}

N_2 {
	[.] {
			System.out.println("Erro na linha " + (yyline+1) + ": Valor ja contem um ponto");
			yybegin(ERR);
		}
		
	{Operadores} {
			yybegin(YYINITIAL);
		}
		
	{Numeros} { }
	
	[^] {
			System.out.println("Erro na linha " + (yyline+1) + ": Identificadores nao podem começar com numeros ou conter pontos");
			yybegin(ERR);
		}
}