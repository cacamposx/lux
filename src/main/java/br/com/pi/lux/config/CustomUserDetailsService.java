package br.com.pi.lux.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import br.com.pi.lux.model.Usuario;
import br.com.pi.lux.repository.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Busque o usuário pelo email no repositório
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + email));

        // Crie o objeto UserDetails com as informações do usuário do banco
        return User.withUsername(usuario.getEmail())
                .password(usuario.getSenha())  // Certifique-se de que a senha está criptografada
                .roles(usuario.getGrupo())     // Use o campo 'grupo' para definir as permissões
                .build();
    }
}
