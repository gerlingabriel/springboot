package com.springboot.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotEmpty;

@Entity
public class Telefone implements Serializable{
	
	/**/
	private static final long serialVersionUID = 1L;
	/**/
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "telefone_sequence")
	@SequenceGenerator(name = "telefone_sequence", allocationSize = 1, initialValue = 1)
	private Long id;
	
	@NotEmpty(message = "Não pode ser vazio o número")
	@javax.validation.constraints.NotNull(message = "Não pode ser null o número")
	private String numero;
	
	@NotEmpty(message = "Não pode ser vazio o tipo")
	@javax.validation.constraints.NotNull(message = "Não pode ser null o tipo")
	private String tipo;
	
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private Pessoa pessoa;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	
}
