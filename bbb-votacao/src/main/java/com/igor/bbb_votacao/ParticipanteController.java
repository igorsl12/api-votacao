package com.igor.bbb_votacao;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController // Avisa que essa classe vai responder requisições da internet
@RequestMapping("/participantes") // O endereço dessa porta
@CrossOrigin(origins = "*") // Permite que o nosso Front-end acesse essa porta depois
public class ParticipanteController {

    private final ParticipanteRepository repository;

    public ParticipanteController(ParticipanteRepository repository) {
        this.repository = repository;
    }

    @GetMapping // Quando alguém acessar a porta, este método é executado
    public List<Participante> listarTodos() {
        return repository.findAll(); // Vai no banco e traz todos os participantes
    }
}