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

    // Método para validar CPF
    private boolean isValidCPF(String cpf) {
        String cpfLimpo = cpf.replaceAll("[^\\d]", "");

        if (cpfLimpo.length() != 11 || cpfLimpo.matches("^(\\d)\\1{10}$")) {
            return false;
        }

        int soma = 0;
        int peso = 10;

        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(cpfLimpo.charAt(i)) * peso--;
        }

        int digito1 = 11 - (soma % 11);
        digito1 = digito1 > 9 ? 0 : digito1;

        soma = 0;
        peso = 11;

        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(cpfLimpo.charAt(i)) * peso--;
        }

        int digito2 = 11 - (soma % 11);
        digito2 = digito2 > 9 ? 0 : digito2;

        return cpfLimpo.charAt(9) == (char) (digito1 + '0') && cpfLimpo.charAt(10) == (char) (digito2 + '0');
    }

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
            // Criação do objeto Usuario com o construtor correto
            model.addAttribute("mensagem", "As senhas não coincidem!");
            model.addAttribute("usuario", new Usuario(idUsuario, nome, cpf, senha, grupo, "", true));
            return "alterarUsu";
        }

        // Remove caracteres não numéricos do CPF
        String cpfLimpo = cpf.replaceAll("[^\\d]", "");

        // Valida o CPF
        if (!isValidCPF(cpfLimpo)) {
            // Criação do objeto Usuario com o construtor correto
            model.addAttribute("mensagem", "CPF inválido!");
            model.addAttribute("usuario", new Usuario(idUsuario, nome, cpf, senha, grupo, "", true));
            return "alterarUsu";
        }

        Optional<Usuario> usuarioOptional = repository.findById(idUsuario);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            usuario.setNome(nome);
            usuario.setCpf(cpfLimpo);
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
