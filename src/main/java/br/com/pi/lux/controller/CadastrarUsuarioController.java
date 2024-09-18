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
public class CadastrarUsuarioController {

    @Autowired
    private UsuarioRepository repository;

    @GetMapping("/cadastrarUsu")
    public String mostrarFormularioCadastro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "cadastrarUsu";
    }

    @PostMapping("/cadastrarUsu")
    public String inserirUsuario(
            @RequestParam String nome,
            @RequestParam String cpf,
            @RequestParam String email,
            @RequestParam String senha,
            @RequestParam String confirmaSenha,
            @RequestParam String grupo,
            Model model) {

        if (!senha.equals(confirmaSenha)) {
            model.addAttribute("mensagem", "As senhas não coincidem!");
            return "cadastrarUsu"; 
        }

        if (!isValidCPF(cpf)) {
            model.addAttribute("mensagem", "CPF inválido!");
            return "cadastrarUsu"; 
        }

        Optional<Usuario> usuarioExistente = repository.findByEmail(email);
        if (usuarioExistente.isPresent()) {
            model.addAttribute("mensagem", "O email já está cadastrado!");
            return "cadastrarUsu"; 
        }

        Optional<Usuario> cpfExistente = repository.findByCpf(cpf.replaceAll("[^\\d]", ""));
        if (cpfExistente.isPresent()) {
            model.addAttribute("mensagem", "O CPF já está cadastrado!");
            return "cadastrarUsu"; 
        }

        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setCpf(cpf.replaceAll("[^\\d]", "")); 
        usuario.setEmail(email);
        usuario.setSenha(BCrypt.hashpw(senha, BCrypt.gensalt()));
        usuario.setGrupo(grupo);
        usuario.setStatus(true);

        repository.save(usuario);

        model.addAttribute("mensagem", "Usuário cadastrado com sucesso!");
        model.addAttribute("usuario", new Usuario());

        return "cadastrarUsu"; 
    }

    private boolean isValidCPF(String cpf) {
        String cpfLimpo = cpf.replaceAll("[^\\d]", "");
        return cpfLimpo.length() == 11;
    }
}

