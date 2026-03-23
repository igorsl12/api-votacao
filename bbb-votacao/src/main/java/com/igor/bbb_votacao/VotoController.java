package com.igor.bbb_votacao;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/votos")
@CrossOrigin(origins = "*")
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
        // Por padrão, visivelNoHistorico já deve ser true no modelo
        
        return votoRepository.save(novoVoto);
    }

    @GetMapping("/contagem/{participanteId}")
    public Long contarVotos(@PathVariable Integer participanteId) {
        return votoRepository.countByParticipanteId(participanteId);
    }

    // ==========================================
    // BUSCAR HISTÓRICO VISÍVEL DO USUÁRIO
    // ==========================================
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> buscarVotosDoUsuario(@PathVariable Integer usuarioId) {
        // BUSCAMOS TODOS, mas vamos filtrar apenas os que estão marcados como visíveis
        List<Voto> votosDoUsuario = votoRepository.findByUsuarioId(usuarioId);
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

        List<Map<String, Object>> historicoFormatado = votosDoUsuario.stream()
                .filter(v -> v.isVisivelNoHistorico()) // MÁGICA AQUI: Só passa o que for TRUE
                .map(voto -> {
                    Map<String, Object> dados = new TreeMap<>();
                    dados.put("idVoto", voto.getId());
                    dados.put("nomeCandidato", voto.getParticipante().getNome());
                    dados.put("dataHora", voto.getDataHora() != null ? voto.getDataHora().format(formatador) : "Data desconhecida");
                    return dados;
                }).collect(Collectors.toList());

        return ResponseEntity.ok(historicoFormatado);
    }

    // ==========================================
    // OCULTAR HISTÓRICO (Limpar sem deletar votos)
    // ==========================================
    @PutMapping("/usuario/{usuarioId}/limpar-historico")
    public ResponseEntity<?> esconderHistoricoUsuario(@PathVariable Integer usuarioId) {
        try {
            List<Voto> votos = votoRepository.findByUsuarioId(usuarioId);
            
            for (Voto v : votos) {
                v.setVisivelNoHistorico(false); // Marca como invisível
            }
            
            votoRepository.saveAll(votos); // Salva a alteração em lote
            return ResponseEntity.ok(Map.of("mensagem", "Histórico ocultado com sucesso!"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao processar solicitação.");
        }
    }

    // ==========================================
    // RESULTADOS (Continua contando TUDO, visível ou não)
    // ==========================================
    @GetMapping("/resultados")
    public ResponseEntity<?> obterResultados() {
        List<Participante> participantes = participanteRepository.findAll();
        List<Voto> todosVotos = votoRepository.findAll(); // Pega tudo do banco

        List<Map<String, Object>> ranking = new ArrayList<>();

        for (Participante p : participantes) {
            long totalVotos = todosVotos.stream()
                .filter(v -> v.getParticipante().getId().equals(p.getId()))
                .count(); // Aqui a contagem ignora se o voto está oculto ou não

            Map<String, Object> dados = new HashMap<>();
            dados.put("id", p.getId());
            dados.put("nome", p.getNome());
            dados.put("votos", totalVotos);
            ranking.add(dados);
        }

        return ResponseEntity.ok(ranking);
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
}