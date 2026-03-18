package com.igor.bbb_votacao;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.time.format.DateTimeFormatter;
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

    // ========================== 
    // HISTÓRICO COM DATA E HORA
    // ========================== 
    @GetMapping("/historico")
    public ResponseEntity<Map<String, Long>> obterHistoricoVotos() {
        
        List<Voto> todosVotos = votoRepository.findAll();

        // MUDANÇA AQUI: Adicionamos o dia (dd) e o mês (MM) antes da hora!
        // O resultado agora será algo como: "18/03 - 14:00"
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM - HH:00");

        Map<String, Long> historicoAgrupado = todosVotos.stream()
                .filter(voto -> voto.getDataHora() != null) 
                .collect(Collectors.groupingBy(
                        voto -> voto.getDataHora().format(formatador),
                        TreeMap::new, // Mantém a ordem cronológica
                        Collectors.counting() 
                ));

        return ResponseEntity.ok(historicoAgrupado);
        
    }
    
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> buscarVotosDoUsuario(@PathVariable Integer usuarioId) {
        
        // Busca no banco apenas os votos desse usuário específico
        List<Voto> votosDoUsuario = votoRepository.findByUsuarioId(usuarioId);

        // Formata os dados para enviar um JSON limpo pro Front-end
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
}