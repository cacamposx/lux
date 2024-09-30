package br.com.pi.lux.controller;

import br.com.pi.lux.model.Produto;
import br.com.pi.lux.model.ProdutoImagem;
import br.com.pi.lux.repository.ProdutoImagemRepository;
import br.com.pi.lux.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Controller
public class AlterarProdutoController {

    @Autowired
    private ProdutoRepository repository;

    @Autowired
    private ProdutoImagemRepository imagemRepository;

    @GetMapping("/alterarProd")
    public String mostrarFormularioAlteracao(@RequestParam("idProduto") int idProduto, Model model) {
        Optional<Produto> produtoOptional = repository.findById(idProduto);
        if (produtoOptional.isPresent()) {
            Produto produto = produtoOptional.get();

            // Converter imagens para Base64
            produto.getImagens().forEach(imagem -> {
                if (imagem.getImagem() != null) {
                    String base64 = Base64.getEncoder().encodeToString(imagem.getImagem());
                    imagem.setImagemBase64(base64);
                }
            });

            model.addAttribute("produto", produto);

            // Verifica o papel do usuário
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Object principal = authentication.getPrincipal();
            boolean isEstoquista = false;

            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                isEstoquista = userDetails.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_ESTOQUISTA"));
            }

            model.addAttribute("isEstoquista", isEstoquista);
            return "alterarProd";
        } else {
            model.addAttribute("mensagem", "Produto não encontrado!");
            return "redirect:/listarProd";
        }
    }

    @PostMapping("/alterarProd")
    public String alterarProduto(
            @RequestParam int idProduto,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Double preco,
            @RequestParam(required = false) Integer quantidade,
            @RequestParam(required = false) String descricao,
            @RequestParam(required = false) Double avaliacao,
            @RequestParam(required = false) Boolean status,
            @RequestParam("imagens") MultipartFile[] imagens,
            @RequestParam(required = false, defaultValue = "0") int principalImagem, // Índice da imagem principal
            Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principalUser = authentication.getPrincipal();
        boolean isEstoquista = false;

        if (principalUser instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principalUser;
            isEstoquista = userDetails.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ESTOQUISTA"));
        }

        Optional<Produto> produtoOptional = repository.findById(idProduto);
        if (produtoOptional.isPresent()) {
            Produto produto = produtoOptional.get();

            if (isEstoquista) {
                if (quantidade != null) {
                    produto.setQuantidade(quantidade);
                } else {
                    model.addAttribute("mensagem", "Você não tem permissão para alterar esta informação!");
                    model.addAttribute("produto", produto); // Recarrega o produto para exibir os dados corretos
                    return "alterarProd";
                }
            } else {
                // Admin ou outros papéis podem alterar todos os campos
                if (nome != null) produto.setNome(nome);
                if (preco != null) produto.setPreco(preco);
                if (descricao != null) produto.setDescricao(descricao);
                if (avaliacao != null) produto.setAvaliacao(avaliacao);
                if (status != null) produto.setStatus(status);
                if (quantidade != null) produto.setQuantidade(quantidade);
            }

            // Gerenciar imagens
            List<ProdutoImagem> listaImagens = new ArrayList<>();

            if (imagens != null && imagens.length > 0) {
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
                            model.addAttribute("mensagem", "Erro ao processar as imagens.");
                            model.addAttribute("produto", produto);
                            return "alterarProd";
                        }
                    }
                }
                // Agora adicione as novas imagens ao produto
                produto.getImagens().clear(); // Limpa a lista de imagens existentes
                produto.getImagens().addAll(listaImagens); // Adiciona novas imagens
            }

            repository.save(produto); // Salva as alterações no produto

            model.addAttribute("mensagem", "Produto alterado com sucesso!");
            return "redirect:/listarProd";
        } else {
            model.addAttribute("mensagem", "Produto não encontrado!");
            return "alterarProd";
        }
    }

}
