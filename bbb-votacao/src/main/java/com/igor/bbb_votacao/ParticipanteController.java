package com.igor.bbb_votacao;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/participantes")
@CrossOrigin(origins = "*") // <-- Essa linha é obrigatória!
public class ParticipanteController {

    private final ParticipanteRepository repository;
    private final VotoRepository votoRepository;

    // Injetamos os dois repositórios aqui
    public ParticipanteController(ParticipanteRepository repository, VotoRepository votoRepository) {
        this.repository = repository;
        this.votoRepository = votoRepository;
    }

    @GetMapping
    public List<Participante> listarTodos() {
        return repository.findAll();
    }

    // 1. CADASTRAR (Create)
    @PostMapping
    public Participante adicionar(@RequestBody Participante novo) {
        return repository.save(novo);
    }

    // 2. EDITAR (Update)
    @PutMapping("/{id}")
    public ResponseEntity<Participante> editar(@PathVariable Integer id, @RequestBody Participante dadosAtualizados) {
        Optional<Participante> existente = repository.findById(id);
        if (existente.isPresent()) {
            Participante p = existente.get();
            p.setNome(dadosAtualizados.getNome()); // Troca o nome
            return ResponseEntity.ok(repository.save(p)); // Salva a alteração
        }
        return ResponseEntity.notFound().build();
    }

    // 3. EXCLUIR (Delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        // Regra de Ouro: Apagar os votos antes de apagar o participante!
        votoRepository.deleteByParticipanteId(id);
        repository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}