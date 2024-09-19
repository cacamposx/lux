package br.com.pi.lux.controller;

import br.com.pi.lux.model.Usuario;
import br.com.pi.lux.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.mindrot.jbcrypt.BCrypt;

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
                                 @RequestParam String senha,
                                 @RequestParam String confirmaSenha,
                                 @RequestParam String grupo,
                                 Model model) {

        // Verifica se as senhas coincidem
        if (!senha.equals(confirmaSenha)) {
            model.addAttribute("mensagem", "As senhas não coincidem!");
            return "alterarUsu";
        }

        Optional<Usuario> usuarioOptional = repository.findById(idUsuario);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            usuario.setNome(nome);
            usuario.setCpf(cpf.replaceAll("[^\\d]", "")); // Remove caracteres não numéricos
            usuario.setGrupo(grupo);

            // Atualiza a senha encriptada
            if (!senha.isEmpty()) {
                usuario.setSenha(BCrypt.hashpw(senha, BCrypt.gensalt()));
            }

            repository.save(usuario);
            model.addAttribute("mensagem", "Usuário alterado com sucesso!");
            return "redirect:/listarUsu";
        } else {
            model.addAttribute("mensagem", "Usuário não encontrado!");
            return "alterarUsu";
        }
    }
}
