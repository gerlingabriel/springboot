package com.springboot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter {

	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;

	@Override // Configurar as soliciações por http
	protected void configure(HttpSecurity http) throws Exception {

		http.csrf().disable() // desabilita as configurações padrões do spring
				.authorizeRequests() // Permitir restrições de acesso
				.antMatchers(HttpMethod.GET, "/").permitAll() // qualquer login acessa ao sistema
				.antMatchers(HttpMethod.GET, "/cadastropessoa").hasAnyRole("GERENTE, COORDENAÇÃO") // faz uma restrição com pagina cadastrar
				.anyRequest().authenticated().and().formLogin().permitAll() /// formulario de login permitir o acesso
				.loginPage("/login")
				.defaultSuccessUrl("/cadastropessoa")
				.failureUrl("/login?error=true")
				.and().logout().logoutSuccessUrl("/login") // mapeia URL de logout e invalida usuário autenticado
														   //fazendo logout volta para tela login
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"));

	}

	@Override // cria atenticação do usuário com o banco de dados ou em mémoria
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		auth.userDetailsService(implementacaoUserDetailsService).passwordEncoder(new BCryptPasswordEncoder());

		/*
		 * auth.inMemoryAuthentication().passwordEncoder(new
		 * BCryptPasswordEncoder()).withUser("teste")
		 * .password("$2a$10$2eGCtNtjDwJWe9W5IvqRSuix1Qn9/cezIPngtf5ddDvR1ru2dkVvq") //
		 * teste criptografado .roles("ADMIN");
		 */

	}

	@Override // Ingona URl especificas
	public void configure(WebSecurity web) throws Exception {

		web.ignoring().antMatchers("/materialize/**");

	}

}
