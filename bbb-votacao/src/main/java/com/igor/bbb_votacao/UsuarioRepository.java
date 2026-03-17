package com.igor.bbb_votacao;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    // O Spring é tão inteligente que ele entende o nome desse método e cria o SQL (SELECT) sozinho!
    Optional<Usuario> findByEmailAndSenha(String email, String senha);
    Optional<Usuario> findByEmail(String email);
}