package br.com.pi.lux.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.pi.lux.model.Produto;
import br.com.pi.lux.repository.ProdutoRepository;

@Controller
public class ListarProdutoController {

    @Autowired
    private ProdutoRepository repository;

    private static final int PRODUTOS_POR_PAGINA = 10;

    @GetMapping("/listarProd")
    public String listarProdutos(@RequestParam(defaultValue = "0") int pagina, Model model) {
        Pageable pageable = PageRequest.of(pagina, PRODUTOS_POR_PAGINA, Sort.by(Sort.Order.desc("data")));
        Page<Produto> produtosPage = repository.findAll(pageable);

        model.addAttribute("produtos", produtosPage.getContent());
        model.addAttribute("paginaAtual", pagina);
        model.addAttribute("totalPaginas", produtosPage.getTotalPages());
        model.addAttribute("totalProdutos", produtosPage.getTotalElements());

        return "listarProd";
    }
}
