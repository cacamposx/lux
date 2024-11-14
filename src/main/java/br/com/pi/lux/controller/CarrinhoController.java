    package br.com.pi.lux.controller;

    import br.com.pi.lux.model.Produto;
    import br.com.pi.lux.repository.ProdutoRepository;
    import jakarta.servlet.http.HttpSession;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestParam;
    import org.springframework.web.bind.annotation.ResponseBody;
    import org.springframework.web.servlet.mvc.support.RedirectAttributes;

    import java.util.*;

    @Controller
    public class CarrinhoController {

        private double frete = 0.0;

        @Autowired
        private ProdutoRepository repository;

        @PostMapping("/adicionarCarrinho")
        public String adicionarCarrinho(@RequestParam("id") Integer id,
                                        @RequestParam("quantidade") int quantidade,
                                        HttpSession session,
                                        RedirectAttributes redirectAttributes) {
            Produto produto = repository.findById(id).orElse(null);

            if (produto != null) {
                // Obtém o carrinho da sessão, ou cria um novo se não existir
                List<ItemCarrinho> carrinho = (List<ItemCarrinho>) session.getAttribute("carrinho");
                if (carrinho == null) {
                    carrinho = new ArrayList<>();
                    session.setAttribute("carrinho", carrinho);
                }

                // Verifica se o produto já está no carrinho
                Optional<ItemCarrinho> itemExistente = carrinho.stream()
                        .filter(item -> item.getProduto().getIdProduto() == id)
                        .findFirst();

                if (itemExistente.isPresent()) {
                    itemExistente.get().setQuantidade(itemExistente.get().getQuantidade() + quantidade);
                } else {
                    ItemCarrinho novoItem = new ItemCarrinho(produto, quantidade);
                    carrinho.add(novoItem);
                }

                redirectAttributes.addFlashAttribute("mensagem", "Produto adicionado ao carrinho com sucesso!");
            } else {
                redirectAttributes.addFlashAttribute("mensagemErro", "Produto não encontrado!");
            }

            return "redirect:/carrinho";
        }

        @GetMapping("/carrinho")
        public String verCarrinho(HttpSession session, Model model) {
            // Obtém o carrinho da sessão
            List<ItemCarrinho> carrinho = (List<ItemCarrinho>) session.getAttribute("carrinho");

            if (carrinho == null) {
                carrinho = new ArrayList<>();
            }

            // Calcula o total do carrinho
            double totalCarrinho = carrinho.stream()
                    .mapToDouble(ItemCarrinho::getTotal)
                    .sum();

            model.addAttribute("carrinho", carrinho);
            model.addAttribute("totalCarrinho", totalCarrinho);
            model.addAttribute("frete", frete);
            return "carrinho";
        }

        @PostMapping("/carrinho/frete")
        public String escolherFrete(@RequestParam("frete") double freteEscolhido, RedirectAttributes redirectAttributes) {
            this.frete = freteEscolhido;
            redirectAttributes.addFlashAttribute("mensagem", "Frete escolhido: R$ " + freteEscolhido);
            return "redirect:/carrinho";
        }

        @PostMapping("/atualizarQuantidade")
        public @ResponseBody Map<String, Object> atualizarQuantidade(@RequestParam("id") Integer id,
                                                                     @RequestParam("quantidade") int quantidade,
                                                                     HttpSession session) {
            List<ItemCarrinho> carrinho = (List<ItemCarrinho>) session.getAttribute("carrinho");

            Map<String, Object> response = new HashMap<>();
            if (carrinho != null) {
                Optional<ItemCarrinho> itemCarrinho = carrinho.stream()
                        .filter(item -> item.getProduto().getIdProduto() == id)
                        .findFirst();

                if (itemCarrinho.isPresent()) {
                    itemCarrinho.get().setQuantidade(quantidade);
                    double subtotal = itemCarrinho.get().getTotal();
                    double totalCarrinho = carrinho.stream()
                            .mapToDouble(ItemCarrinho::getTotal)
                            .sum();
                    response.put("subtotal", subtotal);
                    response.put("total", totalCarrinho + frete);
                } else {
                    response.put("error", "Produto não encontrado no carrinho!");
                }
            } else {
                response.put("error", "Carrinho não encontrado!");
            }

            return response;
        }

        @PostMapping("/removerCarrinho")
        public @ResponseBody Map<String, Object> removerCarrinho(@RequestParam("id") Integer id, HttpSession session) {
            List<ItemCarrinho> carrinho = (List<ItemCarrinho>) session.getAttribute("carrinho");
            if (carrinho != null) {
                carrinho.removeIf(item -> item.getProduto().getIdProduto() == id);
                double totalCarrinho = carrinho.stream()
                        .mapToDouble(ItemCarrinho::getTotal)
                        .sum();

                Map<String, Object> response = new HashMap<>();
                response.put("total", totalCarrinho + frete);
                return response;
            }

            return Collections.singletonMap("error", "Carrinho não encontrado!");
        }

    }
