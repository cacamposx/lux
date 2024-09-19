package br.com.pi.lux.controller;

import br.com.pi.lux.model.Usuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@Controller

public class BackofficeController {

    @GetMapping("/backoffice")
    public String backoffice(HttpSession session, Model model) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuario");

        if (usuarioLogado == null || usuarioLogado.getGrupo() == null || usuarioLogado.getGrupo().isEmpty()) {
            return "redirect:/login";
        }

        model.addAttribute("usuarioLogado", usuarioLogado);

        return "backoffice";
    }


}
