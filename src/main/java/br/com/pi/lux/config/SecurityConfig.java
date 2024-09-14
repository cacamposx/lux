package br.com.pi.lux.config;

import br.com.pi.lux.model.Usuario;
import br.com.pi.lux.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Configura as autorizações de requisições HTTP
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                // Permite acesso público à página de login
                                .requestMatchers("/login").permitAll()
                                // Exige autenticação para qualquer outra requisição
                                .anyRequest().authenticated()
                )
                // Configura o formulário de login
                .formLogin(formLogin ->
                        formLogin
                                // Define a página de login personalizada
                                .loginPage("/login")
                                // Define a URL de sucesso após o login
                                .defaultSuccessUrl("/home", true)
                                .permitAll()
                )
                // Configura o logout
                .logout(logout ->
                        logout.permitAll()
                );
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository usuarioRepository) {
        return email -> {
            // Busca o usuário pelo email
            Usuario usuario = usuarioRepository.findByEmail(email)
                    // Lança exceção se o usuário não for encontrado ou estiver inativo
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado ou inativo"));
            // Verifica se o usuário está ativo
            if (!usuario.isStatus()) {
                throw new UsernameNotFoundException("Usuário não encontrado ou inativo");
            }
            // Constrói um objeto User do Spring Security com as credenciais e roles do usuário
            return User.withUsername(usuario.getEmail())
                    .password(usuario.getSenha())
                    .roles(usuario.getGrupo())
                    .build();
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Define o codificador de senhas a ser usado
        return new BCryptPasswordEncoder();
    }
}