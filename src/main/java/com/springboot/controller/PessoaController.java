package com.springboot.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.springboot.model.Pessoa;
import com.springboot.model.Telefone;
import com.springboot.repository.PessoaRepository;
import com.springboot.repository.ProfissaoRepository;
import com.springboot.repository.TelefoneRepository;

@Controller
public class PessoaController {

	@Autowired
	private PessoaRepository pessoaRepository;

	@Autowired
	private TelefoneRepository telefoneRepository;

	@Autowired
	private ReportUtil reportUril;

	@Autowired
	private ProfissaoRepository profissaoRepository;

	@RequestMapping(method = RequestMethod.GET, value = "/cadastropessoa")
	public ModelAndView cadastro() {

		Iterable<Pessoa> iterablePessoa = pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("id")));

		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		andView.addObject("pessoacampo", new Pessoa()); // deixa criado para ser usado quando chamar a função editar
		andView.addObject("profissao", profissaoRepository.findAll());
		andView.addObject("pessoas", iterablePessoa);

		return andView;

	}

	@RequestMapping(method = RequestMethod.POST, value = "**/salvarpessoa", consumes = {"multipart/form-data"})
	public ModelAndView salvar(@Valid Pessoa pessoa, BindingResult bindingResult, final MultipartFile file)
			throws IOException {

		pessoa.setTelefones(telefoneRepository.listar(pessoa.getId()));
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");

		/* Verificar se existe um aquivo na tela e add */
		if (file.getSize() > 0) {

			pessoa.setArquivo(file.getBytes());
			pessoa.setNomeArquivo(file.getOriginalFilename());
			pessoa.setTipoArquivo(file.getContentType());

		} else if (pessoa.getId() != null) { // caso não tenha arquivo puxar o que tem no banco de dados

			pessoa.setArquivo(pessoaRepository.findById(pessoa.getId()).get().getArquivo());
			pessoa.setNomeArquivo(pessoaRepository.findById(pessoa.getId()).get().getNomeArquivo());
			pessoa.setTipoArquivo(pessoaRepository.findById(pessoa.getId()).get().getTipoArquivo());

		}

		/* Verificar se há erros */
		if (bindingResult.hasErrors()) {

			if (file.getSize() > 0) {

				pessoa.setArquivo(file.getBytes());

			}

			andView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("id"))));
			andView.addObject("pessoacampo", pessoa); // carregar dados da tela para permanecer o preenchimento
			andView.addObject("profissao", profissaoRepository.findAll());

			List<String> msg = new ArrayList<String>();
			for (ObjectError objectError : bindingResult.getAllErrors()) {
				msg.add(objectError.getDefaultMessage()); // trazer as mensagens com os erros
			}

			andView.addObject("mensagens", msg);

			return andView;
		} // se não tiver erros segue as instruções abaixo

		pessoaRepository.save(pessoa);

		andView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("id"))));
		andView.addObject("pessoacampo", new Pessoa()); // precisa ser criado porque ele espera sempre alguma coisa
														// nesse atributo
		andView.addObject("profissao", profissaoRepository.findAll());

		return andView;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/listapessoas")
	public ModelAndView pessoas() {

		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");

		andView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("id"))));
		andView.addObject("pessoacampo", new Pessoa()); // precisa ser criado porque ele espera sempre alguma coisa
														// nesse atributo
		andView.addObject("profissao", profissaoRepository.findAll());

		return andView;

	}

	@GetMapping("/editarpessoa/{pessoaid}")
	public ModelAndView editar(@PathVariable("pessoaid") Long pessoaid) {

		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");

		Optional<Pessoa> AuxPessoa = pessoaRepository.findById(pessoaid);

		andView.addObject("pessoacampo", AuxPessoa.get());
		andView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("id"))));
		andView.addObject("profissao", profissaoRepository.findAll());

		return andView;
	}

	@GetMapping("/excluir/{pessoaid}")
	public ModelAndView excluir(@PathVariable("pessoaid") Long pessoaid) {

		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");

		pessoaRepository.deleteById(pessoaid);

		andView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("id"))));

		andView.addObject("pessoacampo", new Pessoa()); // precisa ser criado porque ele espera sempre alguma coisa
		// nesse atributo
		andView.addObject("profissao", profissaoRepository.findAll());

		return andView;
	}

	/* Metodo de baixa nunca tem retorno,ó baixar */
	@GetMapping("/download/{pessoaid}")
	public void download(@PathVariable("pessoaid") Long pessoaid, HttpServletResponse response) throws IOException {

		Pessoa pessoaAux = pessoaRepository.findById(pessoaid).get();
		if (pessoaAux.getArquivo() != null) {

			// Setar o tamanho da responsta
			response.setContentLength(pessoaAux.getArquivo().length);

			// tipo do arquivo - se quiser passar parametro generica
			// "application/octet-stream"
			response.setContentType(pessoaAux.getTipoArquivo());

			// define o cabeçalho da responsa
			String headerKey = "Content-Disposition";
			String headerValeu = String.format("attachment; filename=\"%s\"", pessoaAux.getNomeArquivo());
			response.setHeader(headerKey, headerValeu);

			// finaliza
			response.getOutputStream().write(pessoaAux.getArquivo());

		}

	}

	@PostMapping("**/pesquisanome")
	public ModelAndView pesquisar(@RequestParam("pesquisanome") String nome, @RequestParam("sexoPesquisa") String sexo,
			@PageableDefault(size = 5, sort = { "id" }) Pageable pageable) {

		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");

		if (sexo != "") {// quando sexo foi preenchido
			Character auxSexo;
			if (sexo.equalsIgnoreCase("M")) {
				auxSexo = 'M';
			} else {
				auxSexo = 'F';
			}

			andView.addObject("pessoas", pessoaRepository.testeSexoNome(pageable, nome, auxSexo));
			andView.addObject("profissao", profissaoRepository.findAll());
			andView.addObject("pessoacampo", new Pessoa()); // precisa ser criado porque ele espera sempre alguma coisa
			andView.addObject("pesquisanome", nome);
			andView.addObject("sexoPesquisa", sexo);
			
			return andView;
		} // quando sexo está vazio
			// Page<Pessoa> pessoas =
		andView.addObject("pessoas", pessoaRepository.findPessoaPorNome(nome, pageable));
		andView.addObject("pessoacampo", new Pessoa()); // precisa ser criado porque ele espera sempre alguma coisa
		andView.addObject("profissao", profissaoRepository.findAll());
		andView.addObject("pesquisanome", nome);

		return andView;
	}

	@GetMapping("**/pesquisanome") // metodo para gerar PDF *********** cuidado que nome é o mesmo ao em cima
	public void gerarPDF(@RequestParam("pesquisanome") String nome, @RequestParam("sexoPesquisa") String sexo,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		List<Pessoa> lista = new ArrayList<Pessoa>();
		Character auxSexo;
		Boolean campoSexoPreenchido = false;
		Boolean campoNomePreenchido = false;

		if (sexo != "") {
			if (sexo.equalsIgnoreCase("M")) {
				auxSexo = 'M';
				campoSexoPreenchido = true;
			} else {
				auxSexo = 'F';
				campoSexoPreenchido = true;
			}
		} else {
			auxSexo = 'V'; // validação para verifica se veio o sexo na pesqquisa (V de vazio) // programa
							// exigiu
		}

		if (nome != "") {
			campoNomePreenchido = true;
		} // validação para verifica se veio o nome na pesqquisa

		/*
		 * Com as informações acima agora é saber qual relatória deseja
		 **************************************/

		if (campoNomePreenchido && campoSexoPreenchido) {
			lista = (List<Pessoa>) pessoaRepository.pesquisaPorNomeSexo(nome, auxSexo);
			// quando os dois campos são preenchidos
		} else if (campoNomePreenchido || campoSexoPreenchido) {
			if (campoNomePreenchido) {
				lista = (List<Pessoa>) pessoaRepository.pesquisaPorNome(nome);
				// somente campo nome foi preenchido
			} else {
				lista = (List<Pessoa>) pessoaRepository.pesquisaPorSexo(auxSexo);
			} // Somente se campo sexo foi preenchido

		} else {
			lista = (List<Pessoa>) pessoaRepository.findAll(Sort.by("id"));
		} // quando nem nome e sem sexo foi preenchido = significa todos
		
		

		/**********************************************************************************/
		/* Chamar o serviço para efeturar PDF */
		byte[] pdf = reportUril.geradorRelatorio(lista, "pessoa", request.getServletContext());

		/* tamanho da resposta */
		response.setContentLength(pdf.length);
		/* Definir na responsa o tipo do arquivo */
		response.setContentType("application/octet-stream");
		/* Definir cabeçalho da resposta */
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"", "relatorio.pdf");
		response.setHeader(headerKey, headerValue);

		/* Finaliza a resposta para navegador */
		response.getOutputStream().write(pdf);
	}

	@GetMapping("/telefones/{pessoaid}")
	public ModelAndView telefones(@PathVariable("pessoaid") Long pessoaid) {

		ModelAndView andView = new ModelAndView("cadastro/telefones");

		Optional<Pessoa> auxPessoa = pessoaRepository.findById(pessoaid);

		andView.addObject("pessoacampo", auxPessoa.get());
		andView.addObject("telefones", telefoneRepository.listar(pessoaid));

		return andView;
	}

	@PostMapping("**/addFone/{pessoaid}")
	public ModelAndView addFone(@Valid Telefone telefone, BindingResult bindingResult,
			@PathVariable("pessoaid") Long pessoaid) {
		/*
		 * @Valid e BindingResult devem está juntos, se não Spring falhará em validar o
		 * objeto e lançará uma exceção.
		 */

		Optional<Pessoa> auxPessoa = pessoaRepository.findById(pessoaid); // manter id e assim a Objeto da pessoa

		if (bindingResult.hasErrors()) {

			ModelAndView andView = new ModelAndView("cadastro/telefones");
			andView.addObject("pessoacampo", auxPessoa.get());
			andView.addObject("telefones", telefoneRepository.listar(pessoaid));

			List<String> msg = new ArrayList<String>();
			for (ObjectError objectError : bindingResult.getAllErrors()) {
				msg.add(objectError.getDefaultMessage()); // trazer as mensagens com os erros
			}

			andView.addObject("mensagens", msg);
			return andView;

		}

		ModelAndView andView = new ModelAndView("cadastro/telefones");

		telefone.setPessoa(auxPessoa.get());
		telefoneRepository.save(telefone);
		/*
		 * ou posso fazer: Pessoa AuxPessoa = pessoaRepository.findById(pessoaid).get();
		 * telefone.setPessoa(auxPessoa);
		 */

		andView.addObject("pessoacampo", auxPessoa.get());
		andView.addObject("telefones", telefoneRepository.listar(pessoaid));

		return andView;
	}

	@GetMapping("/excluirFone/{foneid}")
	public ModelAndView excluirFone(@PathVariable("foneid") Long pessoaid) {

		Pessoa auxPessoa = telefoneRepository.findById(pessoaid).get().getPessoa();
		telefoneRepository.deleteById(pessoaid);

		ModelAndView andView = new ModelAndView("cadastro/telefones"); // trazer a pagina
		andView.addObject("pessoacampo", auxPessoa); // trazer obj pessoa
		andView.addObject("telefones", telefoneRepository.listar(auxPessoa.getId())); // carregar os telefones

		return andView;
	}

	/*Paginação -------------------------------------------------------------------------------------------------*/
	@GetMapping("/pessoapag")
	public ModelAndView carregarListaPorPaginacao(@PageableDefault(size = 5) Pageable pageable, ModelAndView andView,
			@RequestParam("pesquisanome") String pesquisanome, @RequestParam("sexoPesquisa") String sexo) {

		// Page<Pessoa> pagePessoa =
		// pessoaRepository.findAll(PageRequest.of(pageable.getPageNumber(),
		// pageable.getPageSize(), Sort.by("id"))); // aqui
		// galera, a // ordenação
		
		Character auxSexo;
		if (sexo != "") {// quando sexo foi preenchido
			
			if (sexo.equalsIgnoreCase("M")) {
				auxSexo = 'M';
			} else {
				auxSexo = 'F';
			}
			
			Page<Pessoa> pagePessoa = pessoaRepository.testeSexoNome(pageable, pesquisanome, auxSexo);
			
			andView.addObject("pessoas", pagePessoa); // carregar a paginação
			andView.addObject("pessoacampo", new Pessoa()); // precisa ser criado porque ele espera sempre alguma coisa
			andView.setViewName("cadastro/cadastropessoa");
			andView.addObject("pesquisanome", pesquisanome);
			andView.addObject("sexoPesquisa", sexo);
			andView.addObject("profissao", profissaoRepository.findAll());
			return andView;
			
		} else {
			
			Page<Pessoa> pagePessoa = pessoaRepository.findPessoaPorNome(pesquisanome, pageable);

			andView.addObject("pessoas", pagePessoa); // carregar a paginação
			andView.addObject("pessoacampo", new Pessoa()); // precisa ser criado porque ele espera sempre alguma coisa
			andView.setViewName("cadastro/cadastropessoa");
			andView.addObject("profissao", profissaoRepository.findAll());
			andView.addObject("pesquisanome", pesquisanome);
			return andView;
			
		} 


	}
	
	
}
