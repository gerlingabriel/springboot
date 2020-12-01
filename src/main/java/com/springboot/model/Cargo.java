package com.springboot.model;

public enum Cargo {
	
	/*
	 * JUNIOR, 
	PLENO, 
	SENIOR
	// pode ser feito assim somente ou
	 * 
	 * no HTMLse puxa somente "c"
	 * */
	
	JUNIOR("Júnior"), 
	PLENO("Plênor"), 
	SENIOR("Sênior");
	
	private String nome;
	private String valor;
	
	private Cargo(String nome) {
		this.nome =nome;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getValor() {
		return this.name(); // utilizando proprio do Enum
	}

	public void setValor(String valor) {
		this.valor = valor;
	}
	

	

}
