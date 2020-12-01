package com.springboot.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

@Entity
public class Profissao {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "profissao_sequence")
	@SequenceGenerator(name = "profissao_sequence", initialValue = 1, allocationSize = 1)
	private Long id;
	
	private String nomeProfissao;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNomeProfissao() {
		return nomeProfissao;
	}

	public void setNomeProfissao(String nomeProfissao) {
		this.nomeProfissao = nomeProfissao;
	}


}
