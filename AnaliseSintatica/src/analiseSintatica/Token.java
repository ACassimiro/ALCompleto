package analiseSintatica;

public class Token {
	private String nome, simbolo;
	private int linha;
	
	//CONSTRUTOR
	public Token (String linha){
		String []temp = linha.split(" ");
		
		linha = temp[0];
		nome = temp[2];
		simbolo = temp[4];
	}
	
	
	//GETTERS & SETTERS
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSimbolo() {
		return simbolo;
	}

	public void setSimbolo(String simbolo) {
		this.simbolo = simbolo;
	}

	public int getLinha() {
		return linha;
	}

	public void setLinha(int linha) {
		this.linha = linha;
	}

}
