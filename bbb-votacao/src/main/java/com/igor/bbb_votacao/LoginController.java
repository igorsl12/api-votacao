package com.igor.bbb_votacao;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/login")
@CrossOrigin(origins = "*")
public class LoginController {

    private final UsuarioRepository repository;

    public LoginController(UsuarioRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<?> fazerLogin(@RequestBody Usuario dadosLogin) {
        // Vai no banco e procura alguém com esse e-mail e senha
        Optional<Usuario> usuarioEncontrado = repository.findByEmailAndSenha(dadosLogin.getEmail(), dadosLogin.getSenha());
        
        if (usuarioEncontrado.isPresent()) {
            // Pegamos o usuário encontrado
            Usuario usuarioSeguro = usuarioEncontrado.get();
            
            // PROTEÇÃO: Limpamos a senha antes de devolver para o front-end
            usuarioSeguro.setSenha(null); 
            
            // Devolve os dados do usuário (agora sem a senha, mas com o perfil ADMIN ou ELEITOR)
            return ResponseEntity.ok(usuarioSeguro);
        } else {
            // Se não achou, devolve erro 401 (Não Autorizado)
            return ResponseEntity.status(401).body("E-mail ou senha incorretos!");
        }
    }
}