package com.springboot.repository;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.model.Pessoa;

@Repository
@Transactional
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {

	public Iterable<Pessoa> findAllByOrderByIdAsc();

	@Query("select p from Pessoa p where p.nome like %?1%")
	public Iterable<Pessoa> pesquisaPorNome(String nome);

	@Query("select p from Pessoa p where p.nome like %?1% and p.sexo = ?2")
	public Iterable<Pessoa> pesquisaPorNomeSexo(String nome, Character sexo);

	@Query("select p from Pessoa p where p.sexo = ?1")
	public Iterable<Pessoa> pesquisaPorSexo(Character sexo);

	default Page<Pessoa> findPessoaPorNome(String nome, Pageable pageable) {

		Pessoa pessoa = new Pessoa();
		pessoa.setNome(nome);

		/* Configução a pesquisa por parte no banco de dados / igual ao like do SQL */
		ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny().withMatcher("nome",
				ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
		
		/*Uni o objeto com o valor e a configruação para consultar*/
		Example<Pessoa> example = Example.of(pessoa, exampleMatcher);
		
		Page<Pessoa> pessoas = findAll(example, pageable);

		return pessoas;
	}
	
	default Page<Pessoa> findPessoaPorSexoNome(Character sexo, String nome, Pageable pageable) {

		Pessoa pessoa = new Pessoa();
		pessoa.setSexo(sexo);
		pessoa.setNome(nome);

		/* Configução a pesquisa por parte no banco de dados / igual ao like do SQL */
		ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny()
				.withMatcher("sexo", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
				.withMatcher("nome", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
		
		/*Uni o objeto com o valor e a configruação para consultar*/
		Example<Pessoa> example = Example.of(pessoa, exampleMatcher);
		
		Page<Pessoa> pessoas = findAll(example, pageable);

		return pessoas;
	}

	@Query("select p from Pessoa p where p.nome like %?1% and p.sexo = ?2")
	Page<Pessoa> testeSexoNome(Pageable pageable, String nome, Character sexo);
}
