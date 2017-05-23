package analiseSemantica;

public class Variavel {
	private String nome, tipo;
	private boolean inicializada = false;

	public Variavel(String nome, String tipo) {
		this.nome = nome;
		this.tipo = tipo;
	}	
	
	public Variavel(String nome, String tipo, boolean inicializada) {
		this.nome = nome;
		this.tipo = tipo;
		this.inicializada = inicializada;
	}	
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	public boolean getInicializada() {
		return inicializada;
	}


	public void setInicializada(boolean inicializada) {
		this.inicializada = inicializada;
	}
}
