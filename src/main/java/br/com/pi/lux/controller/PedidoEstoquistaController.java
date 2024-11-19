package br.com.pi.lux.controller;

import br.com.pi.lux.model.Pedido;
import br.com.pi.lux.model.Usuario;
import br.com.pi.lux.repository.PedidoRepository;
import br.com.pi.lux.repository.UsuarioRepository;
import br.com.pi.lux.service.PedidoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PedidoEstoquistaController {

    @Autowired
    private PedidoService pedidoService;


    @GetMapping("/listarPedidos")
    public String listarPedidos(
            @RequestParam(required = false) String nome,
            Model model,
            HttpSession session) {

        Usuario usuarioLogado = (Usuario) session.getAttribute("usuario");

        if (usuarioLogado != null && "estoquista".equals(usuarioLogado.getGrupo())) {

            List<Pedido> pedidos;
            pedidos = pedidoService.listarPedidos();

            // Ordena os pedidos pela data de forma decrescente
            pedidos.sort((p1, p2) -> p2.getData().compareTo(p1.getData()));

            model.addAttribute("pedidos", pedidos);
            return "listarPedidos"; // Nome da view (HTML)
        } else {
            return "redirect:/login";
        }
    }


    @PostMapping("/alterarStatus")
    public String alterarStatus(@RequestParam int idPedido, @RequestParam String status, Model model) {
        Pedido pedido = pedidoService.buscarPorId(idPedido);

        if (pedido != null) {
            pedido.setStatus(status);
            pedidoService.salvarPedido(pedido);
        }

        return "redirect:/listarPedidos";
    }
}
