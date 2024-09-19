package br.com.pi.lux.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.pi.lux.model.Usuario;
import br.com.pi.lux.repository.UsuarioRepository;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ListarUsuarioController {

    @Autowired
    private UsuarioRepository repository;

    @GetMapping("/listarUsu")
    public String listarUsuarios(
            @RequestParam(required = false) String nome,
            Model model,
            HttpSession session) {


        Usuario usuarioLogado = (Usuario) session.getAttribute("usuario");


        if (usuarioLogado != null && "admin".equals(usuarioLogado.getGrupo())) {
            List<Usuario> usuarios;
            if (nome != null && !nome.isEmpty()) {
                usuarios = repository.findByNomeContainingIgnoreCase(nome);
            } else {
                usuarios = repository.findAll();
            }
            model.addAttribute("usuarios", usuarios);
            return "listarUsu";
        } else {

            return "redirect:/login";
        }
    }
}
