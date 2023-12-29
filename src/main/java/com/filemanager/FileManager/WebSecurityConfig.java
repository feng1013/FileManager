package com.filemanager.FileManager;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Autowired
	private UserRepository userRepository;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http
			.authorizeHttpRequests((requests) -> requests
				.requestMatchers("/", "/home", "/invalid", "/download", "/download/*", "/assets").permitAll()
				.requestMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/img/**", "/icon/**", "/assets/**").permitAll()
				.anyRequest().authenticated()
			)
			.formLogin((form) -> form
				.loginPage("/login")
				.permitAll()
			)
			.logout((logout) -> logout.logoutSuccessUrl("/login").permitAll());

		return http.build();
	}

	@Bean
	public UserDetailsService userDetailsService() {

		InMemoryUserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();

		for(com.filemanager.FileManager.User userData : userRepository.findAll()){
			UserDetails user =
			 User.withDefaultPasswordEncoder()
				.username(userData.getUsername())
				.password(userData.getPassword())
				.roles("USER")
				.build();

			userDetailsManager.createUser(user);
		}

		return userDetailsManager;
	}
}