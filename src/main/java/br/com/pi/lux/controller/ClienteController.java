package br.com.pi.lux.controller;

import br.com.pi.lux.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

}
