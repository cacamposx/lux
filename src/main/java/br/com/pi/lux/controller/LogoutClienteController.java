package br.com.pi.lux.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LogoutClienteController {

    @GetMapping("/logoutCliente")
    public String logout(HttpSession session) {
        // Invalida a sessão para remover todos os atributos armazenados
        session.invalidate();
        // Redireciona para a página de login ou inicial
        return "redirect:/loginCliente";
    }
}
