package br.com.pi.lux.controller;

import br.com.pi.lux.model.Usuario;
import br.com.pi.lux.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AlterarUsuarioController {

    @Autowired
    private UsuarioRepository repository;

    @GetMapping("/alterarUsu")
    public String mostrarFormularioAlteracao(@RequestParam("idUsuario") int idUsuario, Model model) {
        Optional<Usuario> usuarioOptional = repository.findById(idUsuario);
        if (usuarioOptional.isPresent()) {
            model.addAttribute("usuario", usuarioOptional.get());
            return "alterarUsu";
        } else {
            model.addAttribute("mensagem", "Usuário não encontrado!");
            return "redirect:/listarUsu";
        }
    }

    @PostMapping("/alterarUsu")
    public String alterarUsuario(@RequestParam int idUsuario, 
                                 @RequestParam String nome, 
                                 @RequestParam String cpf, 
                                 @RequestParam String email, 
                                 @RequestParam String grupo, 
                                 Model model) {
        Optional<Usuario> usuarioOptional = repository.findById(idUsuario);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            usuario.setNome(nome);
            usuario.setCpf(cpf);
            usuario.setEmail(email);
            usuario.setGrupo(grupo);
            repository.save(usuario);
            model.addAttribute("mensagem", "Usuário alterado com sucesso!");
            return "redirect:/listarUsu";
        } else {
            model.addAttribute("mensagem", "Usuário não encontrado!");
            return "alterarUsu";
        }
    }
}

