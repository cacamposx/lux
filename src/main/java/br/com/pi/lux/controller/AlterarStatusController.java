package br.com.pi.lux.controller;

import br.com.pi.lux.model.Usuario;
import br.com.pi.lux.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AlterarStatusController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/alterarStatus")
    public String alterarStatus(@RequestParam("idUsuario") int idUsuario) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(idUsuario);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            usuario.setStatus(!usuario.isStatus());
            usuarioRepository.save(usuario);
        }
        return "redirect:/listarUsu";
    }
}