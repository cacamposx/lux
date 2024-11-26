package br.com.pi.lux.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LogoutClienteController {

    @GetMapping("/logoutCliente")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/loginCliente";
    }
}
