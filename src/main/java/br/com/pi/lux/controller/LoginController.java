package br.com.pi.lux.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.pi.lux.model.Usuario;
import br.com.pi.lux.repository.UsuarioRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private UsuarioRepository repository;

    @GetMapping("/login")
    public String mostrarFormularioLogin(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "login";
    }

    @PostMapping("/login")
    public String autenticarUsuario(
            @RequestParam String email,
            @RequestParam String senha,
            Model model) {

        Optional<Usuario> usuarioOptional = repository.findByEmail(email);
    
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
    
            if (BCrypt.checkpw(senha, usuario.getSenha())) {
                model.addAttribute("mensagem", "Login realizado com sucesso!");
                return "backoffice";
            } else {
                model.addAttribute("mensagem", "Senha incorreta!");
                return "login";
            }
        } else {
            model.addAttribute("mensagem", "E-mail não cadastrado!");
            return "login";
        }
    }
    
}
