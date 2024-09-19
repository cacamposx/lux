package br.com.pi.lux.controller;

import br.com.pi.lux.model.Produto;
import br.com.pi.lux.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AlterarProdutoController {

    @Autowired
    private ProdutoRepository repository;

    @GetMapping("/alterarProd")
    public String mostrarFormularioAlteracao(@RequestParam("idProduto") int idProduto, Model model) {
        Optional<Produto> produtoOptional = repository.findById(idProduto);
        if (produtoOptional.isPresent()) {
            model.addAttribute("produto", produtoOptional.get());

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
    public String alterarProduto(@RequestParam int idProduto,
                                 @RequestParam(required = false) String nome,
                                 @RequestParam(required = false) Double preco,
                                 @RequestParam(required = false) Integer quantidade,
                                 @RequestParam(required = false) String descricao,
                                 @RequestParam(required = false) Double avaliacao,
                                 @RequestParam(required = false) Boolean status,
                                 Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        boolean isEstoquista = false;

        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
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

            repository.save(produto);

            model.addAttribute("mensagem", "Produto alterado com sucesso!");
            return "redirect:/listarProd";
        } else {
            model.addAttribute("mensagem", "Produto não encontrado!");
            return "alterarProd";
        }
    }
}
