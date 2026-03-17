package com.igor.bbb_votacao;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/votos")
@CrossOrigin(origins = "*")
public class VotoController {

    private final VotoRepository votoRepository;
    private final ParticipanteRepository participanteRepository;
    private final UsuarioRepository usuarioRepository; // Adicionamos o garçom de usuários

    // Atualizamos o construtor para injetar os 3 repositórios
    public VotoController(VotoRepository votoRepository, ParticipanteRepository participanteRepository, UsuarioRepository usuarioRepository) {
        this.votoRepository = votoRepository;
        this.participanteRepository = participanteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Agora a rota pede os dois IDs: em QUEM votou e QUEM votou
    // Exemplo: POST http://localhost:8081/votos/1/2 (Usuário 2 votando no Participante 1)
    @PostMapping("/{participanteId}/{usuarioId}")
    public Voto registrarVoto(@PathVariable Integer participanteId, @PathVariable Integer usuarioId) {
        
        Participante p = participanteRepository.findById(participanteId).orElseThrow();
        Usuario u = usuarioRepository.findById(usuarioId).orElseThrow(); // Busca quem é o eleitor
        
        Voto novoVoto = new Voto();
        novoVoto.setParticipante(p);
        novoVoto.setUsuario(u); // Amarramos o voto ao usuário!
        
        return votoRepository.save(novoVoto);
    }

    @GetMapping("/contagem/{participanteId}")
    public Long contarVotos(@PathVariable Integer participanteId) {
        return votoRepository.countByParticipanteId(participanteId);
    }

    @GetMapping("/porcentagem/{participanteId}")
    public Double calcularPorcentagem(@PathVariable Integer participanteId) {
        long totalVotosGerais = votoRepository.count(); 
        if (totalVotosGerais == 0) return 0.0;
        
        long votosDoParticipante = votoRepository.countByParticipanteId(participanteId);
        return (votosDoParticipante * 100.0) / totalVotosGerais;
    }
}