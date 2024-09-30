package br.com.pi.lux.controller;

import br.com.pi.lux.model.Produto;
import br.com.pi.lux.model.ProdutoImagem;
import br.com.pi.lux.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Base64;
import java.util.Optional;

@Controller
public class DetalhesProdutoController {

    @Autowired
    private ProdutoRepository repository;

    @GetMapping("/detalhesProd")
    public String detalhesProduto(@RequestParam("idProduto") int idProduto, Model model) {
        Optional<Produto> produtoOptional = repository.findById(idProduto);
        if (produtoOptional.isPresent()) {
            Produto produto = produtoOptional.get();

            // Converter imagens para Base64
            if (produto.getImagens() != null) {
                for (ProdutoImagem imagem : produto.getImagens()) {
                    if (imagem.getImagem() != null) {
                        String base64 = Base64.getEncoder().encodeToString(imagem.getImagem());
                        imagem.setImagemBase64(base64);
                    }
                }
            }

            model.addAttribute("produto", produto);
            return "detalhesProd";
        } else {
            model.addAttribute("mensagem", "Produto não encontrado!");
            return "redirect:/listarProd";
        }
    }
}
