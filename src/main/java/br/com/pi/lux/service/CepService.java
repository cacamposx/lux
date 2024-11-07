package br.com.pi.lux.service;

import br.com.pi.lux.model.Endereco;
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

    public ViaCepResponse buscarEnderecoPorCep(String cep) {
        RestTemplate restTemplate = new RestTemplate();
        String url = UriComponentsBuilder.fromUriString(VIACEP_URL)
                .buildAndExpand(cep)
                .toUriString();
        ViaCepResponse response = restTemplate.getForObject(url, ViaCepResponse.class);

        // Log para verificar a resposta da API
        System.out.println("Resposta ViaCEP: " + response);

        return response;
    }


    public Endereco converterParaEndereco(ViaCepResponse viaCepResponse) {
        Endereco endereco = new Endereco();
        endereco.setCep(viaCepResponse.getCep());
        endereco.setLogradouro(viaCepResponse.getLogradouro());
        endereco.setComplemento(viaCepResponse.getComplemento());
        endereco.setBairro(viaCepResponse.getBairro());
        endereco.setCidade(viaCepResponse.getLocalidade());
        endereco.setUf(viaCepResponse.getUf());
        return endereco;
    }

    public static class ViaCepResponse {
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
            public String getCep() { return cep; }
            public void setCep(String cep) { this.cep = cep; }

            public String getLogradouro() { return logradouro; }
            public void setLogradouro(String logradouro) { this.logradouro = logradouro; }

            public String getComplemento() { return complemento; }
            public void setComplemento(String complemento) { this.complemento = complemento; }

            public String getBairro() { return bairro; }
            public void setBairro(String bairro) { this.bairro = bairro; }

            public String getLocalidade() { return localidade; }
            public void setLocalidade(String localidade) { this.localidade = localidade; }

            public String getUf() { return uf; }
            public void setUf(String uf) { this.uf = uf; }

    }
}


