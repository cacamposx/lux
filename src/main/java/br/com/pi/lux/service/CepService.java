package br.com.pi.lux.service;

import br.com.pi.lux.model.EnderecoEntrega;
import br.com.pi.lux.model.EnderecoFaturamento;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CepService {

    // Classe interna que representa a resposta da API ViaCep
    public static class ViaCepResponse {
        private String cep;
        private String logradouro;
        private String complemento;
        private String bairro;
        private String localidade;
        private String uf;

        // Getters e setters
        public String getCep() {
            return cep;
        }

        public void setCep(String cep) {
            this.cep = cep;
        }

        public String getLogradouro() {
            return logradouro;
        }

        public void setLogradouro(String logradouro) {
            this.logradouro = logradouro;
        }

        public String getComplemento() {
            return complemento;
        }

        public void setComplemento(String complemento) {
            this.complemento = complemento;
        }

        public String getBairro() {
            return bairro;
        }

        public void setBairro(String bairro) {
            this.bairro = bairro;
        }

        public String getLocalidade() {
            return localidade;
        }

        public void setLocalidade(String localidade) {
            this.localidade = localidade;
        }

        public String getUf() {
            return uf;
        }

        public void setUf(String uf) {
            this.uf = uf;
        }
    }

    private final RestTemplate restTemplate;

    public CepService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;  // Injeção do RestTemplate
    }

    public ViaCepResponse buscarEnderecoPorCep(String cep) {
        // Remover hífen caso o CEP esteja nesse formato
        cep = cep.replace("-", "");
        String viaCepUrl = "https://viacep.com.br/ws/" + cep + "/json/";
        System.out.println("Consultando CEP: " + viaCepUrl);  // Log para verificar a URL

        try {
            // Requisição direta com o retorno deserializado para ViaCepResponse
            ViaCepResponse viaCepResponse = restTemplate.getForObject(viaCepUrl, ViaCepResponse.class);

            // Verifica se a resposta foi recebida corretamente
            if (viaCepResponse != null && viaCepResponse.getCep() != null && !viaCepResponse.getCep().isEmpty()) {
                return viaCepResponse;  // Retorna a resposta diretamente
            } else {
                System.out.println("CEP não encontrado ou resposta vazia.");
                return null;
            }
        } catch (Exception e) {
            System.out.println("Erro ao buscar o CEP: " + e.getMessage());
            e.printStackTrace();  // Exibe a stack trace para ajudar a diagnosticar o erro
            return null;
        }
    }

    // Método para validar se o CEP é válido (formato XXXXXXXX ou XXXXX-XXX)
    public boolean isValidCep(String cep) {
        if (cep != null) {
            cep = cep.replace("-", "");  // Remover o hífen caso exista
            boolean isValid = cep.matches("\\d{8}");  // Valida 8 dígitos consecutivos
            System.out.println("Verificando CEP: " + cep + " | Valido: " + isValid);  // Log para depuração
            return isValid;
        }
        return false;
    }


    // Converte a resposta do ViaCep para um objeto EnderecoEntrega
    public EnderecoEntrega converterParaEndereco(ViaCepResponse viaCepResponse) {
        EnderecoEntrega enderecoEntrega = new EnderecoEntrega();
        enderecoEntrega.setCep(viaCepResponse.getCep());
        enderecoEntrega.setLogradouro(viaCepResponse.getLogradouro());
        enderecoEntrega.setComplemento(viaCepResponse.getComplemento());
        enderecoEntrega.setBairro(viaCepResponse.getBairro());
        enderecoEntrega.setCidade(viaCepResponse.getLocalidade());
        enderecoEntrega.setUf(viaCepResponse.getUf());
        return enderecoEntrega;
    }

    // Método para converter a resposta do ViaCEP em um EnderecoFaturamento
    public EnderecoFaturamento converterParaEnderecoFaturamento(ViaCepResponse viaCepResponse) {
        EnderecoFaturamento enderecoFaturamento = new EnderecoFaturamento();

        // Preenchendo os dados de EnderecoFaturamento com as informações da resposta do ViaCEP
        enderecoFaturamento.setCep(viaCepResponse.getCep());
        enderecoFaturamento.setLogradouro(viaCepResponse.getLogradouro());
        enderecoFaturamento.setNumero(""); // O número pode ser preenchido posteriormente
        enderecoFaturamento.setComplemento(viaCepResponse.getComplemento());
        enderecoFaturamento.setBairro(viaCepResponse.getBairro());
        enderecoFaturamento.setCidade(viaCepResponse.getLocalidade());
        enderecoFaturamento.setUf(viaCepResponse.getUf());

        return enderecoFaturamento;
    }



}
