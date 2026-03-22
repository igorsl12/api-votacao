package com.igor.bbb_votacao;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/configuracao")
@CrossOrigin(origins = "*")
public class ConfiguracaoController {

    private final ConfiguracaoRepository repository;

    public ConfiguracaoController(ConfiguracaoRepository repository) {
        this.repository = repository;
        
        // Magia: Se o banco estiver vazio, ele cria a configuração automaticamente ao iniciar!
        if (repository.count() == 0) {
            Configuracao config = new Configuracao();
            config.setId(1);
            config.setVotacaoAberta(true);
            repository.save(config);
        }
    }

    // Rota para consultar o status
    @GetMapping
    public Configuracao obterStatus() {
        return repository.findById(1).orElseThrow();
    }

    // Rota para o Admin apertar o botão
    @PostMapping("/alternar")
    public Configuracao alternarStatus() {
        Configuracao config = repository.findById(1).orElseThrow();
        
        // Inverte o valor (se tá true, vira false. Se tá false, vira true)
        config.setVotacaoAberta(!config.isVotacaoAberta()); 
        
        return repository.save(config);
    }
}