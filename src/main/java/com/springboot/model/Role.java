package com.springboot.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import org.springframework.security.core.GrantedAuthority;

@Entity
public class Role implements GrantedAuthority {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator =  "role_sequence")
	@SequenceGenerator(name = "role_sequence", initialValue = 1, allocationSize = 1)
	private Long id;

	private String nomeRole;

	@Override
	public String getAuthority() { // Sempre implemente ROLE_GERENTE, RULE_SECRETARIA, RULE_ASSISTENTE
		// TODO Auto-generated method stub
		return this.nomeRole;
	}

	public String getNomeRole() {
		return nomeRole;
	}

	public void setNomeRole(String nomeRole) {
		this.nomeRole = nomeRole;
	}

}
