package br.com.pi.lux.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/backoffice")
public class BackofficeController {
    @GetMapping("/backoffice")
    public String backoffice() {
        return "backoffice";
    }
}