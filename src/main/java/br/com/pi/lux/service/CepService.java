package br.com.pi.lux.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class CepService {

    private static final String VIACEP_URL = "https://viacep.com.br/ws/{cep}/json/";

    public boolean isValidCep(String cep) {
        RestTemplate restTemplate = new RestTemplate();
        String url = UriComponentsBuilder.fromUriString(VIACEP_URL)
                .buildAndExpand(cep)
                .toUriString();

        try {
            ViaCepResponse response = restTemplate.getForObject(url, ViaCepResponse.class);
            return response != null && response.getCep() != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static class ViaCepResponse {
        private String cep;
        private String logradouro;
        private String complemento;
        private String bairro;
        private String localidade;
        private String uf;
        private String ibge;
        private String gia;
        private String ddd;
        private String siafi;

        // Getters and setters
        public String getCep() {
            return cep;
        }

        public void setCep(String cep) {
            this.cep = cep;
        }

        // Add other getters and setters as needed
    }
}
