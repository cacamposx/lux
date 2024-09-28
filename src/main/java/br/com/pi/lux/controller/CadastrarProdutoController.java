package br.com.pi.lux.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import br.com.pi.lux.model.Produto;
import br.com.pi.lux.model.ProdutoImagem;
import br.com.pi.lux.repository.ProdutoImagemRepository;
import br.com.pi.lux.repository.ProdutoRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CadastrarProdutoController {

    @Autowired
    private ProdutoRepository repository;

    @Autowired
    private ProdutoImagemRepository imagemRepository;

    @GetMapping("/cadastrarProd")
    public String mostrarFormularioCadastro(Model model) {
        model.addAttribute("produto", new Produto());
        return "cadastrarProd";
    }

    @PostMapping("/cadastrarProd")
    public String inserirProduto(
            @RequestParam String nome,
            @RequestParam double preco,
            @RequestParam int quantidade,
            @RequestParam String descricao,
            @RequestParam double avaliacao,
            @RequestParam("imagens") MultipartFile[] imagens,
            @RequestParam(required = false, defaultValue = "0") int principalImagem, // Índice da imagem principal
            Model model) {

        Produto produto = new Produto();
        produto.setNome(nome);
        produto.setPreco(preco);
        produto.setQuantidade(quantidade);
        produto.setDescricao(descricao);
        produto.setAvaliacao(avaliacao);
        produto.setStatus(true);

        repository.save(produto); // Salva primeiro para gerar ID

        List<ProdutoImagem> listaImagens = new ArrayList<>();

        for (int i = 0; i < imagens.length; i++) {
            MultipartFile arquivo = imagens[i];
            if (!arquivo.isEmpty()) {
                try {
                    // Validação básica de imagem
                    String contentType = arquivo.getContentType();
                    if (contentType == null || !contentType.startsWith("image/")) {
                        continue; // Ignorar arquivos que não são imagens
                    }

                    ProdutoImagem produtoImagem = new ProdutoImagem();
                    produtoImagem.setImagem(arquivo.getBytes());
                    produtoImagem.setPrincipal(i == principalImagem);
                    produtoImagem.setProduto(produto);
                    listaImagens.add(produtoImagem);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        imagemRepository.saveAll(listaImagens);
        produto.setImagens(listaImagens);
        repository.save(produto);

        return "redirect:/listarProd";
    }
}
