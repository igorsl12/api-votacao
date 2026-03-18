package com.igor.bbb_votacao;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioRepository repository;

    public UsuarioController(UsuarioRepository repository) {
        this.repository = repository;
    }

    // 1. CADASTRAR USUÁRIO
    @PostMapping
    public ResponseEntity<?> cadastrarUsuario(@RequestBody Usuario novoUsuario) {
        novoUsuario.setPerfil("ELEITOR");

        boolean emailJaExiste = repository.findAll().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(novoUsuario.getEmail()));

        if (emailJaExiste) {
            return ResponseEntity.status(400).body("E-mail já cadastrado");
        }

        try {
            Usuario usuarioSalvo = repository.save(novoUsuario);
            return ResponseEntity.ok(usuarioSalvo);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno no servidor");
        }
    }

    // 2. ATUALIZAR NOME DO USUÁRIO
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarNomeUsuario(@PathVariable Integer id, @RequestBody Usuario usuarioAtualizado) {
        try {
            Usuario usuarioExistente = repository.findById(id).orElseThrow();
            usuarioExistente.setNome(usuarioAtualizado.getNome());
            repository.save(usuarioExistente);
            
            return ResponseEntity.ok("Nome atualizado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Erro ao atualizar o usuário.");
        }
    }

    // ==========================================
    // 3. NOVA ROTA: BUSCAR USUÁRIO POR ID
    // ==========================================
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarUsuarioPorId(@PathVariable Integer id) {
        try {
            // Vai no banco e pega o usuário completo
            Usuario usuario = repository.findById(id).orElseThrow();
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Usuário não encontrado");
        }
    }
}