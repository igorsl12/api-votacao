package com.igor.bbb_votacao;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioRepository repository;

    public UsuarioController(UsuarioRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrarUsuario(@RequestBody Usuario novoUsuario) {
        
        // 1. Verifica se o e-mail já existe no banco
        Optional<Usuario> existente = repository.findByEmail(novoUsuario.getEmail());
        
        if (existente.isPresent()) {
            // Se achou, devolve um Erro 400 (Bad Request)
            return ResponseEntity.status(400).body("Erro: Este e-mail já está cadastrado!");
        }
        
        // 2. Por segurança, forçamos que todo mundo que se cadastra pela tela é um ELEITOR
        novoUsuario.setPerfil("ELEITOR");
        
        // 3. Salva no banco e devolve o usuário criado
        Usuario salvo = repository.save(novoUsuario);
        return ResponseEntity.ok(salvo);
    }
}