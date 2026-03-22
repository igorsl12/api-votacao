package com.igor.bbb_votacao;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList; // Importante para o /resultados
import java.util.HashMap;   // Importante para o /resultados
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/votos")
@CrossOrigin(origins = "*") // Isso resolve o erro de CORS!
public class VotoController {

    private final VotoRepository votoRepository;
    private final ParticipanteRepository participanteRepository;
    private final UsuarioRepository usuarioRepository;

    public VotoController(VotoRepository votoRepository, ParticipanteRepository participanteRepository, UsuarioRepository usuarioRepository) {
        this.votoRepository = votoRepository;
        this.participanteRepository = participanteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/{participanteId}/{usuarioId}")
    public Voto registrarVoto(@PathVariable Integer participanteId, @PathVariable Integer usuarioId) {
        Participante p = participanteRepository.findById(participanteId).orElseThrow();
        Usuario u = usuarioRepository.findById(usuarioId).orElseThrow();
        
        Voto novoVoto = new Voto();
        novoVoto.setParticipante(p);
        novoVoto.setUsuario(u); 
        
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

    @GetMapping("/historico")
    public ResponseEntity<Map<String, Long>> obterHistoricoVotos() {
        List<Voto> todosVotos = votoRepository.findAll();
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM - HH:00");

        Map<String, Long> historicoAgrupado = todosVotos.stream()
                .filter(voto -> voto.getDataHora() != null) 
                .collect(Collectors.groupingBy(
                        voto -> voto.getDataHora().format(formatador),
                        TreeMap::new, 
                        Collectors.counting() 
                ));

        return ResponseEntity.ok(historicoAgrupado);
    }
    
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> buscarVotosDoUsuario(@PathVariable Integer usuarioId) {
        List<Voto> votosDoUsuario = votoRepository.findByUsuarioId(usuarioId);
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

        List<Map<String, Object>> historicoFormatado = votosDoUsuario.stream().map(voto -> {
            Map<String, Object> dados = new TreeMap<>();
            dados.put("idVoto", voto.getId());
            dados.put("nomeCandidato", voto.getParticipante().getNome());
            dados.put("dataHora", voto.getDataHora() != null ? voto.getDataHora().format(formatador) : "Data desconhecida");
            return dados;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(historicoFormatado);
    }

    // ==========================================
    // ROTA QUE FALTAVA: RESULTADOS EM TEMPO REAL (Para os gráficos)
    // ==========================================
    @GetMapping("/resultados")
    public ResponseEntity<?> obterResultados() {
        // Pega todos os participantes e todos os votos do banco
        List<Participante> participantes = participanteRepository.findAll();
        List<Voto> todosVotos = votoRepository.findAll();

        List<Map<String, Object>> ranking = new ArrayList<>();

        // Conta os votos de cada um
        for (Participante p : participantes) {
            long totalVotos = todosVotos.stream()
                .filter(v -> v.getParticipante().getId().equals(p.getId()))
                .count();

            Map<String, Object> dados = new HashMap<>();
            dados.put("id", p.getId());
            dados.put("nome", p.getNome());
            dados.put("votos", totalVotos);
            ranking.add(dados);
        }

        return ResponseEntity.ok(ranking);
    }
}