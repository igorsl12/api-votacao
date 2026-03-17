package com.igor.bbb_votacao;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/votos")
@CrossOrigin(origins = "*") // Permite acesso do nosso futuro Front-end
public class VotoController {

    private final VotoRepository votoRepository;
    private final ParticipanteRepository participanteRepository;

    // Injetando as ferramentas que vão salvar no banco
    public VotoController(VotoRepository votoRepository, ParticipanteRepository participanteRepository) {
        this.votoRepository = votoRepository;
        this.participanteRepository = participanteRepository;
    }

    // A rota será algo como: POST http://localhost:8081/votos/1
    @PostMapping("/{participanteId}")
    public Voto registrarVoto(@PathVariable Integer participanteId) {
        // 1. Busca quem é o participante número 1 (ou 2)
        Participante p = participanteRepository.findById(participanteId).orElseThrow();
        
        // 2. Cria um voto novo e vincula a esse participante
        Voto novoVoto = new Voto();
        novoVoto.setParticipante(p);
        
        // 3. Salva no banco de dados!
        return votoRepository.save(novoVoto);
    }
    // Nova rota para o Front-end perguntar: "Quantos votos o participante X tem?"
    @GetMapping("/contagem/{participanteId}")
    public Long contarVotos(@PathVariable Integer participanteId) {
        return votoRepository.countByParticipanteId(participanteId);
    }
}